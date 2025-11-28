# 電子發票匯入檔案規格（E0501 / Invoice）

本文檔依 `UploadCsvFileService` 與 `InvoiceChecker` 程式實作整理，提供給開發/上傳端的欄位說明、格式、驗證規則、錯誤訊息範例與測試建議。

## 目錄
- 高階流程
- 檔案編碼與分隔
- E0501（發票號碼分配）規格
  - 欄位對應
  - 格式與驗證
  - 範例
  - 常見錯誤訊息
- Invoice（發票明細/主檔）規格
  - 支援的 MessageType
  - 欄位對應（C0401 主檔與明細）
  - C0501 / C0701 欄位
  - 金額與明細驗證規則（來自 InvoiceChecker）
  - 範例
  - 常見錯誤訊息
- 上傳注意事項與測試清單


## 高階流程
- Client 上傳檔案，系統會在 `ImportFile` 裡記錄 `fileNameOnServer`, `fileChecksum`, `gatewayInTime` 等。
- 處理端分兩種流程：`processE0501`（發票號碼分配）與 `processInvoice`（發票資料）。
- 前置：比對 MD5（若不符中止）、讀檔、逐筆驗證（大量驗證委由 `InvoiceChecker`），通過則寫入 `InvoiceHistory` 與 `Invoice`，並建立 Turnkey XML（`turnkeyHelper.storeXml`）。
- 處理過程中的所有訊息會以 `ImportFileLog` 記錄（INFO/ERROR），最後寫入 DB。


## 檔案編碼與分隔
- E0501
  - 編碼：BIG5（程式使用 `Charset.forName("BIG5")` 讀取）
  - 分隔符：逗號 `,`
  - 第一列為 header（會被程式移除）
- Invoice
  - 編碼：UTF-8（程式使用 `StandardCharsets.UTF_8` 讀取）
  - 分隔符：豎線 `|`（`split("\\|", -1)`，保留末尾空欄位）
  - 注意：若檔案含 UTF-8 BOM（\uFEFF），程式會移除 BOM
  - 驗證：程式會把檔名最後 4 碼當作「宣告筆數」，與實際讀到的行數比對


## E0501（發票號碼分配）
說明：用以匯入發票號段（分配給客戶），每列代表一個號段。

### 必須包含的欄位（index 從 0 開始，至少須包含下列欄位）
- [0] 營業人統一編號（BAN）: 8 碼數字
- [1] 發票類別代號: e.g. "7" 或 "8"（由 `InvoiceType.getInvoiceType` 解析）
- [3] 發票期別（範例：`111/01~111/02`）：程式會取 `~` 後半段並移除 `/`，結果需為 5 碼（民國年 + 月碼）
- [4] 發票字軌（invoiceTrack）: 2 碼英文字母
- [5] 發票起號（invoiceBeginNo）: 8 碼數字，末 2 碼為 `00` 或 `50`
- [6] 發票迄號（invoiceEndNo）: 8 碼數字，末 2 碼為 `49` 或 `99`

> 程式也接受其他欄位，但以上欄位為必需以完成檢核。

### 規則與驗證
- BAN: 必為 8 碼數字（`InvoiceChecker.isIdentifier`）且必須能在 DB 找到對應 `Customer`（`customerRepository.findOneByBanWithEagerRelationships`）
- 發票期別: 解析後必須為 5 碼、數字，且末 2 碼須屬於允許的月份列表（系統內 `InvoiceAssignNoResource.avaliableMonths`，常見值為 `02,04,06,08,10,12`）
- 發票字軌: 長度 2 且為英文字母
- 起始/結束號: 8 碼、數字；起始末 2 碼為 00 或 50；結束末 2 碼為 49 或 99
- 區間長度: (end - begin + 1) 必須是 50 的倍數
- 區間不得與資料庫中已存在之區間重疊（呼叫 `invoiceAssignNoService.isExist`）

### 範例（CSV 行，示意）
Header: `BAN,InvoiceType,?,PeriodRange,Track,BeginNo,EndNo, ...`
Row: `12345678,7,_,111/01~111/02,WB,00883662,00884124`

