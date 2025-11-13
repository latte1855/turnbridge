# 電子發票匯入系統 - 業務流程文檔

## 📊 系統架構概覽

```
┌─────────────────────────────────────────────────────────────────────┐
│                       電子發票匯入系統                               │
│                  (E-Invoice Gateway System)                         │
└─────────────────────────────────────────────────────────────────────┘
                                    ↓
        ┌───────────────────────────────────────────────────┐
        │              Frontend (客戶端)                     │
        │    • Web UI: 發票/配號 CSV 上傳介面               │
        │    • Client API: 程式化上傳介面                   │
        │    • 下載: 回饋檔案、測試檔案                      │
        └───────────────────────────────────────────────────┘
                                    ↓
        ┌───────────────────────────────────────────────────┐
        │        REST API 層 (CsvFileResource)              │
        │    • /api/upload/e0501/csv                        │
        │    • /api/upload/invoice/csv                      │
        │    • /api/client/auto/upload/invoice/csv          │
        └───────────────────────────────────────────────────┘
                                    ↓
        ┌───────────────────────────────────────────────────┐
        │    Service 層 (UploadCsvFileService)              │
        │    • processE0501(): 配號檔處理                   │
        │    • processInvoice(): 發票檔處理                 │
        └───────────────────────────────────────────────────┘
                                    ↓
    ┌────────────────┬──────────────────────┬──────────────┐
    ↓                ↓                      ↓              ↓
┌────────┐    ┌──────────────┐    ┌──────────────┐  ┌──────────┐
│ 驗證層   │    │  業務邏輯    │    │   Turnkey    │  │  通知層  │
│        │    │              │    │   XML 生成   │  │          │
├────────┤    ├──────────────┤    ├──────────────┤  ├──────────┤
│Invoice │    │計算金額      │    │生成 XML 配號 │  │背景備份  │
│Checker │    │聚合明細      │    │生成 XML 發票 │  │呼叫客戶端│
│        │    │狀態轉換      │    │上傳至 Turnkey│  │         │
└────────┘    └──────────────┘    └──────────────┘  └──────────┘
                                    ↓
                    ┌───────────────────────────────┐
                    │        Database (資料庫)       │
                    ├───────────────────────────────┤
                    │INVOICE_ASSIGN_NO              │
                    │CUSTOMER_INVOICE_ASSIGN_NO     │
                    │INVOICE                        │
                    │INVOICE_ITEM                    │
                    │INVOICE_HISTORY                │
                    │IMPORT_FILE                    │
                    │IMPORT_FILE_LOG                │
                    └───────────────────────────────┘
                                    ↓
                    ┌───────────────────────────────┐
                    │  Turnkey 系統（財政部）       │
                    │  • 驗收 XML 配號檔             │
                    │  • 驗收 XML 發票檔             │
                    │  • 回傳驗收狀態               │
                    └───────────────────────────────┘
```

---

## 🔄 E0501 配號檔匯入流程

### 流程概述
E0501 是「營業人電子發票配號檔」，由財政部提供的空白配號，系統管理員轉接給具體客戶使用。

### 完整流程圖

