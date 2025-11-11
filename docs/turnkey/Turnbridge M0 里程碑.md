以下是截至目前（2025-11-10）為止，**Turnbridge M0 里程碑**的整體進度統整：

---

## ✅ 已完成項目（完成並通過測試）

| 類別                       | 項目                                                    | 說明                                                                            |
| ------------------------ | ----------------------------------------------------- | ----------------------------------------------------------------------------- |
| **資料模型 / Repository**    | StoredObject / UploadJob / UploadJobItem / TrackRange | 已完成 JDL → Liquibase → Entity → Repository → 測試資料，索引與欄位命名一致。                   |
| **StorageProvider 架構**   | LocalFsStorageProvider + MinioStorageProvider         | 已可依 `turnbridge.storage.type` 自動切換；`StoredObjectRef` 提供完整 metadata。           |
| **上傳入口 API**             | `/api/invoices/upload` (InvoicesUploadResource)       | 可接收 Multipart CSV/ZIP，上傳後建立 StoredObject + UploadJob，狀態=RECEIVED。             |
| **應用服務層**                | UploadJobAppService                                   | 封裝上傳流程、IdempotencyKey 驗證、呼叫 StorageProvider 與 Pipeline。                       |
| **背景管線**                 | UploadPipeline                                        | 模擬流程：RECEIVED → PARSING → VALIDATING → PACKING → SENT → RESULT_READY。自動產生回饋檔。 |
| **回饋產生器**                | ResultFileGenerator                                   | 依 jobId 產生 CSV (UTF-8)，寫入 Storage，並綁定 UploadJob.resultFile。                   |
| **整合測試**                 | EndToEndUploadToResultIT                              | 測試完整流程「上傳→Pipeline 產回饋→下載回饋 CSV」，已自動等待 RESULT_READY。                          |
| **Swagger / OpenAPI 文件** | Code-first 設定完成                                       | 改採 Controller annotation 掃描產生 `/v3/api-docs`，不再使用 openapi-generator。          |
| **國際化與 Validation**      | ProblemDetails + zh-TW messages                       | 啟用 RFC7807，統一錯誤格式；新增 i18n/messages_zh_TW.properties；全域驗證處理器。                  |
| **測試架構**                 | IntegrationTest + Testcontainers                      | 已能使用 MockMvc 驗證流程；PostgreSQL/Local storage 皆可執行。                              |
| **安全與授權**                | JWT 認證機制啟用                                            | 所有 /api/* 端點均在 Spring Security 保護下。                                           |
| **程式碼風格 / 模組化**          | m0 模組結構明確                                             | `com.asynctide.turnbridge.app`、`storage`、`web.rest` 已分層；註解、Javadoc 齊備。        |

---

## ⚙️ 尚未完成 / 待補項目

| 優先 | 類別                               | 項目                                                                | 狀態 / 說明                                                     |
| -- | -------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------- |
| P0 | **上傳入口強化**                       | 檔案大小上限 + MIME/副檔名白名單                                              | 尚未加入 `spring.servlet.multipart.max-file-size` 與 validation。 |
| P0 | **IdempotencyService 實作**        | 目前為 stub，未實際使用 Redis/DB 儲存鍵值。                                     |                                                             |
| P0 | **Profile 偵測與 CSV/ZIP 解析**       | 尚未實作 `Profile-Legacy / Profile-Canonical` 欄位自動判斷與明細寫入。            |                                                             |
| P0 | **UploadJobItem 實際建立**           | 目前 pipeline 僅模擬狀態推進，未產生對應明細紀錄。                                    |                                                             |
| P0 | **ResultFileGenerator 強化**       | 仍為簡化欄位（lineNo/resultCode/resultMsg），需改為「原欄位 + result_*」。          |                                                             |
| P0 | **Turnkey outbox (MIG4.1 stub)** | 尚未產生佔位 XML 或轉交至 outbox 目錄。                                        |                                                             |
| P0 | **重試機制**                         | `/upload-jobs/{jobId}/retry-failed` 邏輯尚未補上（ERROR → QUEUED）。       |                                                             |
| P0 | **DB 唯一鍵與約束**                    | `upload_job.job_id` 與 `stored_object(bucket, object_key)` 唯一索引待加。 |                                                             |
| P0 | **觀測性 / Pipeline 錯誤處理**          | Pipeline 發生例外時目前僅記 log，未發送通知或統計。                                  |                                                             |
| P1 | **ETag / 304 支援**                | `StoredObject` 下載可加 ETag header 與條件式回應。                           |                                                             |
| P1 | **查詢介面補強**                       | `/api/upload-jobs/by-job-id/{jobId}` 仍缺。                          |                                                             |
| P1 | **錯誤碼字典與驗證規則**                   | ResultCodeDef 未建立；CSV 欄位驗證（0402/0510）尚未連動。                        |                                                             |
| P1 | **資料清理排程**                       | 原始/回饋檔與 DB JobItem 保留期未設定。                                        |                                                             |
| P1 | **README / API 說明文件**            | 尚未整理最終 `docs/turnkey/系統規格書.md` 與 Code-first 說明段。                  |                                                             |
| P1 | **React UI（M0必要）**               | UploadJobsList / UploadJobDetail 介面與篩選器尚未串接後端。                    |                                                             |
| P2 | **MIG4.1 真實 XML 產生**             | 尚未進入開發。                                                           |                                                             |
| P2 | **SFTP/Agent 對接**                | 尚未開發。                                                             |                                                             |
| P2 | **Dashboard 與監控指標**              | 尚未開發。                                                             |                                                             |

---

## 🧭 建議開發順序（Code-first 續開）

1️⃣ **上傳入口強化 + Idempotency 實作**
　→ 完善防呆與安全檢查。
　commit: `feat(api): enforce multipart size/mime limits + implement idempotency store`

2️⃣ **Profile 偵測 + CSV/ZIP 解析骨架**
　→ 解析明細、寫入 UploadJobItem。
　commit: `feat(pipeline): add CSV profile detection and item parsing (legacy/canonical)`

3️⃣ **ResultFileGenerator 強化**
　→ 輸出「原欄位 + result_code/result_msg/trace_id/assigned_invoice_no」。
　commit: `feat(result): generate canonical feedback CSV with original columns`

4️⃣ **Retry 機制 + 查詢補強**
　→ `/upload-jobs/{jobId}/retry-failed`、`/upload-jobs/by-job-id/{jobId}`。
　commit: `feat(api): implement retry-failed endpoint and job lookup by jobId`

5️⃣ **React UI 整合（M0）**
　→ 完成前端查詢、下載、重試介面。
　commit: `feat(ui): upload jobs list/detail integration`

---

✅ **結論**
你目前的 M0 已完成約 **70%**（後端主線已跑通），剩下主要是：

* 上傳/解析強化
* 回饋欄位正式化
* Retry + JobItem 明細
* UI 串接

要我從這個基礎開始撰寫下一階段的程式（例如「1️⃣ 上傳入口強化 + Idempotency」），我可以直接給你完整的程式碼與 commit。是否要我開始？
