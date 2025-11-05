# TurnBridge TODO 任務清單（依《系統規格書 v1.0》）
> 版本：v1.0（2025-11-05）  
> 範圍：`apps/turnbridge-backend`（Monolith）、`docs/turnkey/*`、未來 `apps/turnbridge-agent`  
> 說明：以 **M0 → M3** 里程碑拆解，附 **驗收條件（AC）**、**交付物（Deliverables）**、**完成定義（DoD）**。  
> 標記：☐ 待辦、◔ 進行中、☑ 已完成、✖ 延後/移除。

---

## M0 – 規格與環境
- ☑ M0-1：完成《系統規格書 v1.0》（含資料量/策略/風險/里程碑）
  - AC：章節完整、表格完備、可直接送審
  - Deliverables：`docs/turnkey/系統規格書.md`
- ☑ M0-2：CSV Profiles 與 Header-Mapping 樣板
  - AC：支援 Legacy/Canonical，自動偵測規則清楚
  - Deliverables：`csv-profiles-spec.md`、`header-mapping-sample.yaml`
- ☑ M0-3：OpenAPI 合約（Inbound/Outbound/Job）
  - AC：無冒號 REST 路徑、JWT 安全、Problem 結構
  - Deliverables：`apps/turnbridge-backend/src/main/resources/swagger/api.yml`
- ☑ M0-4：Monorepo 設定
  - AC：根層 `.gitignore`、`.gitattributes`、`.editorconfig`、`.prettierrc.json`
  - Deliverables：專案根檔案

---

## M1 – Happy Path（上傳→轉換→出包→回饋→下載）
### Domain / DB
- ☐ M1-1：建立 Entity 與 Liquibase（含索引/唯一鍵/分區策略）
  - UploadJob、JobItem、CanonicalInvoice、InvoiceItem、Tax、TrackRange
  - AC：`mvn verify` 通過、資料表與索引到位
  - Deliverables：`src/main/resources/config/liquibase/*`

### 上傳與解析
- ☐ M1-2：`POST /invoices/upload`
  - AC：支援 CSV/ZIP、profile 指定/自動偵測、>50MB 提示替代路徑
  - Deliverables：`*Resource/*Delegate` + Service/IT 測試
- ☐ M1-3：ProfileDetector / Parser
  - AC：同號多筆聚合；單元測試覆蓋 ≥90%（正常/異常）
  - Deliverables：`service/parser/*`、`src/test/*`

### 標準化與檢核
- ☐ M1-4：MappingAgent / ValidationAgent
  - AC：欄位/稅別/金額（B2B/B2C）檢核；錯誤寫回 JobItem
  - Deliverables：`service/mapping/*`、`service/validation/*`

### 配號與出包
- ☐ M1-5：NumberingAgent（動態配號：DB 鎖 + Redis 鎖）
  - AC：併發 1000 壓測不重號；稽核日誌完備
- ☐ M1-6：MIG4.1 產生器（C0401）
  - AC：1000 筆或 15MB 出包、XSD 驗證通過、置入 `SendFile/*/SRC`

### 回饋解析與回覆
- ☐ M1-7：ProcessResult / SummaryResult 解析器
  - AC：Job/Item 狀態正確更新；差異統計；可重試
- ☐ M1-8：`GET /upload-jobs/{jobId}` `/items` `/result`
  - AC：回饋 CSV 追加 `result_code/result_msg/trace_id/assigned_invoice_no`
  - Deliverables：Controller/Service + 測試

### Portal/Admin（占位）
- ☐ M1-9：Portal `/portal/jobs` 列表/明細/下載（假資料打通）
- ☐ M1-10：Admin `/admin/jobs` 批次監控（趨勢/錯誤碼 TopN）

---

## M2 – 字軌、錯誤分流與對帳
- ☐ M2-1：E0401 匯入（TrackRange 建立）
- ☐ M2-2：E0402 產生（字軌到期自動收斂空白發票）
- ☐ M2-3：錯誤分流：可修正重送 vs 作廢重開（含 Portal 操作）
- ☐ M2-4：對帳報表：SummaryResult vs 內部統計
- ☐ M2-5：Dashboard：日上傳成功率、失敗率、回饋延遲

---

## M3 – Agent、即時通知與觀測
- ☐ M3-1：TurnBridge Agent（Watcher/Upload/Result Sync）
- ☐ M3-2：WebSocket 即時通知（批次進度/回饋完成）
- ☐ M3-3：Prometheus/Grafana 指標與告警
- ☐ M3-4：歷史查詢最佳化（S3 索引表、跨期聚合）
- ☐ M3-5：MIG 多版本共存（4.1/4.2/4.3 模組化）

---

## Definition of Done（DoD）
1. Parser/Validator 單元測試覆蓋 ≥80%，其餘模組 ≥60%  
2. CI `mvn verify` 通過；OpenAPI 產生 stub 無錯  
3. 交付物與文件（規格書/README/API 範例）更新完成  
4. 重要情境（大檔/多明細/重送）均有整合測試

---

## 依賴與風險
- Turnkey 目錄權限/容量與回饋延遲  
- 客戶 CSV 變異（以 YAML Profile 降低改程式機會）  
- 配號併發鎖控（需壓測）
