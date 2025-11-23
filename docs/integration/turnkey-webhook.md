# Turnkey / Webhook 整合操作手冊

> 適用對象：整合工程師、後端開發、測試與維運團隊  
> 依據：`turnbridge-srs-v1.0.md`、`AGENTS.md`、`webhook_spec.md`、`turnkey-mig41-integration.md`、MIG 4.1 官方文件

---

## 1. 環境需求與安裝重點

### 1.1 官方硬體／系統需求（v3.9 摘要）

| 項目 | 要求 |
| --- | --- |
| 作業系統 | Windows 10 / Windows Server 2016 以上，或 Ubuntu 18.04 / RHEL 7 / FreeBSD 12.2 以上（皆需 64-bit，Linux 需 XWindow 支援） |
| CPU / RAM | 至少 4 核心 2.0GHz，32 GB RAM 以上 |
| 儲存空間 | 可用空間 80 GB 以上（含運作空間） |
| OpenJDK | 17（隨 Turnkey 提供或自備） |
| 資料庫 | PostgreSQL ≥11.7、Oracle 19c/21c、MySQL ≥5.7、SQL Server 2014 以上、MariaDB ≥10.9.3，或使用 Turnkey 內建檔案系統 |

> 若採用外部 DB，建議先建立獨立 schema/database，並於安裝前完成 DB 帳號與 schema 建置（Turnkey 也提供啟動後建立的精靈）。

### 1.2 安裝模式

| 平台 | 安裝方式 | 重點 |
| --- | --- | --- |
| Windows | 官方 Installer | 依精靈輸入 DB / 目錄 / 憑證路徑，完成後由 GUI 操作 |
| Linux | 下載 `API_Linux_EINVturnkey_setup_X.X.X.tar.gz`，解壓後執行 `./run_ui.sh` 或 `./run_cmd.sh` | 需準備 XWindow（若使用 UI），並使用 OpenJDK 17；請在安裝目錄內執行腳本 |

> Turnkey 本身沒有官方 Docker 映像。安裝參數（例如 MOF 位址、憑證、DB 連線）仍需透過 GUI/CLI 設定流程輸入，系統會寫入其內部 config/DB。建議：完成一次 GUI 設定後，備份安裝目錄與設定檔，以便複製到其他環境。

### 1.3 無憑證時的開發策略

- **模擬 Turnkey**：在 DEV 環境建立 `/INBOX`、`/OUTBOX` 目錄並以腳本模擬 Turnkey 行為，待取得財政部測試憑證後再切至正式 Turnkey。  
- **外部流程自動化**：雖然安裝需人工，但目錄/排程/監控可透過 `turnkey-flow.yaml` + IaC（Ansible/Helm）自動化。  
- **設定維護**：由於 GUI 內部設定無 API，可在 `turnkey-flow.md` 或 Runbook 中記錄操作步驟，確保每次重新安裝都可重現。

---

## 2. 整體流程概觀

```
Client/Agent → REST Upload → Normalize/Validation → F/G XML → Turnkey /INBOX
                                                       ↑
                                Webhook 推播 ← State Engine ← Turnkey /OUTBOX
```

1. 客戶/Agent 依 999 筆規格上傳 CSV/ZIP（含 SHA-256 與 legacy metadata）。
2. 後端進行 Raw persistence → Normalize（A/B/C/D → F/G）→ Validation。
3. 通過驗證後，以 F/G 結構產出 XML，置於 Turnkey `/INBOX`（Pickup）。
4. Turnkey 既有排程負責簽章、加密、上拋 MOF；回饋（ACK/ERROR）置於 `/OUTBOX`。
5. Backend 監聽 `/OUTBOX`、解析回饋、更新狀態並透過 Webhook 推播；必要時觸發 Ops Runbook。

---

## 3. Turnkey 目錄與責任分工

| 目錄 | Turnkey 責任 | 加值中心（Backend）責任 | 備註 |
| --- | --- | --- | --- |
| `/INBOX` | 每 5 分鐘掃描、簽章、傳送 MOF | 產生合法 F/G XML 並放入子目錄（依租戶、日期或批次命名） | 檔名建議：`FG0401_YYYYMMDD_seq.xml` |
| `/OUTBOX` | 放置回饋檔（ACK、ERROR、RSP） | 監聽/解析 XML，更新 `TurnkeyMessage`、觸發 Webhook | 需保留至少 30 天備查 |
| `/ERROR` | 放置 Turnkey 無法處理之檔案 | 下載並分析原因，重新產檔或通知客戶 | 需串接 Ops 告警 |

### 2.1 XML 產出要點

1. **訊息類型**：僅允許 F/G 系列（F0401/F0501/F0701、G0401/G0501）。  
2. **檔案結構**：遵循 MIG 4.1 XSD；若來源為舊制，Normalize 後再生成。  
3. **附件**：簽章、加密由 Turnkey 處理，Backend 不需自行簽章。  
4. **權限**：寫入 `/INBOX` 需具備適當 OS/網路權限；建議使用專屬 service account。  
5. **日誌**：輸出時記錄 `xml_path`、`message_type`、`tenant_id`、`sha256`。

### 2.2 回饋解析

