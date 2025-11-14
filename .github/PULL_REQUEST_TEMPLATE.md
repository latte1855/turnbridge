**PR 標題**：`feat(module): 調整 XXX，對應 DEC-0xx`

## 摘要
- 這個 PR 做了什麼？為什麼做？
- 相關決策：`DEC-00x`（請列出）

## 影響範圍（勾選適用項目）
- [ ] 上傳/驗證
- [ ] 轉檔/XML 生成
- [ ] Webhook/事件
- [ ] 多租戶/RLS
- [ ] 資料模型/DB 結構
- [ ] Agent 端腳本/工具
- [ ] 文件（SRS/Decision/Webhook/Agents）

## 驗收證據
- 單元/整合測試、壓測或手動操作截圖
- Webhook 驗簽成功紀錄（若適用）

## 檢核清單
- [ ] 程式碼含繁體中文註解；Java 類/方法/欄位含 Javadoc
- [ ] 單檔 999 明細不拆單規則仍成立
- [ ] 輸出 Turnkey XML 仍為 F/G 新制（舊制保留 `legacyType` + `rawLine`）
- [ ] 回寫相關文件：
  - [ ] `docs/spec/turnbridge-srs-v1.0.md` 或子章節
  - [ ] `docs/requirements/DECISION_LOG_v0.3.md`
  - [ ] `docs/requirements/webhook_spec.md`
  - [ ] `docs/AGENTS_MAPPING_v1.md` / `AGENTS.md`
- [ ] 風險與 rollback 方案已描述
- [ ] 需要的 migration/script 已提供

## 其他
- 相關 Issue：
- 相依 PR：
