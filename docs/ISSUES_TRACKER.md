# Issues Tracker

此文件作為單一問題追蹤檔（Markdown），用來記錄手動測試或發現的缺陷、重現步驟、證據與修正狀態。

格式同下表，請在新增問題時自增 ID 並完整填寫欄位。

| ID | Title | Component | Steps | Expected | Actual | Evidence | Status | Assignee | Priority | Fix |
|---:|---|---|---|---|---|---|---|---|---:|---|
| 1 | 使用者建立未同時綁定租戶（Create user: tenant not bound） | backend / frontend | 1. Admin 建立新使用者 (User creation) 2. 未提供或無法選擇 tenant 3. 新使用者建立後無 tenant 欄位 | 新建立的使用者應同時被綁定到 tenant（tenantId/tenant.code） | 新建立的使用者未綁定 tenant，導致該使用者無法存取特定租戶資源 | 螢幕截圖: `evidence/user-create-no-tenant.png`; API response: `evidence/user-post-response.json` | Open | frontend/backend | P1 | |
| 2 | 上傳檔案沒有附檔名，系統拒絕或行為不一致（Upload file without extension not allowed） | backend / api | 1. 以 user/admin 登入 2. 前往上傳頁面 3. 上傳一個沒有副檔名的檔案（例如 `file` 而非 `file.csv`） | 系統應接受沒有副檔名的檔案，依 header/sha256/內容判斷格式並正常處理 | 目前系統對無附檔名檔案處理不一或拒絕，需放寬上傳條件以支援此種情況 | 建議收集：上傳 request/response、前端上傳表單截圖（`evidence/upload-no-ext-request.json`） | Fixed | backend | P2 | UploadService 允許缺副檔名檔案，並以 content-type/CSV 解析確認（PR #182，commit `fix/upload-no-ext`) |
| 3 | Admin 上傳時缺少 tenant 資訊導致 400 Bad Request（error.tenantmissing） | backend / frontend | 1. 以 admin 身份登入並於上方選擇租戶（但未填入「原訊息別」或 Profile） 2. 前往上傳頁面並點選上傳 3. 觀察回應 | 當 admin 已選擇租戶（或表單含 tenant 欄位）時，上傳 API 應接受請求並返回 importId 或成功結果 | 回應 HTTP 400，body 包含 `message: error.tenantmissing`；後端 log 顯示 UploadService.resolveTenant 拋出 BadRequestAlertException（stack trace 已記錄） | Evidence: `evidence/upload-400-response.json` (response JSON)，`evidence/upload-service-log.txt` (後端 log excerpt) | Fixed | frontend/backend | P1 | Portal 強制 ADMIN 須先選取租戶後才能上傳；後端 TenantResolver 亦會透過 JWT/tenant claim 判斷租戶（commit `fix/admin-tenant-check`). |
| 4 | 匯入結果顯示筆數與實際資料不符（importId=1001） | backend / ui | 1. 以 admin 登入並於上方選擇租戶（未填「原訊息別」或 Profile） 2. 上傳 `samples/客戶提供測試資料/202511/BS0004202511130180.csv` 3. 系統回傳 `importId=1001` 並顯示匯入摘要 4. 在 Import Monitor 下載該批次的結果檔 | 下載結果檔的行數、`失敗筆數` 與 UI 顯示應一致；結果檔中每一列應為原始上傳的單筆資料並附上 `status/error` 欄位 | UI 顯示：檔名標註 `180` 筆，但 `成功 0 / 失敗 179`。下載結果檔的第一列與第二列為：
```
C0401|UF39468935|20251113|06:54:12|16070446|長興路加油站有限公司|08709576|08709576|07|0||||Y||6695|95Plus無鉛|37.68|28.6|1078|1|1027|0|0|1|0.05|51|1078|0|,status,errorCode,errorMessage,fieldErrors,tbCode,tbCategory,tbCanAutoRetry,tbRecommendedAction,tbResultCode,tbSourceCode,tbSourceMessage
C0401|UF39468936|20251113|07:01:18|16070446|長興路加油站有限公司|0000000000|0000|07|0||||Y||4923|95Plus無鉛|3.66|28.6|105|1|105|0|0|1|0.05|0|105|0|,FAILED,FIELD_REQUIRED,"400 BAD_REQUEST, ProblemDetailWithCause[type='https://www.jhipster.tech/problem/normalization-failure', title='Type 欄位必填', status=400, detail='null', instance='null', properties='{normalizedFamily=UNKNOWN, field=Type, errorCode=FIELD_REQUIRED}']","Type(1):FIELD_REQUIRED-400 BAD_REQUEST, ProblemDetailWithCause[type='https://www.jhipster.tech/problem/normalization-failure', title='Type 欄位必填', status=400, detail='null', instance='null', properties='{normalizedFamily=UNKNOWN, field=Type, errorCode=FIELD_REQUIRED}']",,,,,,
```
可能情況：結果檔在產生或合併時多出欄位名稱或多餘 header（`,status,errorCode,...` 被附加到某筆資料後），導致 CSV 解析或計數不一致。需檢查結果檔產生流程（`ImportFile` / `ImportFileItem` -> result CSV 生成）是否在合併原始行時混入 header 或有換行/編碼問題 | Evidence: `docs/evidence/import-1001-result-first-two-lines.txt` (下載結果前兩列)、原始上傳檔案: `samples/客戶提供測試資料/202511/BS0004202511130180.csv` | Open | backend | P2 | 建議：檢查結果檔產生流程，加入單元測試驗證 CSV 行數/欄位一致性，並修正合併邏輯。 |


## 欄位說明
- ID: 流水號。
- Title: 簡短說明（包含情境）。
- Component: 受影響系統（frontend/backend/api）。
- Steps: 重現步驟，簡潔但可重現。
- Expected: 預期行為。
- Actual: 實際行為，包含錯誤訊息或 warning。
- Evidence: 螢幕截圖/Network JSON/後端 log 檔案位置。
- Status: Open / In Progress / Fixed / Verified / Won't Fix。
- Assignee: 建議負責人或隊伍。
- Priority: P0 / P1 / P2 / P3。
- Fix: 若有 PR 或 Issue 參考，請放入編號或 commit hash。

---

更新流程：
1. 測試者執行重現步驟並把證據放到 `docs/evidence/`，在本檔中新增一列或更新既有列。
2. 開發者在處理問題時，把 `Status` 改為 `In Progress`，並在 `Fix` 中填入 PR 編號或 commit。
3. QA 驗證後將 `Status` 改為 `Verified` 並關閉對應 Issue（若已建立）。
