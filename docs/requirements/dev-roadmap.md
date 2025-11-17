# **Turnbridge 開發排程與模組拆解（Dev Roadmap v1.1）**

> 適用：Turnbridge（加值中心）後端 + Portal + Agent
> 目的：提供開發前的「完整計畫、Phase 里程碑、模組分工、任務拆解、橫切議題」
> 更新者：ChatGPT（依據專案資料自動生成）
> 版本：v1.1（整併 MIG4.1／DECISION_LOG／Agent／Webhook 最新規格）

---

# **0. Phase 總覽**

| Phase               | 建議期間                    | 主要交付物（Deliverables）                                                     |
| ------------------- | ----------------------- | ----------------------------------------------------------------------- |
| **Phase 1（W1–W4）**  | 匯入 & Normalize 基礎       | 上傳 API、匯入流程、ABCD→FG Normalize、ImportFile/Log、RLS、Tenant Skeleton        |
| **Phase 2（W5–W8）**  | Turnkey 整合（雙向）+ Webhook | XML Builder、XSD 驗證、/INBOX 上拋、/OUTBOX feedback parser、Webhook HMAC + DLQ |
| **Phase 3（W9–W12）** | Portal/手動作業/監控落地        | Portal UI、Manual Resend + 審核、Webhook 管理、Monitoring + Ops Tools          |
| **Phase 4（W13+）**   | E2E & 壓測與演練             | Newman E2E、容量壓測、DR/備援演練、全文件更新                                           |

---

## **最新進度（Phase 1 / 目前 Sprint）**

- ✅ `POST /api/upload/invoice|e0501` 已可驗證 SHA-256 並建立 `ImportFile`，同時把 `legacyType`/`rawLine` 保存（對應 DEV-001、DEV-002）。
- ✅ `NormalizationService` 已上線：上傳後立即解析 CSV/ZIP → 寫入 `Invoice/InvoiceItem`、錯誤寫入 `ImportFileLog`，金額/訊息別驗證依 `docs/AGENTS_MAPPING_v1.md`、`docs/turnkey/mig41/CSV_FG_MAPPING.md`（DEV-003、DEV-004）。
- ⚠️ 待辦：補齊 `com.asynctide.turnbridge.my.upload` 底下的 Normalize 成功/失敗測試案例，驗證 ProblemDetail、ImportFileLog（本回合完成後更新 DEV-003 驗收）。

---

# **1. 模組拆解（Modules Overview）**

## **1.1. 匯入 / Normalize**

| 模組                    | 子項                                                          | 依賴     | 輸入/產出                       |
| --------------------- | ----------------------------------------------------------- | ------ | --------------------------- |
| **上傳/驗證**             | `POST /upload/{type}`、**僅允許單一 CSV**、SHA-256、ImportFile、999 不拆單、agent metadata | 無      | API Spec、Agent 測試集          |
| **ImportFileItem**    | *每一行* 上傳資料都落地，含 `rawData/hash/result` 與錯誤摘要，後續下載結果檔直接引用；使用者可選擇多筆批次下載，系統再自動壓縮為 ZIP | 上傳/驗證  | 原始 CSV、結果 CSV/ZIP         |
| **ImportFileItemError** | 一行資料可對應多個欄位錯誤（欄位名、錯誤碼、訊息、嚴重度），供 UI 詳細檢視／下載結果時附上 | ImportFileItem | 多欄位錯誤報表、結果檔附註            |
| **Normalize 引擎**      | ABCD → FG、欄位映射、錯誤碼、rawLine/legacyType 保存；<br/>逐行處理，成功才寫 Invoice，失敗僅更新 ImportFileItem + ImportFileItemError | 上傳/驗證  | AGENTS_MAPPING_v1.md、MIG4.1 |
| **RLS / 多租戶（Tenant）** | tenant_id、角色（加值中心/總公司/分公司）、RLS Policy                       | Domain | RLS Policy、Migration        |

> **交易規範：** Normalize 進行時不得因單筆失敗回滾整批 ImportFile。ImportFileItem 必須先寫入後再進一步產生 Invoice/InvoiceItem；就算全部失敗，也能透過 ImportFile + ImportFileItem 查詢與下載錯誤結果。

> **ImportFileLog 角色：** 自本版起作為「批次事件/稽核紀錄」，僅記錄 `UPLOAD_RECEIVED`、`NORMALIZE_SUMMARY`、`NORMALIZE_FAILURE` 等事件（含層級、訊息、細節 JSON）；逐行錯誤改由 ImportFileItem + ImportFileItemError 儲存。