```
使用者上傳 E0501 CSV
       ↓
[API] handleE0501CsvUpload()
       ↓
驗證客戶身份
       ↓
儲存檔案到伺服器
       ↓
計算 MD5 校驗碼
       ↓
建立 IMPORT_FILE 記錄
標記狀態: GatewayIn
       ↓
[非同步] processE0501Async()
       ↓
┌─────────────────────────────────────────────┐
│         E0501 檔案處理流程                   │
├─────────────────────────────────────────────┤
│                                             │
│ 1️⃣ 檢查 MD5 校驗碼                           │
│    • 檔案上傳時計算的 MD5                     │
│    • 與客戶提供的 MD5 比對                    │
│    • 若不符 → 記錄錯誤 → 終止處理             │
│                                             │
│ 2️⃣ 讀取檔案內容                              │
│    • 編碼：BIG5 或 UTF-8                     │
│    • 分隔符：逗號 (,)                        │
│    • 移除第一行（Header 列）                 │
│    • 逐行拆解為 7 個欄位                      │
│                                             │
│ 3️⃣ 逐筆驗證與處理                            │
│    ┌──────────────────────────────────┐    │
│    │ 對每一筆資料：                     │    │
│    │                                  │    │
│    │ • 驗證統編 (BAN) 格式              │    │
│    │   - 必須 8 碼數字                  │    │
│    │   - 檢查是否在系統中存在             │    │
│    │   - 對應 Customer 主檔              │    │
│    │                                  │    │
│    │ • 驗證發票類別                     │    │
│    │   - 07: 一般稅額計算               │    │
│    │   - 08: 特種稅額計算               │    │
│    │                                  │    │
│    │ • 驗證發票期別 (YearMonth)         │    │
│    │   - 格式：111/01~111/02 轉 11102  │    │
│    │   - 必須 5 碼純數字                │    │
│    │   - 末 2 碼必須為雙月 (02/04/..)  │    │
│    │                                  │    │
│    │ • 驗證發票字軌 (Track)             │    │
│    │   - 固定 2 碼英文大寫               │    │
│    │                                  │    │
│    │ • 驗證起號 (BeginNo)               │    │
│    │   - 8 碼純數字                     │    │
│    │   - 末 2 碼須為 00 或 50           │    │
│    │                                  │    │
│    │ • 驗證迄號 (EndNo)                 │    │
│    │   - 8 碼純數字                     │    │
│    │   - 末 2 碼須為 49 或 99           │    │
│    │   - 必須 ≥ 起號                   │    │
│    │                                  │    │
│    │ • 驗證號碼區間                     │    │
│    │   - (迄號 - 起號 + 1) 必須能被 50 整除 │    │
│    │   - 計算組數：差值 / 50             │    │
│    │                                  │    │
│    │ • 檢查區間重疊                     │    │
│    │   - 同期別/字軌不可與既有資料重疊   │    │
│    │                                  │    │
│    │ • 若驗證通過                       │    │
│    │   → 呼叫 addNewAndCreateInvoices  │    │
│    │   → 建立 InvoiceAssignNo           │    │
│    │   → 依據起迄號產生空白 Invoice     │    │
│    │                                  │    │
│    │ • 若驗證失敗                       │    │
│    │   → 記錄錯誤日誌                   │    │
│    │   → 跳過此筆，繼續下一筆            │    │
│    └──────────────────────────────────┘    │
│                                             │
│ 4️⃣ 儲存日誌                                  │
│    • IMPORT_FILE_LOG: 所有驗證結果          │
│    • 行號、錯誤/警告訊息、時間戳            │
│                                             │
│ 5️⃣ 更新狀態                                  │
│    • 無錯誤 → GatewayOK                     │
│    • 有錯誤 → GatewayFail                   │
│    • 記錄完成時間                           │
│                                             │
│ 6️⃣ 呼叫客戶端同步                           │
│    • 備份資料庫                             │
│    • 通知客戶端更新本地配號資訊              │
│    • 使用 WebSocket 推送通知                │
│                                             │
└─────────────────────────────────────────────┘
       ↓
檔案處理完成
狀態更新至 IMPORT_FILE_PROCESS_STATUS
       ↓
生成 CSV 回饋檔案 (可選)
       ↓
系統統計更新
```

### 資料庫變化

#### 建立的表記錄

**IMPORT_FILE**
```
ID          | 自動生成
FILE_FORMAT | INVOICE_ASSIGN_NO (E0501)
CUSTOMER_ID | 上傳客戶
FILE_NAME   | 原始檔名
FILE_NAME_ON_SERVER | 伺服器檔名
FILE_SIZE   | 檔案大小
FILE_CHECKSUM | MD5 校驗碼
GATEWAY_IN_TIME | 上傳時間
GATEWAY_DONE_TIME | 處理完成時間
PROCESS_STATUS | GatewayOK/GatewayFail
```

**INVOICE_ASSIGN_NO** (每成功驗證一筆就建立)
```
ID | 自動生成
UID | UUID (供同步用)
CUSTOMER_ID | 關聯到 Customer
INVOICE_TYPE | 07 或 08
YEAR_MONTH | 11102 等
INVOICE_TRACK | AB、CD 等
INVOICE_BEGIN_NO | 起號
INVOICE_END_NO | 迄號
INVOICE_BOOKLET | 組數 (計算值)
INVOICE_SOURCE_TYPE | TURNKEY
CREATED_AT | 建立時間
```

**INVOICE** (依據起迄號自動產生空白記錄)
```
ID | 自動生成
INVOICE_NUMBER | 如 AB00883662
INVOICE_STATUS | BLANK (空白)
YEAR_MONTH | 11102
INVOICE_TRACK | AB
... (其他欄位均為空)
```

