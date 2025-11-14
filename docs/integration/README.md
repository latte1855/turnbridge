# `docs/integration/`

## 目的
集中維護所有「對外整合」相關資料，包含：

* Turnkey 目錄/排程介接的流程圖與實作細節
* Webhook、API、SFTP、批次匯入等雙向測試腳本與驗收腳本
* 與第三方系統（POS、ERP、加值中心）的欄位對映與範例檔

## 既有文件
| 檔名 | 說明 | 狀態 |
| --- | --- | --- |
| `turnkey-webhook.md` | Turnkey 目錄責任、XML 產出、Webhook HMAC/Retry/監控指引 | ✅ 初版 |
| `turnkey-flow.yaml` | Turnkey 目錄/排程設定範例（INBOX/OUTBOX/警報） | ✅ 初版 |
| `turnkey-flow.md` | 如何將 YAML 套用於 Ansible/Systemd/K8s | ✅ 初版（對應 `deploy/turnkey/ansible/`） |
| `mock-turnkey.md` | DEV/UAT 模擬 Turnkey 流程指引 | ✅ 初版 |
| `scripts/check-turnkey-flow.sh` | 驗證 IaC 是否引用 `turnkey-flow.yaml` | ✅ 初版 |
| `scripts/mock-turnkey.py` | 模擬 Turnkey pickup/回饋行為的腳本 | ✅ 初版 |
| `webhook-contract.md` | Webhook 契約（事件、Header、Schema、錯誤碼） | ✅ 初版 |
| `../../scripts/check-webhook-consistency.py` | 驗證 `webhook_spec.md` 與 `webhook-contract.md` 事件一致性（CI：Docs Quality workflow） | ✅ 初版 |
| `test-scripts.md` | curl/CLI/Postman 測試腳本說明 | ✅ 初版 |
| `scripts/newman-smoke.sh` | Newman smoke 測試腳本 | ✅ 初版 |
| `ci-newman.md` | 在 CI Pipeline 執行 Newman smoke 測試的指引 | ✅ 初版 |
| `e2e-scenarios.md` | Inbound→Turnkey→Feedback 實測案例 | ✅ 初版 |
| `postman/turnbridge-api.postman_collection.json` | Postman Collection（上傳/查詢/註冊 Webhook） | ✅ 初版 |
| `postman/turnbridge-env.postman_environment.json` | Postman Environment（base_url/token 等變數） | ✅ 初版 |
| `docs/turnkey/MIG4.1.pdf` | 官方 MIG 4.1 規格 | 保留於 `turnkey/`（唯讀） |
| `docs/turnkey/Turnkey使用說明書 v3.9.pdf` | Turnkey 原廠手冊 | 保留於 `turnkey/`（唯讀） |
| `docs/requirements/webhook_spec.md` | Webhook 契約與 API 片段 | 逐步拆分中 |

## TODO（開發階段暫緩，於上線前統一處理）
1. **IaC 完整整合**：Ansible Playbook 已引用 `turnkey-flow.yaml`（見 `deploy/turnkey/ansible/`，可用 `scripts/check-turnkey-flow.sh` 驗證），但尚未併入正式 IaC Repo / CI；計畫在上線前統一處理。
2. **Webhook 雙文件鎖定**：雖有 `scripts/check-webhook-consistency.py`（Docs Quality workflow 也會跑），仍需在最終驗收前逐欄比對 `webhook_spec.md` 與 `webhook-contract.md` 的 schema/錯誤碼，確保沒有遺漏。
3. **Newman Smoke 實際運行**：Workflow 已建（`.github/workflows/newman-smoke.yml`），但尚未配置 Secrets（Token/Base URL）或統一報告格式；上線前會設定環境變數並驗證。
4. **E2E 報告自動化**：`e2e-scenarios.md` 已列模板，實際報告/自動化腳本將在功能完成後（臨近上線）一次性撰寫。

> 維護者：整合工程師（Integration Squad）。任何對外介面變更需在此留下測試證據。
