# 人工重送與審核流程（Manual Resend SOP）

> 依據：`AGENTS.md §5`、`turnbridge-srs-v1.0.md §5`、DEC-006（手動轉檔與號段分配 UI）  
> 目的：在自動重送超過上限或客戶要求人工操作時，提供一致的流程，確保稽核與責任歸屬。

---

## 1. 適用情境

1. Turnkey 回饋為 `ERROR` 且系統自動重送已達上限。  
2. 客戶要求快速補送（例如已修正資料）。  
3. Webhook 投遞進入 DLQ，需手動再送。  
4. 審核人員確認人工轉檔、手動配號等操作。

---

## 2. 角色與權限

| 角色 | 權限 | 備註 |
| --- | --- | --- |
| 操作員（Operator） | 建立重送請求、填寫原因、附上證據 | 不得審核自己的請求 |
| 審核員（Reviewer） | 審核/駁回重送請求 | 必須與操作員不同帳號 |
| 維運（Ops） | 監控佇列、執行批次重送腳本 | 審核通過後才可執行 |

---

## 3. 流程步驟

1. **建立申請**  
   - 於 Portal 或 Ops 工單系統建立「Manual Resend」申請。  
   - 必填：`import_id` 或 `invoice_id`、失敗原因、重送方式（Turnkey/Webhook）、附件（如修正後 XML）。  
   - 系統產生 `manual_resend_request_id`，狀態為 `PENDING_REVIEW`。

2. **審核**  
   - 審核員檢視申請，確認資料完整、原因合理。  
   - 選擇 `APPROVE` 或 `REJECT`，並留下備註。  
   - 審核通過後狀態改為 `APPROVED`，等待執行。

3. **執行**  
   - Ops 於後台選擇執行方式：  
     - **Turnkey**：重新產生 XML → 放入 `/INBOX/manual/<date>/` → 觸發 Turnkey Pickup。  
     - **Webhook**：從 DLQ 撈出 payload，依序重送並記錄結果。  
   - 執行結果（成功/失敗、時間、md5）回寫 `manual_resend_request`。

4. **結案**  
   - 成功：狀態標記 `COMPLETED`，並於 `AuditLog` 紀錄。  
   - 失敗：狀態改為 `FAILED`，並可建立新申請或升級 Incident。

---

## 4. Turnkey 重送細節

1. 驗證資料：確認 `Invoice` 狀態與最新修改時間，避免使用過期資料。  
2. 重新產 XML：透過內部工具或 API 產出 F/G XML，檢查 XSD。  
3. 置放目錄：  
   ```
   /INBOX/manual/<YYYYMMDD>/<tenant>/<importId>/FG0401_<seq>.xml
   ```
4. 記錄 `xml_path`、`md5` 至 `manual_resend_history`。  
5. 監控 Turnkey 回饋，確認狀態從 `ERROR` 轉為 `ACKED` 或輸出新的錯誤碼。

---

## 5. Webhook 重送細節

1. 從 DLQ 撈取特定 `delivery_id` 或租戶範圍。  
2. 驗證 Webhook 端點目前可用（可 `HEAD` / `GET` 健康檢查）。  
3. 使用工具（curl/腳本）重新 POST payload，並重算簽章。  
4. 若再次失敗，更新 `attempts` 與 `last_error`，並評估是否需要通知客戶或停用 Webhook。  
5. 成功後，標記對應 DLQ 項目為 `RESOLVED`。

---

## 6. 稽核要求

- 每個手動操作都需有 `request_id`、`approver`、`executor`、`timestamp`、`reason`。  
- 系統須保留至少一年操作紀錄以供稽核。  
- 每月匯總手動重送次數、原因分類，回饋給產品/開發檢討。

---

## 7. 工具建議

- `turnbridge-cli resend turnkey --import <id>`：產 XML 並推送到 `/INBOX`。  
- `turnbridge-cli resend webhook --delivery <id>`：從 DLQ 重送特定事件。  
- 若 CLI 尚未完成，可使用 Postman / curl 腳本，並確保記錄於工單系統。

> 若流程或工具有變更，請同步更新 `DECISION_LOG` 與本檔。
