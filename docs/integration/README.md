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
| `turnkey-flow.md` | 如何將 YAML 套用於 Ansible/Systemd/K8s | ✅ 初版 |
| `webhook-contract.md` | Webhook 契約（事件、Header、Schema、錯誤碼） | ✅ 初版 |
| `test-scripts.md` | curl/CLI/Postman 測試腳本說明 | ✅ 初版 |
| `scripts/newman-smoke.sh` | Newman smoke 測試腳本 | ✅ 初版 |
| `e2e-scenarios.md` | Inbound→Turnkey→Feedback 實測案例 | ✅ 初版 |
| `postman/turnbridge-api.postman_collection.json` | Postman Collection（上傳/查詢/註冊 Webhook） | ✅ 初版 |
| `postman/turnbridge-env.postman_environment.json` | Postman Environment（base_url/token 等變數） | ✅ 初版 |
| `docs/turnkey/MIG4.1.pdf` | 官方 MIG 4.1 規格 | 保留於 `turnkey/`（唯讀） |
| `docs/turnkey/Turnkey使用說明書 v3.9.pdf` | Turnkey 原廠手冊 | 保留於 `turnkey/`（唯讀） |
| `docs/requirements/webhook_spec.md` | Webhook 契約與 API 片段 | 逐步拆分中 |

## TODO
1. （下一步）將 `turnkey-flow.yaml` 納入 IaC Repo（Helm/Ansible）並設置自動檢查。
2. 與 `webhook_spec.md` 保持同步（若官方規格更新，需更新 `webhook-contract.md`）。
3. 將 `scripts/newman-smoke.sh` 納入 CI Pipeline，並收斂報告格式。
4. 將 `e2e-scenarios.md` 的報告輸出自動化（例如 Jenkins stage）。

> 維護者：整合工程師（Integration Squad）。任何對外介面變更需在此留下測試證據。
