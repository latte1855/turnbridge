# 決策紀錄 (Decision Log) - 電子發票 Turnkey 系統重構

> **文件版本**：v0.3  
> **日期**：2025-11-12  
> **簽核者**：業主、技術主管、架構師  
> **用途**：記錄所有關鍵決策、理由與影響

---

## 📋 決策摘要表

| 決策 ID | 決策項 | 決定結果 | 優先級 | 狀態 | 影響範圍 |
|---------|--------|----------|--------|------|---------|
| DEC-001 | 公開 API 第一階段支援 | 是 | 🔴 High | ✅ Approved | Backend, SDK, 文件 |
| DEC-002 | Event Trigger 內建機制 | Spring 內建事件；Webhook-first 擴充點 | 🔴 High | ✅ Approved | 架構、Redis |
| DEC-003 | 行動 App 支援 | 是（PWA 優先，原生 App 後續） | 🟡 Medium | ✅ Approved | Frontend, 離線邏輯 |
| DEC-004 | 總公司/分公司層級隔離 | 是，但子公司間互不可見 | 🟡 Medium | ✅ Approved | RBAC, 資料隔離 |
| DEC-005 | 第三方代理機制 | 暫不支援，預留擴充 | 🟢 Low | ⏳ Deferred | 後期需求 |
| DEC-006 | 手動轉檔與號段分配 UI | 允許，二階段審核 | 🔴 High | ✅ Approved | 運維、Audit |
| DEC-007 | Redis 快取與多租戶 + RLS 行級安全 | 是（Redis；Shared schema + RLS 初期） | 🔴 High | ✅ Approved | 基礎設施、DB |
| DEC-008 | 客戶群體 | 以加油站為主要客戶 | 🔴 High | ✅ Confirmed | 容量規劃、UX |
| DEC-009 | 批次檔筆數上限 | 每檔 999 筆；單張發票不可跨檔拆分 | 🔴 High | ✅ Approved | 匯入/切檔、驗證 |
| DEC-010 | Backend / Agent 分工 | Backend 轉檔交換；Agent 整理上傳 | 🟡 Medium | ✅ Approved | 架構、維運 |
| DEC-011 | Turnkey 整合方式 | Backend 產生 XML → Turnkey 排程上拋；回饋解析入庫 | 🔴 High | ✅ Approved | 整合、排程 |
| DEC-012 | `docs/` 基礎結構 | 清理舊檔、鎖定目錄責任；`turnkey/` 僅存官方 PDF，legacy 檔案改以 `舊系統_*` 前綴 | 🟡 Medium | ✅ Approved | 文件、治理 |

---

## 🎯 決策詳情（新增/更新

### DEC-007：Redis 快取與多租戶 + RLS 行級安全性

**背景**：  
系統需支援 500 租戶、日均 10 萬發票、尖峰可達 30–50 萬筆。  
為兼顧效能與維運成本，採用 **Shared schema + tenant_id** 設計，並以 PostgreSQL **Row-Level Security (RLS)** 實現租戶隔離。  
同時導入 Redis 提供快取與分散鎖功能。

**決策**：  
✅ 採用 **Shared schema + tenant_id + RLS** 為初期架構。  
未來視業務成長或法遵需求，升級路徑如下：  
- Phase 3：Schema-per-tenant（中隔離度、較易備份）  
- Phase 4：DB-per-tenant（高隔離度、適用法人級客戶）

---

#### 🧩 實作要點

1. **共用連線池與 DB 使用者**
   - 所有租戶共用同一組 DB 帳號與 Connection Pool（HikariCP）。  
   - 每次請求進入時，由後端在該連線上設定 session 變數：
     ```sql
     SELECT set_config('app.tenant_id', '123', true);
     ```

2. **RLS Policy 定義**
   ```sql
   ALTER TABLE invoices ENABLE ROW LEVEL SECURITY;

   CREATE OR REPLACE FUNCTION app_can_read(row_tenant_id bigint)
   RETURNS boolean LANGUAGE sql STABLE AS $$
     SELECT
       current_setting('app.is_admin', true) = 'true'
       OR row_tenant_id = current_setting('app.tenant_id')::bigint
       OR (
         current_setting('app.allowed_tenant_ids', true) IS NOT NULL
         AND row_tenant_id = ANY (
           string_to_array(current_setting('app.allowed_tenant_ids'), ',')::bigint[]
         )
       );
   $$;

   CREATE POLICY tenant_policy ON invoices
   USING (app_can_read(tenant_id));
   ```

3. **應用端設定（Spring Boot）**
   ```java
   // 一般租戶
   jdbcTemplate.execute("SELECT set_config('app.tenant_id', '" + tenantId + "', true)");

   // 管理者（查看所有租戶資料）
   jdbcTemplate.execute("SELECT set_config('app.is_admin', 'true', true)");

   // 總公司（可查看子公司資料）
   String csvIds = "101,102,103";
   jdbcTemplate.execute("SELECT set_config('app.allowed_tenant_ids', '" + csvIds + "', true)");
   ```

4. **優點**
   - 安全：DB 層強制隔離，ORM/原生 SQL 均受保護。  
   - 彈性：支援管理者與總公司跨租戶檢視。  
   - 可審計：所有 `app.is_admin` 與 `app.allowed_tenant_ids` 的設定都記錄於 `AUDIT_LOG`。

5. **Redis 配置**
   - Redis Sentinel 三節點高可用。
   - 功能用途：  
     - 驗證規則快取（減少 DB 查詢）  
     - 分散鎖（配號號段防競爭）  
     - Rate limiting / Retry counter  
     - Webhook 任務暫存

---

