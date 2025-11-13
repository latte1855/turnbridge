
**Turnbridge（電子發票 Turnkey 轉拋整合系統）— 自動化代理人與整合腳本作業指南（含驗收清單與映射表）**

> 讀者：Codex、自動化代理人（Agent/RPA）、CI 腳本、整合工程師
> 依據：`SRS v1.0`、`DECISION_LOG_v0.3`、`webhook_spec.md`、`CAPACITY_AND_ARCHITECTURE.md`、`turnkey_system_interview_draft_v0.3`、`turnkey_review_findings_v0.1`
> 核心原則：**Webhook-first**、**單檔 999 明細不拆單**、**MIG 4.x 以 F/G 新制為唯一輸出**
> 語言：繁體中文（程式碼與文件均需中文註解；Java 類/方法/欄位務必撰寫 Javadoc）

---

## 0. 快速結論（三件事）

1. **MIG 新制為主**：輸出 XML 一律用 `F0401/F0501/F0701`、`G0401/G0501`；若上游仍送 A/B/C/D 舊制，**CSV 需保留原始行與 legacyType**，並映射為新制欄位再上傳。
2. **單檔 999 明細，不可拆單**：若第 999 筆落在某張發票中間，**整張移到下一檔**。
3. **Webhook-first**：必備 HMAC 驗簽 Webhook 端點；WebSocket/Email 僅備援。

---

## 1. 代理人（Agent）與後端（Backend）職責

* **Agent（客戶端）**

  * 蒐集 POS/ERP/3S 資料 → **本地前置檢核** → 輸出 **CSV/ZIP + MD5**。
  * 舊制 → 新制欄位映射（見 §6）。
  * 呼叫後端 API 上傳、記錄 `importId`、處理重試與離線補送。

* **Backend（主機端）**

  * 驗證/入庫 → 轉檔（XML 以 F/G 新制）→ 投遞 Turnkey → 解析回饋 → 狀態更新 + Webhook。

---

## 2. 上傳規格（E0501 / Invoice）

**共同：** `multipart/form-data`，欄位：`file` (`*.csv|*.zip`)、`md5`（16 進位小寫）、`encoding`（預設 UTF-8；E0501 可 BIG5）、`profile`（可選，如加油站卷=250）。
**分檔：** 以**明細行數**計 `<= 999`；**不得拆單**；需附分割序號（splitSeq）。
**多租戶：** 由 OAuth2 Client/Scope 與 Header 指示租戶；RLS 於 DB 端強制隔離。

### 2.1 E0501（配號）重點

* 規則：`Ban` 8 碼；`Period` 偶數雙月；`Track` 2 碼；`FromNo/ToNo` 卷內連號、**00/50 起，49/99 迄**。
* 加油站卷：`RollSize = 250`；仍需卷內連號。
* 常見錯誤：`E0501_ROLL_START_INVALID`、`E0501_RANGE_OVERLAP`。

### 2.2 Invoice（發票/折讓）重點

* **新制訊息**：`F0401` 開立、`F0501` 作廢、`F0701` 註銷、`G0401` 折讓開立/傳送、`G0501` 折讓作廢。
* 金額驗證：`sum(items.amount) + tax == total`；稅率 0/5% 等；小數精度依 MIG。
* 舊制上傳：CSV 需含 `legacyType`（A0401/C0401…）與 `rawLine`（完整原始行）欄位以留存。

---

## 3. 後端 API（Agent 最常用）

| 用途         | Method     | Path                             | 重點                               |
| ---------- | ---------- | -------------------------------- | -------------------------------- |
| 上傳 E0501   | `POST`     | `/api/v1/upload/e0501`           | 回 `importId`                     |
| 上傳 Invoice | `POST`     | `/api/v1/upload/invoice`         | 支援 ZIP；999 切檔；回 `importId`       |
| 查匯入        | `GET`      | `/api/v1/imports/{importId}`     | `status/successCount/errorCount` |
| 查回饋        | `GET`      | `/api/v1/turnkey/messages`       | 期間/代碼/狀態過濾                       |
| 重送         | `POST`     | `/api/v1/invoices/{id}/resend`   | 人工重送需審核（雙人）                      |
| Webhook    | `POST/GET` | `/api/v1/webhooks` / `{id}/logs` | 註冊/查投遞紀錄                         |