**IMPORT_FILE_LOG** (每筆驗證結果)
```
IMPORT_FILE_ID | 關聯 IMPORT_FILE
LINE_NO | 資料行號
LOG_TYPE | INFO / WARNING / ERROR
MESSAGE | 驗證訊息
LOG_TIME | 時間戳
```

---

## 🔄 Invoice 發票資料匯入流程

### 流程概述
Invoice 是實際的發票資料，包含開立(C0401)、修正(A0401)、作廢(C0501)、折讓(C0701) 等多種訊息類別。

### 完整流程圖

```
使用者上傳 Invoice CSV
       ↓
[API] handleInvoiceCsvUpload()
       ↓
驗證客戶身份
       ↓
儲存檔案到伺服器
       ↓
計算 MD5 校驗碼
       ↓
建立 IMPORT_FILE 記錄
標記狀態: GatewayIn
       ↓
[非同步] processInvoiceAsync()
       ↓
┌─────────────────────────────────────────────────────────────┐
│         Invoice 檔案處理流程                                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 1️⃣ 檢查 MD5 校驗碼                                           │
│    (同 E0501)                                               │
│                                                             │
│ 2️⃣ 讀取檔案內容                                              │
│    • 編碼：UTF-8 (無 BOM)                                    │
│    • 分隔符：管線符 (|)                                      │
│    • 無 Header 列，直接開始資料                              │
│    • 逐行拆解為 30 個欄位                                    │
│                                                             │
│ 3️⃣ 逐筆處理 (按 MessageType 分類)                             │
│                                                             │
│    ┌─────────────────────────────────────────┐             │
│    │ MessageType 判定                         │             │
│    ├─────────────────────────────────────────┤             │
│    │                                         │             │
│    │ • C0401 (開立) / A0401 (修正)          │             │
│    │ • A0101 (更正)                          │             │
│    │ → 呼叫 toC0401Invoice()                 │             │
│    │   • 驗證 30 個欄位                      │             │
│    │   • 構建 Invoice entity                │             │
│    │   • 構建 InvoiceItem[] 明細            │             │
│    │   • 計算加總金額                        │             │
│    │   • 建立 InvoiceHistory 記錄             │             │
│    │   • 快取 C0401 發票 (供後續參考)        │             │
│    │                                         │             │
│    │ • C0501 (作廢) / A0501 (修正作廢)      │             │
│    │ • A0201 (更正作廢)                      │             │
│    │ → 呼叫 toC0501Invoice()                 │             │
│    │   • 查詢對應的 C0401 發票                │             │
│    │   • 更新 InvoiceStatus → VOID           │             │
│    │   • 記錄作廢原因/備註                    │             │
│    │   • 建立 InvoiceHistory (C0501 記錄)    │             │
│    │                                         │             │
│    │ • C0701 (折讓) / A0601 (修正)          │             │
│    │ • A0301 (更正)                          │             │
│    │ → 呼叫 toC0701Invoice()                 │             │
│    │   • 查詢對應的 C0401 發票                │             │
│    │   • 建立折讓記錄                         │             │
│    │   • 折讓金額為負數                       │             │
│    │   • 建立 InvoiceHistory (C0701 記錄)    │             │
│    │                                         │             │
│    └─────────────────────────────────────────┘             │
│                                                             │
│ 4️⃣ 多明細聚合                                               │
│    • 相同 invoiceNumber + invoiceDate                      │
│    • 系統自動聚合為單一發票                                  │
│    • 金額自動加總：                                          │
│      - salesAmount 加總                                    │
│      - freeTaxSalesAmount 加總                             │
│      - zeroTaxSalesAmount 加總                             │
│      - taxAmount 加總                                      │
│      - totalAmount 加總                                    │
│    • 明細行按 sequenceNumber 排序                            │
│                                                             │
│ 5️⃣ 欄位驗證細節 (InvoiceChecker)                             │
│                                                             │
│    關鍵驗證：                                                │
│    ✓ invoiceNumber: ^[A-Z]{2}[0-9]{8}$                     │
│    ✓ invoiceDate: yyyyMMdd 格式                            │
│    ✓ invoiceTime: HH:mm:ss 格式                            │
│    ✓ 統編格式: 8 位或 10 位數字                              │
│    ✓ 載具驗證:                                               │
│      - 3J0002: /[\\dA-Z0-9+-\\.]{7}                        │
│      - CQ0001: /[A-Z]{2}[0-9]{14}                          │
│    ✓ randomNumber: [0-9A]{4}                               │
│    ✓ 金額計算: totalAmount = salesAmount × (1+taxRate)     │
│    ✓ 捐贈驗證: donateMark=1 時 npoban 必填                  │
│    ✓ 稅率檢驗: 0.05 (5%) 或 0.00 (零稅)                     │
│                                                             │
│ 6️⃣ 狀態機制                                                  │
│    • 新增發票: InvoiceStatus.ISSUE (已開立)                 │
│    • 作廢發票: InvoiceStatus.VOID (已作廢)                   │
│    • 折讓發票: InvoiceStatus.ALLOWANCE (折讓)                │
│    • Turnkey 狀態:                                          │
│      - WaitTurnkey: 待上傳                                  │
│      - Confirmed: 已驗收                                    │
│      - Denied: 被拒                                        │
│                                                             │
│ 7️⃣ 建立相關記錄                                              │
│    • Invoice: 發票主檔 (1 筆 = 1 張發票)                     │
│    • InvoiceItem: 發票明細 (1 張發票可多筆明細)              │
│    • InvoiceHistory: 發票歷史 (記錄所有異動)                 │
│    • InvoiceItemHistory: 明細歷史                           │
│                                                             │
│ 8️⃣ 建立 Turnkey XML                                         │
│    • 依據 invoiceStatus 生成對應的 XML 檔案                  │
│    • 上傳至財政部 Turnkey 系統                                │
│    • 記錄上傳結果                                            │
│                                                             │
│ 9️⃣ 儲存日誌                                                  │
│    • IMPORT_FILE_LOG: 所有驗證結果                          │
│    • 行號、錯誤/警告訊息、時間戳                              │
│                                                             │
│ 🔟 更新狀態                                                  │
│    • 無錯誤 → GatewayOK                                     │
│    • 有錯誤 → GatewayFail                                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
       ↓
檔案處理完成
       ↓
呼叫客戶端同步
       ↓
排程: 生成 CSV 回饋檔案
       ↓
排程: 重送 C0401 (若 C0701 確認)
```

