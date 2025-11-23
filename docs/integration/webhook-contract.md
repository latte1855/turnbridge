# Webhook 契約規格（Contract Reference）

> 目的：提供對外 Webhook 整合的正式契約文件，供 API/SDK/客戶端對照。  
> 來源：`docs/requirements/webhook_spec.md`（摘要）、`turnbridge-srs-v1.0.md §7.2`、`openapi-turnbridge-v1.yml`。  
> 範圍：註冊 API、事件定義、Header/簽章、錯誤處理、測試要求。

---

## 1. 註冊與管理 API

### 1.1 建立 Webhook

```
POST /api/v1/webhooks
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "url": "https://client.example.com/hooks/invoice",
  "events": ["upload.completed", "invoice.status.updated"],
  "secret": null,
  "description": "Main webhook endpoint"
}
```

| 欄位 | 說明 | 備註 |
| --- | --- | --- |
| `url` | HTTPS endpoint | 必填；僅接受 443/8443 |
| `events` | 事件陣列 | 至少一項，見 §2 |
| `secret` | HMAC 秘鑰 | 可留空，由系統產生並僅顯示一次 |
| `description` | 描述 | 選填 |

回應：201，body 含 `id`、`secret`（若由系統產生）、`createdAt`。

### 1.2 查詢 / 更新 / 停用

| API | 用途 | 備註 |
| --- | --- | --- |
| `GET /api/v1/webhooks` | 列出租戶 Webhook | 支援 event 過濾 |
| `GET /api/v1/webhooks/{id}` | 取得單一 Webhook | 不回傳 secret |
| `PATCH /api/v1/webhooks/{id}` | 更新 url/events/描述 | 更新 secret 需重新簽章 |
| `DELETE /api/v1/webhooks/{id}` | 停用 | 實作為軟刪除 |

---

## 2. 事件定義

| 事件 | 說明 | 常見欄位 |
| --- | --- | --- |
| `upload.completed` | 匯入完成（成功/失敗筆數） | `import_id`、`success_count`、`error_count` |
| `invoice.status.updated` | 發票狀態改變（例如 ACK/ERROR） | `invoice_no`、`status`、`normalized_message_type`、`mof_code`、`tb_code`、`tb_category`、`can_auto_retry`、`recommended_action`、`source_layer`、`source_code`、`source_message`、`result_code`、`legacy_type`、`import_id`、`turnkey_message_id` |
| `turnkey.feedback.daily-summary` | Turnkey 回饋日報 | `date`、`expected`、`ack_count`、`error_count` |
| `invoice.manual.issued` | 人工建立發票 | `invoice_no`、`operator` |
| `invoice.manual.cancelled` | 人工作廢 | `invoice_no`、`operator` |
| `webhook.delivery.failed` | （選用）系統通知某 Webhook 已進 DLQ | `failed_delivery_id`、`webhook_endpoint_id`、`event_failed`、`attempts`、`last_error`、`dlq_reason` |

> 如需新增事件，須更新本檔、`AGENTS.md`、`DECISION_LOG`，並發佈給客戶。

---

## 3. Payload & Header

### 3.1 共通 Header

| Header | 說明 |
| --- | --- |
| `Content-Type: application/json` | 永遠為 JSON |
| `X-Turnbridge-Event` | 事件名稱 |
| `X-Turnbridge-Delivery-Id` | UUID；供去重 |
| `X-Turnbridge-Timestamp` | Unix epoch 秒 |
| `X-Turnbridge-Signature` | `sha256=<hex(HMAC(secret, payload))>` |

### 3.2 範例 Payload（upload.completed）

```json
{
  "delivery_id": "d949bd44-...-e712",
  "event": "upload.completed",
  "timestamp": "2025-11-12T08:36:10Z",
  "tenant_id": "TEN-001",
  "import_id": "imp_20251112_0001",
  "success_count": 245,
  "error_count": 5,
  "file_name": "invoice_20251112_0001.zip"
}
```

### 3.3 範例 Payload（invoice.status.updated）

```json
{
  "delivery_id": "06f50bc6-2c18-40d0-ab0d-296a4ad1d7b9",
  "event": "invoice.status.updated",
  "timestamp": "2025-11-12T09:00:00Z",
  "tenant_id": "TEN-001",
  "data": {
    "invoice_no": "AB12345678",
    "status": "ACKED",
    "normalized_message_type": "F0401",
    "import_id": 321,
    "mof_code": "E200",
    "tb_code": "TB-5003",
    "tb_category": "PLATFORM.DATA_AMOUNT_MISMATCH",
    "can_auto_retry": false,
    "recommended_action": "FIX_DATA",
    "source_layer": "PLATFORM",
    "source_code": "E200",
    "source_message": "Tax amount mismatch",
    "result_code": "9",
    "message": "Tax amount mismatch",
    "turnkey_message_id": 98,
    "legacy_type": "C0401"
  }
}
```