---

## **1.2. Domain / DB**

| 模組                         | 內容                             | 依賴        |
| -------------------------- | ------------------------------ | --------- |
| Invoice / Items            | FG 格式 normalized JSON、AssignNo | Normalize |
| ImportFile / ImportFileLog | 原始匯入紀錄、錯誤細節                    | 上傳/驗證     |
| AssignNo / InvoiceStatus   | 號段分配、按 MIG4.1 SRS              | Invoice   |
| Auditing / 二階段審核           | Manual Resend 審核               | Portal    |

---

## **1.3. Turnkey 整合**

| 模組                  | 子項                                              | 輸入/產出             |
| ------------------- | ----------------------------------------------- | ----------------- |
| **XML Builder**     | FG XML 生成、XSD 驗證、簽章、/INBOX 投遞                   | MIG4.1, XSD       |
| **Turnkey Monitor** | 監控 /INBOX、/OUTBOX、錯誤檔案                          | turnkey-flow.yaml |
| **Feedback Parser** | 解析 SummaryResult / ACK / ERROR，更新 InvoiceStatus | MIG4.1、/OUTBOX    |

---

## **1.4. Webhook（事件驅動）**

| 模組                   | 子項                                | 輸入/產出           |
| -------------------- | --------------------------------- | --------------- |
| Webhook Deliver      | HMAC-SHA256、Retry、DLQ、DeliveryLog | webhook_spec.md |
| Webhook Registration | URL/Secret/Events 註冊管理            | Portal          |
| Webhook Dashboard    | Delivery Log 查詢、手動重送              | Portal          |

---

## **1.5. Portal + Manual Tools**

| 模組              | 子項                            | 依賴              |
| --------------- | ----------------------------- | --------------- |
| 匯入查詢            | ImportFile / Log / Invoice 查詢 | Domain          |
| Manual Resend   | 二階段審核、UI流程                    | Feedback Parser |
| Manual AssignNo | 指定號段、錯誤更正                     | Domain          |
| Webhook 管理      | enable/disable、secret rotate  | Webhook         |

---

## **1.6. Monitoring / Ops / Infra**

| 模組                  | 子項                                               |
| ------------------- | ------------------------------------------------ |
| Prometheus 指標       | import count、turnkey throughput、webhook delivery |
| Turnkey Healthcheck | inbound/outbound queue readable、permissions      |
| Incident Tools      | 手動重送 XML、修復 broken invoice、DLQ 移轉                |

---

## **1.7. 橫切議題（Cross-cutting Concerns）**

### 必須明列為獨立模組（否則開發容易漏掉）

| 模組                | 子項                                                 |
| ----------------- | -------------------------------------------------- |
| **Security**      | API JWT/Token、Webhook HMAC、憑證管理、secret encryption  |
| **Documentation** | SRS、AGENTS.md、mapping、webhook_spec、turnkey-flow 更新 |
| **Testing / TDD** | Mapper UT、XML Builder UT、Webhook UT、E2E（Newman）    |
| **Capacity / DR** | 壓測、備援策略、災難演練                                       |

---

# **2. 任務拆解（Task Breakdown）**

以下是「可直接放進 Jira」的任務設計。

---

## **Phase 1 任務（W1–W4）**

| Task        | 描述                                                  | 估工 | 完成定義                                    |
| ----------- | --------------------------------------------------- | -- | --------------------------------------- |
| **DEV-000** | 多租戶 + RLS Skeleton                                  | 3d | tenant_id 套入 Import/Invoice / 查詢 RLS 生效 |
| **DEV-001** | `POST /api/v1/upload/{type}` 上傳 API + ImportFile 建立 | 5d | Newman/agent 測試成功；回傳 importId           |
| **DEV-002** | 匯入大檔處理（999 不拆單）+ CSV 驗證                             | 3d | >999 回錯誤；邏輯符合 DEC-009                   |
| **DEV-003** | Normalize Engine：ABCD → FG 映射、錯誤碼                   | 6d | MIG4.1 覆蓋；normalized JSON 入 DB          |
| **DEV-004** | ImportFileLog / NormalizeLog                        | 3d | 詳細錯誤碼可查；rawLine 保存                      |
| **DEV-005** | Domain/ERD 建立 + Migration                           | 3d | ERD/ Migration 與 SRS §6 一致              |

