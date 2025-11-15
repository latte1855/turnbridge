# Inbound → Turnkey → Feedback 實測案例

> 目的：逐步驗證匯入 → Normalize → Turnkey → Webhook 全流程，並在完成後產生報告。

## 1. 案例矩陣

| ID | 案例 | 輸入資料 | 預期 Turnkey 結果 | Webhook 事件 | 備註 |
| --- | --- | --- | --- | --- | --- |
| E2E-001 | 正常 F0401 批次 | `samples/e2e/E2E-001_invoice.csv` | `/OUTBOX` ACK | `upload.completed`、`invoice.status.updated (ACKED)` | 999 內，mock-turnkey 即可 |
| E2E-002 | Normalize 錯誤（缺欄位） | `samples/e2e/E2E-002_invoice_missing.csv` | ImportFileLog ERROR | `upload.completed` (errorCount>0) | 檢查 ImportFileLog |
| E2E-003 | Turnkey ERROR | `samples/e2e/E2E-003_invoice_error.csv` | `/OUTBOX` ERROR XML | `invoice.status.updated (ERROR)` | 觸發 manual-resend |
| E2E-004 | Manual Resend 成功 | 取自 E2E-003 importId | 手動 XML 再投遞 ACK | `invoice.status.updated (ACKED)` | 驗證審核 + AuditLog |
| E2E-005 | Webhook 端點故障 | 任一 import | Webhook 3 次 500 → DLQ | `webhook.delivery.failed`（選用） | 搭配 DLQ 重送 |
| E2E-006 | Turnkey Summary | 多筆 import | `/OUTBOX` summary 檔 | `turnkey.feedback.daily-summary` | 與報表對帳 |

## 2. 資料與腳本

- `samples/e2e/E2E-001_invoice.csv`：含 300 筆 F0401（內附合法 A0401 格式做映射測試）。
- `samples/e2e/E2E-002_invoice_missing.csv`：缺欄位、覆蓋 Normalize error。
- `samples/e2e/E2E-003_invoice_error.csv`：合法檔案，但特定筆以 `.error` 命名，mock-turnkey 會產生 ERROR。
- `samples/e2e/E2E-004_manual_payload.json`：Manual resend 用 payload（XML 路徑與原因）。
- `samples/e2e/E2E-005_webhook_payload.json`：模擬 Webhook 送出/重送。
- `docs/integration/scripts/mock-turnkey.py`：模擬 `/INBOX`→`/OUTBOX`。
- `docs/integration/scripts/newman-smoke.sh`：驗證上傳/查詢/Webhook。
- `samples/客戶提供測試資料/202212/轉出欄位訂定.txt`：客戶現行系統匯出格式（`|` 分隔、C0401/C0501/C0701 欄位說明），請針對實際測試時依此檔案產生輸入資料。

> Sample CSV/XML/JSON 可放置於 `samples/e2e/`，由 `tools/agent-tests/` or CI 讀取。

## 3. 執行流程（例：E2E-001）

1. 使用 `tools/agent-tests/node` 將 `E2E-001_invoice.csv` 切檔並上傳。
2. 確認 ImportFile/Log 無錯誤（`status=UPLOADED`）。
3. 透過 `mock-turnkey.py` 或實際 Turnkey 驗證 `/OUTBOX` 出現 ACK。
4. 驗證 InvoiceStatus=ACKED，並檢查 Webhook Log。
5. 填寫報告（見下一節）。

## 4. 報告模板

```
## 案例：E2E-001（正常批次）
- ImportId：imp_20251115_0001
- Turnkey MessageID：2025111500001
- Webhook Delivery IDs：...
- 結果：PASS
- 異常/備註：無
```

建議於 `workspace/e2e-reports/` 或專用資料夾保存報告，並在 PR/里程碑附上連結。

## 5. TODO
- 自動化：以 Jenkins/GitHub Actions 跑 `E2E-001` ~ `E2E-006`，並輸出報告。
- Mock vs 實機：確定何時需切換至實際 Turnkey（取得憑證後）。
- 轉接 `docs/requirements/dev-roadmap.md` 的 Phase 里程碑：M2/M3/M4 完成前需跑哪些案例。
- 客戶實際檔案：若需與客戶資料完全一致，請以 `samples/客戶提供測試資料/202212` 內容產製測試檔，再用 `tools/agent-tests/` 轉成上傳格式。