| 種類 | 常見欄位 | 處理規則 |
| --- | --- | --- |
| ACK | `MessageID`、`ReceiptNo`、`TimeStamp` | 將 Invoice/Import 狀態更新為 `ACKED`，記錄回饋 XML |
| ERROR | `ErrorCode`、`ErrorMessage`、`OriginalMessageID` | 狀態設為 `ERROR`，寫入 `TurnkeyMessage` 供 Ops/客戶查詢 |
| Daily Summary | 匯總筆數、成功/失敗統計 | 供報表與 webhook `turnkey.feedback.daily-summary` 使用 |

---

## 4. Webhook 規格（實作版）

### 3.1 設計原則

- **傳輸**：HTTPS POST，payload 為 UTF-8 JSON。  
- **驗證**：HMAC-SHA256，Header：`X-Turnbridge-Signature: sha256=<hex>`。  
- **重試**：1 分鐘 → 5 分鐘 → 15 分鐘，仍失敗則進 DLQ。  
- **Idempotency**：`delivery_id`（UUID）唯一；接收端以該值去重。  
- **可觀察性**：所有投遞記錄於 `WebhookDeliveryLog`，指標輸出至 Prometheus。

### 3.2 註冊 API TL;DR

```
POST /api/v1/webhooks
{
  "url": "https://example.com/webhooks/invoice",
  "events": ["upload.completed", "invoice.status.updated"],
  "secret": "<可留空，由系統產生>",
  "description": "主站 Webhook"
}
```

回應：201，含 webhook `id` 與一次性 `secret`。  
Webhook 可透過 `PATCH /api/v1/webhooks/{id}` 更新事件或啟用/停用。

### 3.3 推播 Header

| Header | 說明 |
| --- | --- |
| `Content-Type` | `application/json` |
| `X-Turnbridge-Event` | 事件名稱（如 `invoice.status.updated`） |
| `X-Turnbridge-Delivery-Id` | UUID |
| `X-Turnbridge-Timestamp` | Unix epoch seconds |
| `X-Turnbridge-Signature` | `sha256=<hex(HMAC(secret, body))>` |

### 3.4 範例 Payload

```json
{
  "delivery_id": "a2c2d447-...-98fa",
  "event": "invoice.status.updated",
  "timestamp": "2025-11-12T09:00:00Z",
  "tenant_id": "TEN-001",
  "invoice_no": "AB12345678",
  "status": "ACKED",
  "mof_code": "050200",
  "message": "MOF confirmed"
}
```

其他事件：

- `upload.completed`：附帶 `import_id`、`success_count`、`error_count`。  
- `turnkey.feedback.daily-summary`：含 `date`、`expected`、`ack_count`、`error_count`。  
- `invoice.manual.issued` / `invoice.manual.cancelled`：供人工流程回饋。

### 3.5 重試與 DLQ

| 次數 | 延遲 | 行為 |
| --- | --- | --- |
| 第 1 次 | 即時 | 失敗則進入 retry queue |
| 第 2 次 | +1 分鐘 | 仍失敗則紀錄 |
| 第 3 次 | +5 分鐘 | 仍失敗則紀錄 |
| 第 4 次 | +15 分鐘 | 再失敗 → DLQ，Halt 重試，通知 Ops (`webhook.delivery.failed`) |

> 排程設定：`application.yml` 的 `webhook.retry-cron`（預設 `0 */1 * * * *`）負責掃描 `webhook_delivery_log.next_attempt_at`，若 `status=RETRY` 且到期即重送；超過四次則將 `status=FAILED`、填寫 `dlqReason` 並產生 DLQ 事件。

DLQ 記錄：`delivery_id`、`payload`、`last_status`、`attempts`、`last_error`。  
Ops 可於 `docs/operations/manual-resend.md` 依指引重送。

### 3.6 監控指標

| 指標 | 來源 | 門檻/告警 |
| --- | --- | --- |
| `webhook_delivery_success_rate` | Prometheus | < 98% 觸發 P2 告警 |
| `webhook_delivery_latency_ms` | Prometheus | P95 > 2s 時發出警示 |
| `webhook_dlq_total` | Redis / DB | >0 需 30 分鐘內處理 |

### 3.7 驗收案例（BDD）

| Scenario | 驗收要點 |
| --- | --- |
| 成功推播 | 簽章驗證通過、HTTP 200、delivery log 標記 success |
| 簽章錯誤 | 接收端回 401，系統不視為成功並記錄錯誤 |
| 接收端 500 | 重試 3 次後送 DLQ，通知客戶與 Ops |

---

## 5. 測試與驗證建議

1. **Turnkey Sandbox**：建立模擬 `/INBOX`、`/OUTBOX` 目錄，使用 cron/腳本仿真。  
2. **Webhook 自測**：利用 `ngrok` 或暫時的 mock server 驗證簽章與 retry。  
3. **CI 檢查**：Pull Request 若變動與整合相關程式，需附 XML / Webhook 範例與 logs。  
4. **合約測試**：以 Postman/Newman 或 Dredd 驗證 Webhook/OpenAPI 片段。

---

## 6. 參考與後續

- 官方 Turnkey 文件：`docs/turnkey/MIG4.1.pdf`、`docs/turnkey/Turnkey使用說明書 v3.9.pdf`。  
- 若 Turnkey 版本升級或 Webhook 增加事件，請同步更新本檔與 `DECISION_LOG`。  
- Ops Runbook 詳見 `docs/operations/*.md`。