---

## 4. Webhook（HMAC，重試，DLQ）

* Header：`X-Turnbridge-Signature: sha256=<base64>`（對 **body** 做 HMAC-SHA256）。
* 重試：1m → 5m → 15m；失敗 → DLQ。
* 事件：`upload.completed`、`invoice.status.updated`、`turnkey.feedback.daily-summary`。

**Payload 範例**

```json
{
  "event":"invoice.status.updated",
  "timestamp":"2025-11-12T09:00:00Z",
  "data":{"tenantId":"TEN-001","invoiceNo":"AB12345678","status":"UPLOADED"},
  "retry":0
}
```

---

## 5. 轉檔與 Turnkey（系統內部，Agent 僅需理解）

* 排程：每 **5 分鐘**掃描待轉檔。
* 輸出 XML：**僅** `F0401/F0501/F0701/G0401/G0501`；XSD 驗證、壓縮、簽章。
* 投遞：置檔至 Turnkey 上拋目錄；由 Turnkey 排程上傳 MOF。
* 回饋：監聽 ACK/ERROR → 更新狀態 → Webhook。
* 重送：指數退避；超限轉人工佇列（**二階段審核**）。

---

## 6. **舊制 → 新制 CSV 欄位映射表（重點）**

> 實際欄位以 SRS/MIG4.1 欄位表為準；此處列出**最小可行欄位**。
> 上游仍用舊制（A/B/C/D）時，Agent 必須**保留原始行**（`rawLine`）與 `legacyType`，同時提供新制欄位。

### 6.1 發票（開立/作廢/註銷）：A/C 系 → **F 系**

| 舊制            | 新制            | 對應/說明                                               |
| ------------- | ------------- | --------------------------------------------------- |
| A0401 / C0401 | **F0401**（開立） | 欄位 1:1 對應，部分命名/列舉值依 MIG4.1 新名詞；補上 `BuyerId`/載具欄位標準化 |
| A0501 / C0501 | **F0501**（作廢） | 需帶 **作廢原因碼**、原發票關聯鍵（發票號/日期）                         |
| C0701         | **F0701**（註銷） | 需帶註銷理由與原始關聯鍵，與 F0501 不同語意                           |

**最小欄位（F0401 範例）**

| 欄位                          | 說明          | 例                         |
| --------------------------- | ----------- | ------------------------- |
| `Type`                      | 固定 `F0401`  | F0401                     |
| `InvoiceNo`                 | 字軌+8 碼      | AB12345678                |
| `DateTime`                  | ISO8601     | 2025-11-12T08:35:00+08:00 |
| `BuyerId`                   | 統編/載具       | 12345678                  |
| `Total` / `Tax` / `TaxType` | 金額與稅別       | 1000.00 / 50.00 / 5%      |
| `Items[]`                   | 名稱/數量/單價/金額 | …                         |
| `legacyType`                | 舊制型別        | C0401                     |
| `rawLine`                   | 舊制原始行       | <整行內容>                    |

### 6.2 折讓證明單：B/D 系 → **G 系**

| 舊制            | 新制               | 對應/說明                  |
| ------------- | ---------------- | ---------------------- |
| B0401 / D0401 | **G0401**（開立/傳送） | 對應折讓開立（或平台轉送）；需含關聯發票資訊 |
| B0501 / D0501 | **G0501**（作廢）    | 需含原折讓單鍵與作廢原因           |

**最小欄位（G0401 範例）**

| 欄位                       | 說明         | 例              |
| ------------------------ | ---------- | -------------- |
| `Type`                   | 固定 `G0401` | G0401          |
| `AllowanceNo`            | 折讓單號       | AL202511120001 |
| `RefInvoiceNo`           | 關聯原發票      | AB12345678     |
| `Total` / `Tax`          | 金額/稅       | 200.00 / 10.00 |
| `Reason`                 | 折讓原因       | 退貨             |
| `legacyType` / `rawLine` | 舊制保留       | B0401 / <整行>   |

---

## 7. **Agent 驗收 Checklist（交付前必驗）**

**A. 功能面**