---

## **Phase 2 任務（W5–W8）**

| Task        | 描述                                    | 估工 | 完成定義                            |
| ----------- | ------------------------------------- | -- | ------------------------------- |
| **DEV-006** | XML Builder：FG XML + XSD 驗證           | 5d | XSD validate pass；產生 /INBOX     |
| **DEV-007** | Turnkey Pickup：投遞流程 + mock-turnkey 整合 | 3d | mock-turnkey 收到 XML；回 ACK       |
| **DEV-008** | **Turnkey Feedback Parser**（重要新增）     | 5d | 解析 ACK/ERROR → InvoiceStatus 更新 |
| **DEV-009** | Webhook Deliver：HMAC + Retry + DLQ    | 4d | newman-smoke/timeout case pass  |
| **DEV-010** | Webhook Registration + Secret Rotate  | 3d | UI 可設定 URL/secret/events        |

---

## **Phase 3 任務（W9–W12）**

| Task        | 描述                                            | 估工 | 完成定義                        |
| ----------- | --------------------------------------------- | -- | --------------------------- |
| **DEV-011** | Portal：Import / Invoice 查詢                    | 5d | 權限：總公司→分公司；分公司互不可見          |
| **DEV-012** | Manual Resend（二階段審核）                          | 5d | AuditLog、審核 UI 完整           |
| **DEV-013** | Manual AssignNo                               | 4d | 號段補分配 + 審核                  |
| **DEV-014** | DeliveryLog 查詢 + Webhook Resend               | 4d | 可查、可重送、DLQ 清理               |
| **DEV-015** | Monitoring Dashboard（Prometheus + Turnkey 健檢） | 4d | 所有指標齊全；healthcheck API pass |

---

## **Phase 4 任務（W13+）**

| Task        | 描述                              | 估工 | 完成定義                              |
| ----------- | ------------------------------- | -- | --------------------------------- |
| **DEV-016** | Newman E2E 測試（全流程）              | 4d | 正常/錯誤/部分成功 scenario 齊全            |
| **DEV-017** | 匯入/轉檔 壓測                        | 3d | TPS/Day capacity 達成 CAPACITY spec |
| **DEV-018** | DR / 備援演練                       | 3d | 故障復原腳本；DB/設定皆可還原                  |
| **DEV-019** | 文件同步更新（SRS/AGENTS/webhook_spec） | 2d | 全部文件同版號；可交付                       |

---

# **3. Stakeholder 與會議（Meetings）**

| 會議                 | 目的           | 參與者                        | 頻率         |
| ------------------ | ------------ | -------------------------- | ---------- |
| 週開發同步              | DEV 進度、阻塞排除  | Backend/Integration/Ops/PM | 1/w        |
| Turnkey/憑證小組       | 憑證、mock/實機切換 | Integration/Infra          | 2/w（或必要時）  |
| Webhook Payload 審查 | 事件結構、錯誤碼確認   | Backend/Frontend/客戶        | Phase2 完成前 |
| E2E 測試檢討           | 問題清單、行動項     | 全體                         | Phase4     |

---

# **4. 里程碑檢查（Milestones）**

## **M1（Phase 1 完成）**

* 匯入/Normalize 流程可跑
* ABCD→FG 對照可查
* rawLine、legacyType、錯誤碼完整
* RLS/多租戶查詢驗證通過
* DB 結構穩定

## **M2（Phase 2 完成）**

* XML Builder 100% XSD valid
* Turnkey 模擬/實機皆能收/回 ACK
* Feedback Parser 更新 DB 狀態
* Webhook 推播成功率 > 99%（含 DLQ）

## **M3（Phase 3 完成）**

* Portal 可查：Import、Invoice、Webhook
* Manual Resend + AssignNo 二階段審核
* Webhook 管理 console
* Monitoring dashboard 可用

## **M4（Phase 4 完成）**

* 全流程 E2E 通過
* 壓測結果達 CAPACITY 標準
* DR 演練通過
* 全部文件（SRS/AGENTS/mapping/webhook）更新完成

---

# **5. 後續若有新需求**

若遇：

* MIG 4.2 更新
* 新 webhook 事件
* 客戶新 CSV Profile
* Turnkey 主機作業流程變更

→ 請新增 DECISION_LOG（例如 DEC-013：Roadmap 調整） 並同步更新本文件。

---