### 常見錯誤訊息（ImportFileLog.message 範例）
- "營業人統編 [XXXXX] 格式錯誤. 須為 8 碼數字"
- "營業人統編 [XXXXX] 不存在資料庫中."
- "發票年月固定長度 5 碼. [xxxxx]."
- "發票字軌固定長度 2 碼. [xx]."
- "起始號固定長度 8 碼. [xxxxxxxx]."
- "起始號末 2 碼為 [00, 50] 其中之一. [xx]."
- "結束號固定長度 8 碼. [xxxxxxxx]."
- "結束號末 2 碼為 [49, 99] 其中之一. [xx]."
- "起始號與結束號之差必須為 50 之倍數."
- "發票區間跟其他資料重疊. [yyyyy WB0088xxxx ~ WB0088xxxx]"


## Invoice（發票資料）規格
說明：發票檔每列是一個訊息（MessageType），以 `|` 分隔；對同一張發票，可能會跨多列（第一列為發票主檔（含明細），下一列若 messageType 與 invoiceNumber 相同即視為同張發票的另一項明細）。程式支援多種 MessageType（開立、作廢、註銷、退回等）。

### 支援的 MessageType（節錄）
- 開立: `A0101`, `A0401`, `C0401`
- 作廢/退回: `A0201`, `A0301`, `A0501`, `A0601`, `C0501`
- 註銷: `C0701`

若 `MessageType` 解析失敗，該列會被標記 ERROR 並跳過。


### C0401（開立） 欄位對應（index 從 0 開始）
0. messageType
1. invoiceNumber (2 大寫字母 + 8 數字)
2. invoiceDate (yyyyMMdd)
3. invoiceTime (HH:mm:ss)
4. sellerIdentifier (賣方統編，8 碼)
5. sellerName (賣方名稱，1-60 碼)
6. buyerIdentifier (買方統編，若無則填 0000000000)
7. buyerName (買方名稱，1-60 碼；若填統編則必與買方統編相同)
8. invoiceType
9. donateMark (0/1)
10. carrierType
11. carrierId1
12. carrierId2
13. printMark (Y/N)
14. npoban (捐贈對象)
15. randomNumber (發票防偽隨機碼 4 碼)
16. detail.description
17. detail.quantity
18. detail.unitPrice
19. detail.amount
20. detail.sequenceNumber
21. salesAmount
22. freeTaxSalesAmount
23. zeroTaxSalesAmount
24. taxType
25. taxRate
26. taxAmount
27. totalAmount
28. discountAmount
29. cardLast4No (可選)

> 明細可視為多列（同一張發票之多條明細），`readInvoiceFile` 會把每列讀成一行並由 `toC0401Invoice` 聚合同一 invoiceNumber 的明細。


### C0501 / C0701（作廢 / 註銷） 欄位（節錄）
0. messageType
1. invoiceNumber
2. invoiceDate
3. buyerIdentifier
4. sellerIdentifier
5. cancelDate / voidDate
6. cancelTime / voidTime
7. cancelReason / voidReason
8. returnTaxDocumentNumber (作廢專案核准文號)
9. cancelRemark / voidRemark


### 重要驗證規則（來自 `InvoiceChecker`）
- 發票號碼: 必填，格式 `^[A-Z]{2}\d{8}$`
- 發票日期: 必填，格式 `yyyyMMdd`（使用 `LocalDate.parse` 驗證）
- 發票時間: 必填，格式 `HH:mm:ss`（使用 `LocalTime.parse` 驗證）
- 發票隨機碼: 必填，4 碼，數字或字母 A（pattern: `[0-9A]{4}`，實作使用 PATTERN_RANDOM_NUMBER）
- 捐贈註記: 必填，且為 0 或 1
  - 若為 1，則 `npoban` 必填且為捐贈碼 (3-7 碼數字) 或統一編號(8 碼)
  - 若為 1，`PrintMark` 必須為 N，且 `checkNumber` (發票檢查碼) 不得填寫
- PrintMark (Y/N): 必填
  - 若為 Y，則 `carrierType`, `carrierId1`, `carrierId2` 必須為空白，且 donateMark 必為 0
  - 若使用手機條碼 (carrierType = `3J0002`)，`carrierId1`, `carrierId2` 必填且符合 `PATTERN_3J002`（以 `/` 起頭，總長 8）且兩者相同
  - 若使用自然人憑證 (`CQ0001`)，`carrierId1`, `carrierId2` 必填且符合 `PATTERN_CQ0001` 且兩者相同
  - 其他會員載具：carrierType 由系統管理（格式檢查 PATTERN_CARRIER_TYPE），carrierId1/2 必填並長度 <= 64
