# Turnkey Export / Webhook Dashboard 測試計畫

此測試計畫讓 QA/PM 確認新的 event filter 與 pagination 已成功整合 Turnkey 匯出與 Webhook 儀表板。請依序驗證下列項目：

1. **Turnkey Export API 正確回傳 pagination**  
   * 呼叫 `GET /api/turnkey/export/logs?page=0&size=5&event=XML_GENERATED&event=XML_DELIVERED_TO_TURNKEY`，確認 response header 包含 `X-Total-Count`，body 為 DTO list。  
   * 改為 `page=1`、`size=10` 時能取得下一頁；`event` 參數可以接受多值。  

2. **前端 event filter & pagination**  
   * Turnkey Export log 區塊勾選/取消 `XML_GENERATED`、`XML_DELIVERED_TO_TURNKEY`、`XML_DELIVERY_FAILURE` checkbox，確認 table 只顯示對應 log，並自動回到第 1 頁。  
   * 調整「每頁筆數」下拉選單，確認 Table row 數變化、頁碼與 total 正確顯示。  
   * 點擊「上一頁/下一頁」按鈕，檢查 `page` 參數與 header `X-Total-Count` 是否變化。  

3. **detail 顯示 `turnkeyFile`/`reason`**  
   * 以 `XML_DELIVERED_TO_TURNKEY` event log 為例，開啟 detail modal，驗證 detail JSON 中包含 `turnkeyFile` 路徑，前端可顯示；若 event 有 `reason`（搬移失敗）則 UI 也要呈現。  
   * 在 table row 中 `message` 欄也可以顯示 `detail` 的摘要，以輔助 Quick view。

4. **Webhook 儀表板 event filter**  
   * 同樣應能依 `eventCode` 過濾（`invoice.status.updated` 等），並支援 pagination（參數 `page`/`size`）。  
   * 確認 UI 也可以透過 schedule log / `ImportFileLog` 來查 `XML_DELIVERED_TO_TURNKEY` 事件，以便 Ops 追蹤 Turnkey 目錄。

5. **Grafana / Turnkey pickup metrics**  
   * 監控 `turnkey_pickup_src_stuck_files`、`turnkey_pickup_pack_stuck_files` 等指標，確定 Grafana dashboard 可以顯示 `SRC`/`Pack`/`Upload` 的滯留數量與 last scan timestamp。  
   * 請 QA 實驗：在 Turnkey `SRC` 放入 XML 檔案後，檢查 pickup metrics 是否在規定分鐘內反映該筆；若目錄超過 `pickup-max-age-minutes`，是否會觸發 Alert。

完成後請記得把 `Issue #6`（Turnkey export event filter）狀態改為 In Progress / Fixed，並在手動測試證據裡附上 UI截圖與 API request/response。