* [ ] 可將上游資料切檔為 `<= 999` **明細**且**不拆單**；分割序號正確遞增。
* [ ] E0501 巻規則：**00/50 起、49/99 迄**；加油站客製 **250/卷** 可切換。
* [ ] 舊制檔案：CSV 內 **保留 `legacyType` 與 `rawLine`**，並正確映射 **F/G 新制欄位**。
* [ ] 上傳 API：`multipart` + `md5` 參數；成功回 `importId`。
* [ ] 斷網重送：離線快取成功（IndexedDB/本機檔），恢復網路自動補送（指數退避）。

**B. 效能/穩定**

* [ ] 1MB/999 筆檔平均驗證 < 2 秒（含 CSV 解析 + MD5）。
* [ ] 連續上傳 100 檔無明顯退化；失敗自動重試策略正確。

**C. 安全/多租**

* [ ] OAuth2 Client → JWT 正常；僅能存取本租戶。
* [ ] Webhook HMAC 驗證通過；非 2xx 觸發重試與 DLQ。

**D. 可觀測性**

* [ ] 代理端落地日誌：記錄 `fileName/md5/importId/httpStatus/retryCount/result`。
* [ ] 每日輸出 `YYYYMMDD_agent_upload_report.jsonl` 以供對帳。

**E. 文件與程式碼規範**

* [ ] **所有程式碼具繁體中文註解**；Java 類/方法/欄位均有 **Javadoc**（描述/參數/回傳/例外）。
* [ ] **變更已回寫文件**（見 §9）並提交 PR（含影響分析與對應決策編號）。

---

## 8. 常見錯誤碼（Agent 需識別）

| 代碼                     | 說明       | 處置           |
| ---------------------- | -------- | ------------ |
| `MD5_MISMATCH`         | 檔案校驗失敗   | 重新計算 MD5 後再送 |
| `AMOUNT_MISMATCH`      | 金額/稅額不一致 | 修正資料重送       |
| `ILLEGAL_MESSAGE_TYPE` | 不支援型別    | 轉為 F/G 新制    |
| `E0501_RANGE_OVERLAP`  | 號段重疊     | 調整起訖或走人工審核   |
| `TENANT_FORBIDDEN`     | 無權限      | 檢查憑證與租戶設定    |

---

## 9. **文件與程式碼規範（必遵守）**

### 9.1 繁體中文註解與 JavaDoc（強制）

* **所有語言**：需要**繁體中文**註解闡明業務語意、邊界條件與錯誤處理。
* **Java**：Class/Method/Field 一律撰寫 **Javadoc**，涵蓋：用途、參數、回傳、例外、商業規則與引用規格（MIG/SRS 條次）。
* 範例（Java）：

```java
/**
 * 轉檔服務（MIG 4.1）— 將內部發票資料產生對應的 F/G 新制 XML。
 * <p>注意：單檔 999 明細，整張不可拆；輸出一律採新制。</p>
 * @param invoice 發票聚合根
 * @return 產生後的 XML 檔案路徑
 * @throws XmlGenerateException 當欄位不符 XSD 或金額不一致
 */
public Path generateNewSchemaXml(@Nonnull Invoice invoice) { ... }
```

### 9.2 變更即回寫（Documentation-as-Code）

* 任一變更 **必須同步更新**下列文件並提交同一 PR：

  * `SRS.md`（或 `docs/SRS/` 各章）
  * `DECISION_LOG.md`（新增/修改決策，標註 `DEC-xxx`）
  * `webhook_spec.md`（如影響事件或簽章）
  * 相關教學與 `AGENTS.md`（如影響上傳或映射）
* PR 模板需包含：**變更摘要 / 影響範圍 / 對應決策 ID / 測試證據**。

---

## 10. 觀測與日誌（Agent 側建議格式）

**upload log（JSONL）**

```json
{"ts":"2025-11-12T08:35:02+08:00","tenant":"TEN-001","file":"invoice_20251112_0001.zip","md5":"0cc1...","importId":"imp_...42","http":200,"retry":0,"result":"OK"}
```

**webhook log（JSONL）**

```json
{"ts":"2025-11-12T08:36:10+08:00","event":"invoice.status.updated","invoiceNo":"AB12345678","signature_ok":true,"http":200}
```

---

## 11. 實作指令（示意）

