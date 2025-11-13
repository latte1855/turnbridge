# Inbound → Turnkey → Feedback 實測案例

> 目的：建立可重複的 E2E 測試清單，覆蓋匯入、Turnkey 轉拋、回饋解析與 Webhook 通知。

---

## 1. 案例矩陣

| ID | 案例 | 輸入檔 | 預期 Turnkey 結果 | Webhook 事件 | 備註 |
| --- | --- | --- | --- | --- | --- |
| E2E-001 | 正常 F0401 批次 | `samples/f0401_ok.csv` | `/OUTBOX` 產生 ACK，狀態 `ACKED` | `upload.completed`, `invoice.status.updated` | 999 筆內 |
| E2E-002 | 含錯誤行（Normalize 失敗） | `samples/f0401_mix.csv` | Import 失敗筆數 >0，無 XML | `upload.completed`（errorCount>0） | 驗證 `ImportFileLog` |
| E2E-003 | Turnkey ERROR | `samples/f0401_turnkey_error.csv` | `/OUTBOX` 產 ERROR XML | `invoice.status.updated`（status=ERROR） | 觸發 Manual Resend |
| E2E-004 | 手動重送成功 | 以 E2E-003 的 importId | 手動 XML 產生後 Turnkey ACK | `invoice.status.updated`（status=ACKED） | 需審核流程 |
| E2E-005 | Webhook 端點失敗 | 任一 import | Webhook 連續 3 次 500 → DLQ | `webhook.delivery.failed`（可選） | 驗證 `manual-resend.md` |
| E2E-006 | Daily summary | 當日累積 50+ 筆 | `/OUTBOX` summary | `turnkey.feedback.daily-summary` | 與報表對帳 |

---

## 2. 操作步驟（示例：E2E-001）

1. 依 `docs/integration/test-scripts.md` 產生 ZIP 並上傳。  
2. 於 DB 查詢 `importId` 狀態 = `UPLOADED`。  
3. 確認 `/turnkey/INBOX` 出現 XML 並於 5 分鐘內消失。  
4. 使用 `docs/operations/turnkey-healthcheck.md` 稽核 `/OUTBOX` 新增 ACK。  
5. 驗證 `TurnkeyMessage` 與 `WebhookDeliveryLog`。  
6. 將結果紀錄於 `workspace/e2e-reports/E2E-001_<date>.md`。

---

## 3. 報告模板

```
## 案例：E2E-001
- Import: imp_20251114_0001
- Turnkey MsgID: 2025111400012345
- 結果：PASS
- Webhook Delivery IDs: [...]
- 備註： ---
```

---

## 4. 自動化建議

* 將案例矩陣轉換為 CI Pipeline 的工作階段，引用 `test-scripts.md` 的 curl/Neuman 指令。  
* 透過 `docs/integration/postman/*.json` 在 Newman 中跑 smoke test；再由 Ansible/SSH 執行 Turnkey 目錄檢查。  
* 測試成果需回寫 `DECISION_LOG` 對應決策（例：DEC-011 整合）。*** End Patch