### 資料庫變化

#### 新建立的表記錄

**INVOICE**
```
ID | 自動生成
UID | UUID
INVOICE_NUMBER | AB12345678
INVOICE_STATUS | ISSUE/VOID/ALLOWANCE
INVOICE_DATE | 2025-01-13
SELLER_IDENTIFIER | 賣方統編
BUYER_IDENTIFIER | 買方統編
SALES_AMOUNT | 金額
TAX_AMOUNT | 稅額
TOTAL_AMOUNT | 合計
OWNER_ID | 客戶 ID
INVOICE_ASSIGN_NO_ID | 配號關聯
... (30+ 欄位)
```

**INVOICE_ITEM** (明細)
```
ID | 自動生成
INVOICE_ID | 關聯 INVOICE
DESCRIPTION | 品名
QUANTITY | 數量
UNIT_PRICE | 單價
AMOUNT | 金額
SEQUENCE_NUMBER | 序號
... 
```

**INVOICE_HISTORY** (歷史記錄)
```
ID | 自動生成
INVOICE_ID | 關聯 INVOICE
IMPORT_FILE_ID | 來源檔案
HISTORY_STATUS | C0401_RECEIVED/C0501_RECEIVED 等
MESSAGE_TYPE | 訊息類別
CREATED_AT | 建立時間
...
```

**INVOICE_ITEM_HISTORY** (明細歷史)
```
ID | 自動生成
INVOICE_HISTORY_ID | 關聯發票歷史
... 明細資訊
```

---

## 🔄 MessageType 處理邏輯

### C0401 - 開立發票
- **用途**：新建發票
- **處理**：
  - 驗證所有 30 個欄位
  - 建立 Invoice 記錄
  - 建立 InvoiceItem 記錄
  - InvoiceStatus = ISSUE (已開立)
  - 快取此發票供後續查詢

### A0401 - 修正發票
- **用途**：修改已開立發票（跨期更正）
- **處理**：
  - 與 C0401 相同驗證流程
  - 但標記為修正版本
  - 保留原發票記錄
  - 新增修正記錄