```bash
# 切檔（<=999 明細）、輸出 ZIP 與 MD5
turnbridge-pack invoices.csv --max-lines 999 --zip --md5 -o out/

# 上傳（multipart）
curl -fS -H "Authorization: Bearer $TOKEN" \
  -F "file=@out/invoice_20251112_0001.zip" \
  -F "md5=$(cat out/invoice_20251112_0001.zip.md5)" \
  -F "encoding=UTF-8" \
  https://turnbridge.example.com/api/v1/upload/invoice
```

---

## 12. 版本與相容性

* **輸出唯一**：Turnkey XML 僅 F/G 新制。
* **向前相容**：接受舊制 A/B/C/D，CSV 需留 `legacyType`+`rawLine`。
* **檔名慣例**：`<type>_<yyyymmdd>_<seq>.csv|zip`；`seq` 連號便於追蹤。
* **語系編碼**：預設 UTF-8；E0501 可 BIG5。


## 13. 文件治理（現況與規劃）

### 13.1 `docs/` 現況（必要文件索引）

| 範疇 | 目錄 | 代表文件 | 內容摘要 / 負責人 |
| --- | --- | --- | --- |
| 需求與決策 | `docs/requirements/` | `DECISION_LOG_v0.3.md`、`CAPACITY_AND_ARCHITECTURE.md`、`webhook_spec.md`、`turnkey_review_findings_v0.1.md`、`turnkey_system_interview_draft_v0.3.md` | 決策、容量、Webhook、訪談摘要；由 SA/PO 維護 |
| 系統規格 | `docs/spec/` | `turnbridge-srs-v1.0.md`、`openapi-turnbridge-v1.yml`、`turnbridge-rfp-v1.0.md`、`mig-message-type-mapping.md`、`mig4.0-changelog.md`、`mig4.1-changelog.md`、`turnkey-mig41-integration.md` | SRS、API、MIG 變更、RFP；由 架構師/Tech Lead 維護 |
| 整合指南 | `docs/integration/` | `README.md`（索引）＋待搬遷 Turnkey/Webhook 深入說明 | 整合工程師；依 DEC-012 搬遷 |
| 運維流程 | `docs/operations/` | `README.md`（索引）＋ Runbook（Monitoring/Incident/Manual Resend） | SRE/Ops；對應 DEC-006、DEC-011 |
| Turnkey 接軌 | `docs/turnkey/` | `MIG4.1.pdf`、`Turnkey使用說明書 v3.9.pdf` | 官方 MIG 與 Turnkey 原廠手冊；由 Turnkey 專案小組維護 |
| 舊系統教材 | `docs/legacy-system-docs/` | `舊系統_import-spec.md`、`舊系統_E0501_template.md`、`舊系統_Invoice_template.md`、`舊系統_BUSINESS_FLOW.md`、`舊系統_QUICK_REFERENCE.md` | 舊版操作與模板；作為轉換參考 |
| JDL 模型 | `docs/jdl/` | `m0-upload-core.jdl` | JHipster/資料模型種子 |

> 其他臨時筆記請移至 `workspace/` 或個人目錄，避免進入 `docs/` 正式結構。

### 13.2 未來文件佈局與責任切分

```
docs/
  README.md              # 快速導覽（新增），指向下列子目錄
  requirements/          # 決策、容量、外部約束；PO/SA 負責
  spec/                  # SRS、API、MIG 對應；架構師負責
  integration/           # 與第三方互通（含 Turnkey 詳細流程、Webhook 實例）；整合工程師負責
  operations/            # Runbook、監控、事件流程；SRE/Ops 負責
  legacy/                # 舊系統參考（原 `legacy-system-docs/`）
  assets/                # 共用圖檔、Mermaid 來源、匯出的 PDF
```

* **`docs/README.md` 已就緒**：新增或移除文件時務必同步更新該檔與本節。
* **`integration/`**：索引 README 已建，請將 `docs/turnkey/`、`webhook_spec.md` 中的實作/測試內容搬遷並標記進度；`turnkey/` 僅存官方 PDF。
* **`operations/`**：索引 README 已建，Runbook（Monitoring/Incident/Manual Resend 等）需依 §5 規範補齊。
* **治理流程**：任一程式/流程變更需檢查對應目錄是否需同步更新（搭配 §9.2「Documentation-as-Code」）。

> 目錄與 README 已建；新增子章節時仍應維持「目的 / 典型文件 / 主要負責人」格式並保持 10 行以內。

**本文件完**