- 明細檢核（每項）
  - description: 必填, 長度 <= 256
  - quantity: 必填, numeric (BigDecimal)，若為 TWD 可能期望整數
  - unitPrice: 必填, numeric
  - amount: 必填, numeric
  - sequenceNumber: 必填, 長度 < 3
  - unit: optional, 長度 <= 6
  - remark: optional, 長度 <= 40
  - relateNumber: optional, 長度 <= 20
- 金額合計檢核（主檔）
  - salesAmount / freeTaxSalesAmount / zeroTaxSalesAmount: 必填（若無交易則填 0），不得為負數
  - taxType: 必填且符合 TaxType
  - taxRate / taxAmount / totalAmount: 必填且為數字，不得為負數
  - currency 若指定必為長度 3（例: TWD）

### Turnbridge Normalize 額外檢核
 - 上傳時會先合併 `invoiceDate` (yyyyMMdd) 與 `invoiceTime` (HH:mm:ss)，若任一格式錯誤即取消該筆並回報 `DATETIME_INVALID`，未來若改成單一 `DateTime` 欄位也可延伸。  
 - `InvoiceNo` 必須符合 `[A-Z]{2}\d{8}`，格式錯誤會回傳 `INVOICE_NO_INVALID`；未來若出現新的字軌/號碼格式，只需調整正則即可。  
 - `RandomNumber`（發票防偽碼）限 4 碼 `[0-9A]`，違規會回傳 `RANDOM_NUMBER_INVALID`。  
 - `DonateMark` 只能是 `0/1`，違反會回傳 `DONATE_MARK_INVALID`。  
訊息僅驗證通過的發票才會寫入 `Invoice`/`InvoiceItem`；其他錯誤行會寫到 `ImportFileLog`/`ImportFileItem` 的 `fieldErrors` 中供前端 `欄位錯誤` 顯示。


### 範例（單列 C0401 範例）
```
C0401|AB12345678|20251101|12:00:01|12345678|賣方公司|0000000000|0000|08|0||/|||N||品名A|2|100|200|1|200|0|0|0|5|10|210|0|1234
```
> 範例中 `|` 代表欄位分隔。明細欄位 (16-20) 與主檔合併於列中；多筆明細可用多列（同 messageType 與 invoiceNumber）儲存。


### 常見錯誤訊息（節錄）
- "訊息類別 X 錯誤."（MessageType 解析失敗）
- "發票號碼錯誤.（空值）"
- "發票號碼 [xxx] 格式錯誤."
- "發票日期 [xxx] 格式錯誤." / "發票時間 [xxx] 格式錯誤."
- "發票防偽隨機碼 [xxx] 錯誤. 4 碼（0-9,A）"
- 捐贈、載具、列印註記相關錯誤（範例見 `InvoiceChecker` 各方法的 `message`）
- 明細不足或超過 999 項
- 檔名筆數與實際筆數不符


## 上傳注意事項
- E0501 範本請以 BIG5 編碼儲存（Windows-1252/UTF-8 會造成讀取錯誤）
- Invoice 範本請以 UTF-8 編碼上傳，若來自含 BOM 的來源，系統會自動去除 BOM
- 檔名尾部 4 碼應為檔案筆數（Invoice 類型），以便程式做比對
- 上傳前請計算 MD5（hex）並填入系統上傳表單的 checksum 欄位，系統會比對後才處理


## 測試清單（建議）
1. E0501: 正常案例（單列、符合 50 倍數、字軌、統編存在）
2. E0501: md5 不符 → 期望中止，產生 ERROR log
3. E0501: 起訖號末碼不符（例如末碼不是 00/50 或 49/99）→ ERROR
4. Invoice: 正常 C0401（含一筆明細）→ 建立 invoice 並呼叫 issuance
5. Invoice: 多列同 invoiceNumber 聚合為多項明細並檢核金額合計
6. Invoice: 捐贈情境（DonateMark=1）→ npoban 必填且 PrintMark=N
7. Invoice: 手機條碼情境（CarrierType=3J0002）→ carrierId1/2 必填且格式符合（以 / 起頭，共 8 碼）
8. Invoice: 欄位缺失與格式錯誤→ 針對各列產生 ERROR 並跳過（或視情況標記 ValidateError）


## 檔案位置（本次建立）
- `docs/import-spec.md`（本檔）
- `templates/e0501_template_big5.csv`（E0501 範本，請另存為 BIG5 編碼）
- `templates/invoice_template_utf8.csv`（Invoice 範本，UTF-8）


---
若要我把 `import-spec.md` 轉成 PDF、或把範本自動以 BIG5 編碼輸出（直接轉檔），我可以再幫你做。