### A0101 - 更正發票
- **用途**：更正已開立發票（同期更正）
- **處理**：
  - 與 C0401 相同驗證流程
  - 標記為同期更正版本

### C0501 - 作廢發票
- **用途**：作廢已開立發票
- **處理**：
  1. 查詢對應的 C0401 發票
     - 根據 invoiceNumber + invoiceDate
  2. 若找到原發票
     - 更新 InvoiceStatus = VOID
     - 記錄作廢原因、時間、備註
     - 建立 C0501 InvoiceHistory
  3. 若未找到原發票
     - 記錄錯誤日誌
     - 建立孤立 C0501 記錄

### A0501 - 修正作廢
- **用途**：修改作廢狀態
- **處理**：
  - 查詢對應的 C0501 記錄
  - 更新作廢資訊

### A0201 - 更正作廢
- **用途**：更正作廢發票
- **處理**：
  - 查詢對應的 C0501 記錄
  - 標記為更正版本

### C0701 - 銷項折讓
- **用途**：退貨或折扣
- **處理**：
  1. 查詢原發票 (C0401)
  2. 建立折讓記錄
  3. 金額為負數
  4. InvoiceStatus = ALLOWANCE

### A0601 - 修正折讓
- **用途**：修改折讓狀態
- **處理**：
  - 查詢對應的 C0701 記錄
  - 更新折讓資訊

### A0301 - 更正折讓
- **用途**：更正折讓發票
- **處理**：
  - 查詢對應的 C0701 記錄
  - 標記為更正版本

---

## 📤 後續處理流程

### 1️⃣ Turnkey XML 生成與上傳

```
Invoice 匯入成功
       ↓
呼叫 TurnkeyHelper 生成 XML
       ↓
分類生成：
• invoice_c0401_*.xml (開立)
• invoice_c0501_*.xml (作廢)
• invoice_c0701_*.xml (折讓)
       ↓
上傳至財政部 Turnkey 系統
       ↓
更新 invoiceTurnkeyStatus
       ↓
等待 Turnkey 驗收回覆
```

### 2️⃣ 客戶端同步

```
檔案處理完成 (E0501 或 Invoice)
       ↓
備份資料庫 (BackupDatabaseJob)
       ↓
查詢 CustomerSecurity
       ↓
透過 WebSocket 推送通知
       └─→ simpMessagingTemplate.convertAndSendToUser()
       ↓
客戶端收到通知
       ↓
客戶端主動拉取更新
       ↓
客戶端本地資料庫同步
```

### 3️⃣ CSV 回饋檔案生成

```
[定時排程] GenerateCsvFeedbackFileJob
       ↓
每日定時執行 (預設午夜)
       ↓
查詢待回饋的 Invoice
       ↓
產生回饋 CSV 檔案
       ↓
格式：
• invoiceNumber
• invoiceTurnkeyStatus
• 驗收結果
• 錯誤訊息(若有)
       ↓
客戶端下載
       ↓
用於對帳
```

### 4️⃣ C0401 重送排程

```
[定時排程] ReSendC0401Job
       ↓
在 C0701 (折讓) 上傳確認後運行
       ↓
查詢同期別內 InvoiceStatus=VOID
且 invoiceTurnkeyStatus=Confirmed 的發票
       ↓
重新產生 C0401 XML
       ↓
重新上傳至 Turnkey
       ↓
用途：
• 折讓確認後，原發票狀態需更新至 Turnkey
• 通知財政部已折讓金額
```

---

## 🔐 驗證規則總結

### E0501 驗證項

| 欄位 | 規則 |
|-----|------|
| 統編 | 8 位數字，必須在 Customer 中存在 |
| 類別 | 07 或 08 |
| 期別 | yyyy/mm~yyyy/mm 轉換為 5 碼(yyMM)，末 2 碼為雙月 |
| 字軌 | 2 位大寫英文 |
| 起號 | 8 位數字，末 2 碼為 00 或 50 |
| 迄號 | 8 位數字，末 2 碼為 49 或 99，≥ 起號 |
| 區間 | (迄號-起號+1) ÷ 50 必須整除，且無重疊 |

### Invoice 驗證項

