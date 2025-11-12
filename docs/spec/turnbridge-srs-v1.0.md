# Turnbridge 電子發票系統 — 系統規格書（SRS） v1.0

> 草案：由需求訪談與決策紀錄（`turnkey_system_interview_draft.md`、`turnkey_review_findings_v0.1.md`、`DECISION_LOG_v0.2.md`）彙整。
> 目的：把已確認的設計決策轉為可供開發參考的 SRS 條目。

## 1. 簡介
- 範圍：支援 E0501 與 Invoice（MIG 4.1）上傳→驗證→轉檔→Turnkey 上傳→回饋處理。包含 Portal UI / REST API / Agent 支援。
- 目標用戶：加值中心（業主）、企業客戶（門市/分公司）、第三方整合系統（POS、3S）。

## 2. 已確認之關鍵決策（Contract）
- 前端骨架：使用 jHipster v8.11.0 產生之 React 前端（放置於 `src/main/webapp`）。
- 事件機制：Phase 1 採用 Spring ApplicationEvent / @EventListener 作為內部事件驅動；不在 Phase 1 使用 Kafka / RabbitMQ。
- Redis：用於快取、分散鎖與短期 counters；**不**做為初期跨實例事件總線。
- 多租戶：預設採用 Shared schema，資料以 `tenant_id` 隔離，建議搭配 Row-Level Security（RLS）或應用層過濾。
- 批次檔筆數上限：每檔最大 999 筆（以發票明細數計算）；不可將同一張發票拆分到不同檔案。若上傳檔案含超出 999 筆，API 預設拒收並回傳分割建議；系統可選擇提供伺服器端自動分割作為 fallback。
- Backend / Agent 分工：Backend 負責驗證/轉檔/與 Turnkey 互動；Agent 建議負責客戶端資料收集、前置檢核與分割並上傳（Phase 2 推進）。

## 3. 功能規格（概要）
- 上傳 API
  - POST /api/v1/upload/e0501 — 上傳 E0501 CSV
  - POST /api/v1/upload/invoice — 上傳 Invoice CSV/ZIP
  - 回應：同步接受（回傳 uploadJobId）；驗證/解析為非同步處理，最終結果透過 Webhook 或 GET /api/v1/upload-jobs/{id}/result 取得。
  - 驗證：編碼 (BIG5/UTF-8)、欄位、MD5（若提供）、筆數上限檢查。
- 轉檔與上傳
  - 生成符合 MIG 4.1 的 XML，並放至 Turnkey 可讀取目錄；Turnkey 負責簽章/傳送/回饋（外部系統）。
- 回饋處理
  - 解析 MOF 回覆（ACK / ERROR），更新每張發票狀態，並以 Webhook/Portal 通知客戶端。

## 4. 非功能規格
- 可用性：99.9%
- 效能：針對 M0（上傳/解析）場景：10,000 筆 CSV 解析 ≤ 5 秒（目標）
- 安全：OAuth2/JWT（Client Credentials）、Webhook HMAC、TLS 1.3
- 儲存：Shared schema + 分區（按 invoice_date）

## 5. 多租戶安全（已確認實作要點）
- 每表需包含 `tenant_id`、`created_by`、`trace_id`、`created_at`。
- 在 SRS 與 Testing matrix 中明列 RLS 規則（super-admin/ops/admin/tenant-user 的存取範圍）。
- 驗收測試需包含跨租戶隔離測試案例。

## 6. 批次拆檔與上傳策略
- 定義：檔案內筆數以「發票明細列數」計算，單檔上限 999。
- 上傳端 (Agent/API) 必須確保每檔不超 999；若上傳檔案超出，API 回應 400 並回傳分割建議（或錯誤說明）。
- Backend 可選擇提供自動分割（系統產生多個 ImportFile）作為 fallback，但需在設計文件中標註運算成本與監控需求。

## 7. API Contract（草案）
- POST /api/v1/upload/invoice
  - Headers: Authorization: Bearer <token>, Idempotency-Key: <uuid>
  - Body: multipart/form-data { file: file, metadata: { tenantId, source, declaredRows } }
  - Responses:
    - 202 Accepted: { uploadJobId, message }
    - 400 Bad Request: { errors: [...] }

## 8. Acceptance Criteria（高階）
- 上傳檔案超過 999 筆時系統拒收並回傳可分割建議。
- 生成之 MIG 4.1 XML 經 MOF 用例測試（sample schema）通過基本結構驗證。
- 多租戶隔離測試：跨 tenant 資料不可見，RLS 規則生效。

## 9. 後續交付物（當前 sprint）
- 完整 SRS（含 Acceptance、API 詳規）
- OpenAPI 草案（對應上傳/查詢/回饋）
- 測試矩陣（含容量用例）


---

> 備註：此檔為初版 SRS 草案，會依您回覆的待確認項（SLO、檔案大小分佈、Webhook 強化方式等）再做修正並產出正式版本。
