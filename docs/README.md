# Turnbridge 文件導覽

> 依據 DEC-012「`docs/` 結構治理」，所有正式文件須放在下列目錄並標註責任人。臨時草稿請存放於 `workspace/` 或個人資料夾，避免混入正式文件。

| 目錄 | 用途 | 代表文件 / 內容 | 主要負責人 |
| --- | --- | --- | --- |
| `requirements/` | 決策、容量、訪談、Webhook 契約 | `DECISION_LOG_v0.3.md`、`CAPACITY_AND_ARCHITECTURE.md`、`turnkey_system_interview_draft_v0.3.md`、`webhook_spec.md` | PO / 系統分析 |
| `spec/` | SRS、API、RFP、MIG 對應 | `turnbridge-srs-v1.0.md`、`openapi-turnbridge-v1.yml`、`turnbridge-rfp-v1.0.md`、`mig-message-type-mapping.md` | 架構師 / Tech Lead |
| `integration/` | 外部整合指南（Turnkey、Webhook、API 測試腳本） | `README.md`、`turnkey-webhook.md`、`turnkey-flow.yaml`、`turnkey-flow.md`、`webhook-contract.md`、`test-scripts.md`、`scripts/newman-smoke.sh`、`ci-newman.md`、`e2e-scenarios.md` | 整合工程師 |
| `operations/` | Runbook、監控、人工流程、稽核證跡 | `README.md`、`monitoring.md`、`incident-playbook.md`、`manual-resend.md`、`turnkey-healthcheck.md` | SRE / Ops |
| `turnkey/` | 官方 MIG / Turnkey 原廠文件（唯讀） | `MIG4.1.pdf`、`Turnkey使用說明書 v3.9.pdf` | Turnkey 小組 |
| `legacy-system-docs/` | 舊系統教材與模板（`舊系統_*` 前綴） | `舊系統_import-spec.md`、`舊系統_E0501_template.md` 等 | 知識轉移小組 |
| `jdl/` | JHipster/資料模型原始檔 | `m0-upload-core.jdl` | 架構師 |

## 維護守則

1. **變更即回寫**：程式／流程變更需同步更新對應文件（詳見 `AGENTS.md §9.2`）。  
2. **索引更新**：新增或移除文件時，請同時更新本檔與 `AGENTS.md §13`、`SRS §1.3`。  
3. **PR 檢查**：PR 模板需附「受影響文件」小節，並鏈接 `DECISION_LOG` 決策 ID。  
4. **搬遷計畫**：`integration/` 與 `operations/` 目前僅含 README，待內容搬遷後更新清單。必要時於 README 的 TODO 區塊標示進度。

如需新增子目錄，請先在 DECISION_LOG 記錄決策，並於本檔表格新增條目。
