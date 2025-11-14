# 開發前檢視（Dev Readiness Checklist）

> 目的：在正式進入實作前，盤點仍需討論或待確認的項目。

## 1. 架構與環境
- [pending] Turnkey DEV/UAT 主機：是否已有安裝時程與負責人？（需與 `turnkey-flow.md`/Ansible playbook 對齊）
- [pending] 憑證/帳號申請：對接財政部測試環境所需的 CA、MOF 帳號、FTP/SFTP 權限是否啟動流程？
- [pending] 模擬環境：`mock-turnkey.py` 是否納入每日開發流程？需要 docker compose or Makefile 來啟動嗎？

## 2. 功能需求
- [V] Portal/RBAC 細節：`turnbridge-srs-v1.0.md §4` 是否需再補畫面流程、操作權限表？
- [V] 匯入 API 錯誤處理：`§5.1` 已列舉主要錯誤碼，是否還需補「批次/單筆回應 payload」範例？
- [V] Webhook 契約：`webhook-contract.md` 與 `webhook_spec.md` 已同步，是否需要最終審閱/簽核？

## 3. 測試與驗證
- [pending] Newman Smoke：CI secrets（`TURNBRIDGE_TOKEN` 等）何時配置？測試資料（ZIP/MD5）由誰產生？
- [pending] E2E 案例：`docs/integration/e2e-scenarios.md` 預計何時寫入實際報告？是否需要自動化腳本？
- [pending] Turnkey 模擬 vs 實機：切換到正式 Turnkey 前需哪些驗收（憑證測試、MOF 連線、/INBOX 權限）？

## 4. 追蹤
- 請於週會或里程碑檢視本表，確認未勾項目是否需建立 Jira/Issue 或會議。完成後在 PR 中附上更新狀態。