| 欄位 | 規則 |
|-----|------|
| messageType | C0401/A0401/A0101/C0501/C0701 等 |
| invoiceNumber | ^[A-Z]{2}[0-9]{8}$ |
| invoiceDate | yyyyMMdd (8 碼) |
| invoiceTime | HH:mm:ss (8 碼含冒號) |
| 統編 | 8 位(賣方) 或 10 位(買方)，若無則 0000000000 |
| 載具 | 3J0002: /+7碼; CQ0001: /14碼 |
| randomNumber | [0-9A]{4} |
| 稅率 | 0.05 (5%) 或 0.00 (零稅) |
| 金額 | totalAmount = salesAmount × (1+taxRate) - discount |
| 捐贈 | donateMark=1 時 npoban 必填 |

---

## 📊 狀態機制

### ImportFile 狀態流轉

```
GatewayIn (接收入)
    ↓
GatewayProcessing (處理中)
    ├─→ GatewayOK (成功) ──→ 完成
    └─→ GatewayFail (失敗) ──→ 待人工處理
```

### Invoice 狀態流轉

```
BLANK (空白，來自 E0501)
    ↓
ISSUE (已開立，C0401)
    ├─→ VOID (已作廢，C0501)
    └─→ ALLOWANCE (折讓，C0701)
```

### InvoiceTurnkeyStatus 狀態流轉

```
WaitTurnkey (待上傳)
    ↓
TurnkeySent (已上傳)
    ├─→ Confirmed (已驗收)
    └─→ Denied (被拒)
```

---

## 🔗 Entity 關聯圖

```
                    ┌──────────────┐
                    │   Customer   │
                    │   (客戶)      │
                    └──────────────┘
                           ↑
                    ┌──────┴──────┐
                    │             │
        ┌───────────▼──┐   ┌──────▼────────────┐
        │InvoiceAssignNo│   │CustomerInvoiceAssignNo│
        │  (配號主檔)   │   │  (客戶配號檔)      │
        └───────────┬──┘   └──────┬────────────┘
                    │             │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   Invoice   │
                    │  (發票主檔)  │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
    ┌───▼────┐    ┌────────▼────────┐    ┌───▼──────────┐
    │Inv Item │    │Inv History      │    │Customer Inv  │
    │(明細)   │    │(發票歷史)       │    │AssignNo      │
    └────────┘    └────────┬────────┘    └──────────────┘
                           │
                    ┌──────▼──────┐
                    │ Inv Item    │
                    │  History    │
                    │ (明細歷史)   │
                    └─────────────┘

┌──────────────────────────────────────────────┐
│              ImportFile                       │
│          (匯入檔案記錄)                       │
├──────────────────────────────────────────────┤
│  • 關聯 Customer                             │
│  • 記錄 ImportFileLog 清單                   │
│  • 關聯多筆 InvoiceHistory                   │
└──────────────────────────────────────────────┘
```

---

## 💾 核心業務流程總結

### E0501 核心流程 (4 步)
1. **驗證** - MD5、欄位格式、統編存在、區間無重疊
2. **建立** - 建立 InvoiceAssignNo + 空白 Invoice 記錄
3. **紀錄** - ImportFileLog 記錄所有驗證結果
4. **同步** - 通知客戶端、備份資料庫

### Invoice 核心流程 (5 步)
1. **驗證** - MD5、30 個欄位格式、邏輯一致性
2. **分類** - 按 MessageType 分為 C0401/C0501/C0701 處理
3. **聚合** - 相同發票號多明細自動加總
4. **建立** - 建立 Invoice/InvoiceItem/InvoiceHistory 記錄
5. **上傳** - 生成 XML、上傳至 Turnkey、記錄狀態

### 後續流程 (3 個排程)
1. **CSV 回饋** - 每日產生回饋檔供客戶對帳
2. **C0401 重送** - 折讓確認後重新上傳原發票
3. **資料庫備份** - 每次匯入後自動備份

---

## 📈 系統性能特點

- **非同步處理**：大型檔案使用 @Async 避免阻塞
- **交易管理**：關鍵操作使用 REQUIRES_NEW 隔離
- **快取機制**：C0401 快取供後續 C0501/C0701 查詢
- **詳細日誌**：行級別錯誤追蹤，便於問題診斷
- **增量備份**：每次匯入後自動備份，支援增量同步
- **推送通知**：WebSocket 即時推送客戶端更新
- **優雅降級**：單筆驗證失敗不影響其他筆次

---

**文件版本**：1.0  
**建立時間**：2025-01-13  
**涵蓋範圍**：E0501 配號檔、Invoice 發票資料完整業務流程