### 3.4 Schema 對照

| 事件 | Schema（`openapi-turnbridge-v1.yml`） | 重要欄位 | 說明 |
| --- | --- | --- | --- |
| `upload.completed` | `UploadCompletedPayload` | `import_id`、`success_count`、`error_count` | 來源 `ImportFile`，`error_count>0` 需附 `importStatusUrl` |
| `invoice.status.updated` | `InvoiceStatusPayload` | `invoice_no`、`status`、`normalized_message_type`、`mof_code`、`tb_code`、`tb_category`、`can_auto_retry`、`recommended_action`、`source_layer`、`source_code`、`source_message`、`result_code`、`legacy_type`、`import_id`、`turnkey_message_id` | `status ∈ {UPLOADED, ACKED, ERROR, VOIDED}` |
| `turnkey.feedback.daily-summary` | `TurnkeySummaryPayload` | `date`、`expected`、`ack_count`、`error_count` | 對應 Turnkey 回饋日報 |
| `invoice.manual.issued` | `InvoiceManualPayload` | `operator`, `invoice_no` | 由人工 UI 觸發 |
| `webhook.delivery.failed` | `WebhookDeliveryFailedPayload` | `delivery_id`、`webhook_id`、`attempts` | 可選事件 |

> 若 schema 有新增欄位，請同步更新 `openapi-turnbridge-v1.yml` 與本表格。

---

## 4. 簽章與驗證

1. 系統使用 `secret` 產生 HMAC：`signature = hex(HMAC_SHA256(secret, raw_body))`。  
2. Header 格式：`X-Turnbridge-Signature: sha256=<signature>`。  
3. 客戶端需：
   - 取得 Header、body；使用同一 secret 計算 HMAC。  
   - 比較（建議使用 constant-time compare）；若不同，回傳 401。  
   - 建議驗證 `X-Turnbridge-Timestamp` 不超過 5 分鐘以防重放。

---

## 5. Retry / DLQ 合約

| 次數 | 延遲 | 描述 |
| --- | --- | --- |
| 0（首次） | 即時 | 失敗則排入重試佇列 |
| 1 | +1 分鐘 | |
| 2 | +5 分鐘 | |
| 3 | +15 分鐘 | 仍失敗 → DLQ，發 `webhook.delivery.failed`（若訂閱） |

DLQ 項目保留至少 7 天；客戶可透過 UI 查詢並請求重送，或由 Ops 依 `manual-resend.md` 執行。

---

## 6. 錯誤與錯誤碼

### 6.1 HTTP 狀態處理

| HTTP 狀態 | 系統行為 |
| --- | --- |
| 2xx | 設為成功，結束重試 |
| 4xx（非 429/408） | 視為客戶邏輯錯誤；如持續發生，建議客戶修正或暫停 webhook |
| 5xx / 429 / 408 | 依重試策略進行，最終進 DLQ |

### 6.2 Webhook 專用錯誤碼

| 錯誤碼 | 說明 | 建議處置 |
| --- | --- | --- |
| `WEBHOOK_SIGNATURE_INVALID` | 客戶端簽章檢查失敗 | 回傳 401，記錄於 Delivery Log，建議客戶檢查 secret |
| `WEBHOOK_ENDPOINT_UNREACHABLE` | 連線逾時或 DNS 失敗 | 系統自動重試；Ops 可通知客戶 |
| `WEBHOOK_PAYLOAD_SCHEMA_ERROR` | 客戶端回應 400 並指出欄位問題 | 檢查 schema 版本或欄位是否新增 |
| `WEBHOOK_DLQ_EXCEEDED` | 重試超過上限進入 DLQ | 依 `manual-resend.md` 流程重送 |

系統會在 Delivery Log 中記錄 `status_code`、`latency_ms`、`last_error` 以及上述錯誤碼，供 UI/API 查詢。

---

## 7. 測試與驗收需求

1. 客戶端應提供測試端點以驗證 HMAC 與 retry。  
2. 每次部署前，使用 Postman / Newman script 驗證以下情境：  
   - 200 OK（簽章正確）  
   - 401（簽章錯誤）  
   - 500 → DLQ  
3. 重大版更需提供 Mock server（例如 `webhook.site` 或內部 stub）供 CI 自動測試。

---

## 8. 版本與相容性

- 目前版本：`v1`（對應 `openapi-turnbridge-v1.yml`）。  
- 版本升級策略：新增欄位採「可選」方式，重大破壞性變更需發佈 `v2` endpoint。  
- `secret` 變更：由系統或客戶端觸發，舊 secret 可設定 N 分鐘 grace period。

---

## 9. 參考

- `docs/requirements/webhook_spec.md`（完整規格）  
- `docs/integration/turnkey-webhook.md`（流程、監控與 retry 實務）  
- `AGENTS.md §4`、`SRS §7`（Webhook 行為摘要）
