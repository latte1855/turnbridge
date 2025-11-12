# Webhook 規格片段（摘要）

> 目的：提供開發者與測試團隊一個可立即使用的 webhook 註冊/驗證/推播範本，作為 SRS 與 OpenAPI 的基礎。

## 1. 設計要點（高階）
- 推播方式：以 HTTP POST 為主，使用 TLS（HTTPS）傳送。
- 驗證：採用 HMAC-SHA256 (Header: `X-EG-Signature`)，註冊時由客戶提供 `secret`；payload 使用 UTF-8 JSON，簽章計算方式為 HMAC(secret, payload)。
- Retry 與 DLQ：若推送失敗（非 2xx 回應或連線錯誤），依指數退避重試 3 次（例如 1m、5m、15m），仍失敗則送入 Dead Letter Queue，並透過 Email/運維告警通知客戶。
- 事件種類（event types）：upload.completed、turnkey.feedback.daily-summary、invoice.status.updated、invoice.manual.issued、invoice.pdf.ready 等。
- Idempotency：每次推播含唯一 delivery_id；接收端應以 delivery_id 做去重處理。

## 2. Webhook 註冊 API（OpenAPI fragment）
- POST /api/webhooks
  - Request body (application/json):
    - url: string (https URL)
    - events: string[] (allowed events)
    - secret: string (optional; 建議由系統產生並提供給客戶)
    - description: string
  - Response: 201 Created
    - body: { id, url, events, secret (only once), createdAt }

## 3. 推播時 HTTP header 與範例
- Required headers
  - Content-Type: application/json
  - X-EG-Event: <event-type>
  - X-EG-Delivery-Id: <uuid>
  - X-EG-Signature: sha256=<hex-hmac>
  - X-EG-Timestamp: <unix epoch seconds>

簽章計算範例（伺服器端）
- signature = hex(HMAC_SHA256(secret, payload))
- header: `X-EG-Signature: sha256=...`

## 4. 範例 payloads
- upload.completed
{
  "delivery_id": "uuid-...",
  "event": "upload.completed",
  "timestamp": 1700000000,
  "file_id": 12345,
  "uploader": {
    "tenant_id": "T-001",
    "company_id": "C-001"
  },
  "counts": {
    "received": 250,
    "accepted": 245,
    "rejected": 5
  }
}

- turnkey.feedback.daily-summary
{
  "delivery_id": "uuid-...",
  "event": "turnkey.feedback.daily-summary",
  "timestamp": 1700000000,
  "date": "2025-11-11",
  "counts": {
    "expected": 1234,
    "mof_received": 1234
  },
  "details": [ /* optional per-invoice summary items */ ]
}

- invoice.status.updated
{
  "delivery_id": "uuid-...",
  "event": "invoice.status.updated",
  "timestamp": 1700000000,
  "invoice_id": "INV-2025-...",
  "status": "ACKED|ERROR|VOIDED",
  "mof_code": "...",
  "message": "..."
}

## 5. Retry / DLQ policy (建議)
- 1st retry: after 1 minute
- 2nd retry: after 5 minutes
- 3rd retry: after 15 minutes
- 若仍未成功，標記為 FAILED 並送入 DLQ（保留 payload + metadata），由運維或客戶手動檢查/重送。
- 所有嘗試應被記錄（delivery log），可由 UI 查詢並支持手動重送。

## 6. 可觀察性與監控
- 每次 delivery 在系統紀錄：delivery_id、webhook_id、url、http_status、latency、attempts、last_error。
- 指標：success_rate、mean_latency、dlq_count、avg_attempts
- 建議：整合 Prometheus / Grafana 監控，並在 SLA 低於閾值時發自動告警。

## 7. Acceptance tests (BDD 範例)
- Scenario: successful delivery
  - Given a registered webhook for event "upload.completed"
  - When system emits upload.completed with counts matching DB
  - Then receiver returns 200 and X-EG-Signature validates, and delivery logged as success

- Scenario: invalid signature
  - Given a registered webhook with secret
  - When receiver returns a 200 but signature mismatches
  - Then mark delivery as FAILED and notify ops (do not treat as success)

- Scenario: receiver returns 500
  - Given a registered webhook
  - When receiver returns 500 three times
  - Then enqueue to DLQ and notify customer + ops

## 8. 備註
- 若需更完整的 OpenAPI 片段（yaml/json），我可以生成一份可直接貼入 Swagger UI 的檔案（含 schema 與 examples）。預設驗證方式為 HMAC + TLS；若你偏好 mTLS 或 JWT，請回覆。