### DEC-009：批次檔筆數上限與切檔規則
**背景**：舊系統為效能與相容性設計，每檔限制 999 筆；單張發票可能含多明細。  
**決定**：沿用 999 筆上限；當最後一張發票之明細使筆數超出上限時，**整張發票移至下一檔**，不可拆分。  
**影響**：
- 匯入服務需具備自動切檔能力（伺服器端）或於 API 層拒收並回報分割建議。  
- ImportFile/ImportFileLog 需關聯「分割序號」。

### DEC-010：Backend / Agent 職責分工
**背景**：降低 Backend 因客戶多樣性導致的複雜度，並支援無 IT 能力客戶。  
**決定**：
- **Agent**：收集原始資料、前置檢核、產生合規 CSV/ZIP+MD5、上傳 Backend。  
- **Backend**：接收、驗證、轉檔（MIG 4.1 XML）、與 Turnkey 目錄互動、回饋解析與通知。  
**影響**：需規劃 Agent 部署/更新與版本管理；Backend 仍保留 API 直接上傳能力。

### DEC-011：Turnkey 整合方式
**背景**：Turnkey 安裝於業主機房且既有排程。  
**決定**：Backend 生成 XML 置於 Turnkey 指定目錄，由 Turnkey 既有排程上拋 MOF；回饋（ACK/ERROR）由 Backend 解析更新狀態並推播。  
**影響**：需要雙向目錄與權限設定；排程與錯誤補償（重送）機制納入 SRS 與測試。

### DEC-012：`docs/` 基礎結構治理
**背景**：原 `docs/` 內含多份過期或重複檔案（如舊版 `api.yml`、樣板 YAML）；新版規範需確保指向唯一來源並在 AGENTS/SRS 內說明。  
**決定**：  
1. 清理冗餘檔案，僅保留目前有效的規格：`requirements/` 與 `spec/` 維持既有 Markdown/YAML，`turnkey/` 限縮為官方 PDF（`MIG4.1.pdf`、`Turnkey使用說明書 v3.9.pdf`），`legacy-system-docs/` 全數檔案加上 `舊系統_` 前綴。  
2. 在 `AGENTS.md §13` 與 `SRS §1.3` 登記最新清單與責任歸屬，並預留 `integration/`、`operations/` 未來目錄。  
**影響**：部署/維運指引皆需引用新的檔案名稱；CI 應檢查文件引用是否與表列一致。  
**進度**：2025-11-13 建立 `docs/README.md`、`docs/integration/README.md`、`docs/operations/README.md`；2025-11-14 完成 `docs/integration/{turnkey-webhook.md,webhook-contract.md,turnkey-flow.yaml,turnkey-flow.md,test-scripts.md,e2e-scenarios.md,postman/*,scripts/newman-smoke.sh,ci-newman.md}`、`deploy/turnkey/ansible/*`（引用 YAML 並可透過 `scripts/check-turnkey-flow.sh` 驗證）、`.github/workflows/{newman-smoke,docs-quality}.yml` 與 `scripts/check-webhook-consistency.py`，並在 `AGENTS`/`SRS` 連動更新；`docs/operations/{monitoring.md,incident-playbook.md,manual-resend.md,turnkey-healthcheck.md}` 亦補齊決策來源。  
**後續**：將 Ansible playbook 納入正式 IaC Repo、在 CI 中提供合法 token/base URL 運行 smoke workflow、導入文件引用檢核流程。

---

## 📊 決策跟蹤表（更新）

| 決策 ID | 決策項 | 優先級 | 實施 Phase | 負責單位 | 截止日期 | 進度 |
|---------|--------|--------|------------|----------|----------|------|
| DEC-001 | 公開 API | 🔴 High | Phase 1 | Backend Team | 2025-03-31 | ⏳ In Progress |
| DEC-002 | Event Trigger | 🔴 High | Phase 1-2 | Arch / Backend | 2025-04-30 | ⏳ In Progress |
| DEC-003 | 行動 App PWA | 🟡 Medium | Phase 2 | Frontend Team | 2025-06-30 | 📋 Planned |
| DEC-004 | 總公司/分公司 | 🟡 Medium | Phase 2 | Backend / DB | 2025-06-30 | 📋 Planned |
| DEC-005 | 代理機制 | 🟢 Low | TBD | -- | TBD | ❌ Deferred |
| DEC-006 | 手動轉檔 UI | 🔴 High | Phase 2 | Frontend / Ops | 2025-06-30 | 📋 Planned |
| DEC-007 | Redis + Shared Schema + RLS | 🔴 High | Phase 1-2 | Infra / DB | 2025-05-31 | ⏳ In Progress |
| DEC-008 | 加油站容量模型 | 🔴 High | Phase 1 | Capacity Planning | 2025-01-31 | ✅ Confirmed |
| DEC-009 | 999 筆上限與切檔 | 🔴 High | Phase 1 | Backend | 2025-02-15 | 📋 Planned |
| DEC-010 | Backend/Agent 分工 | 🟡 Medium | Phase 2 | Arch / Backend | 2025-03-15 | 📋 Planned |
| DEC-011 | Turnkey 整合 | 🔴 High | Phase 1 | Arch / Backend | 2025-02-28 | ⏳ In Progress |
| DEC-012 | `docs/` 結構治理 | 🟡 Medium | Phase 1 | DocOps / Arch | 2025-02-15 | ✅ Completed（清檔＋目錄＋整合/運維初版）；待 YAML/測試腳本 |

---

## 📝 簽核欄位

| 角色 | 姓名 | 簽名 | 日期 |
|------|------|------|------|
| 業主 | -- | -- | -- |
| 技術主管 | -- | -- | -- |
| 架構師 | -- | -- | -- |
| PM | -- | -- | -- |

---

（文件完）
