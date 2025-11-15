# 舊制 A/B/C/D → 新制 F/G 完整欄位對照（MIG 4.x 以 F/G 為唯一輸出）

> 依據：`turnbridge-srs-v1.0.md`、`DECISION_LOG_v0.3.md`、`mig4.0-changelog.md`
> 原則：
>
> 1. Turnkey XML **僅輸出 F/G 新制**（F0401/F0501/F0701、G0401/G0501）
> 2. 仍接受舊制（A/B/C/D）上傳；**CSV 需保留 `legacyType` 與 `rawLine`**，並映射成新制欄位入庫
> 3. **單檔 999 明細；不可拆單**（發票的所有明細需在同一檔）

---

## 0. 名詞速覽

- F0401：平台存證 **開立發票**（合併 A0401、C0401）
- F0501：平台存證 **作廢發票**（合併 A0501、C0501）
- F0701：平台存證 **註銷發票**（取代 C0701）
- G0401：平台存證 **開立/傳送折讓證明單**（合併 B0401、D0401）
- G0501：平台存證 **作廢折讓證明單**（合併 B0501、D0501）

---

## 1) A0401/C0401 → F0401（開立發票）

> 本節分兩層：
> 1. 高階欄位對照（邏輯欄位）
> 2. **客戶 CSV C0401 索引版欄位對照**（實際 index → F0401）

### 1.0 高階欄位對照（邏輯欄位）

| 舊制欄位（示意）                  | 新制 F0401 欄位                | 轉換規則                            |
| -------------------------------- | ----------------------------- | ----------------------------------- |
| `InvoiceNumber`                  | `InvoiceNo`                   | 保留字軌+8碼（例：AB12345678）     |
| `InvoiceDate` + `InvoiceTime`    | `DateTime`                    | 合併為 ISO8601（含時區）           |
| `SellerId`                       | `Seller.Identifier`           | 統編                               |
| `SellerName`                     | `Seller.Name`                 |                                     |
| `BuyerId` / `Identifier`         | `Buyer.Identifier`            | 統編 / 載具碼（需正規化）          |
| `BuyerName`                      | `Buyer.Name`                  | 長度依 MIG                         |
| `MainRemark`                     | `MainRemark`                  | 文字欄位                           |
| `SalesAmount`                    | `Amount.SalesAmount`          | 金額精度依 MIG                     |
| `TaxAmount`                      | `Amount.TaxAmount`            | 稅額                               |
| `TotalAmount`                    | `Amount.TotalAmount`          | 合計金額                           |
| `TaxType`                        | `Amount.TaxType`              | 1=應稅/2=零稅/3=免稅…             |
| `ItemName[]`                     | `Details[].Description`       | 一對一映射                         |
| `ItemCount[]`                    | `Details[].Quantity`          | 數量                               |
| `ItemPrice[]`                    | `Details[].UnitPrice`         | 單價                               |
| `ItemAmount[]`                   | `Details[].Amount`            | 小計                               |
| *(保留)*                          | `legacyType`                  | 固定寫入 `A0401` 或 `C0401`        |
| *(保留)*                          | `rawLine`                     | 放原始 CSV 行文字                   |

**必填檢核（節錄）**

- `sum(Details[].Amount) + TaxAmount == TotalAmount`
- 稅別/小數位符合 MIG
- 期別、字軌、號碼合法（由後端綜合驗證）

---

### 1.1 客戶 CSV C0401 欄位對照 → F0401

> CSV 為 `|` 分隔，無欄位名稱。  
> `C0401` 每一行代表一張發票的一個明細，需以 `[1] 發票號碼` group。

#### 1.1.1 Main（發票主檔）

```text
C0401|QQ12345678|2013-09-11|12:00:00|27216897|鎰德資訊有限公司|12345678|12345678|07|0|3J0002|/31K2UFO|/31K2UFO|N| |1234|...
```

| CSV idx | 名稱             | 說明                        | F0401 欄位                 | 備註                    |
| ------: | -------------- | ------------------------- | ------------------------ | --------------------- |
|       0 | 表格名稱           | `C0401`                   | （僅用於 routing，不入 F0401）   | 用來判斷 legacyType       |
|       1 | 發票號碼           | 例：`QQ12345678`            | `Main.InvoiceNumber`     | 必填                    |
|       2 | 發票日期           | `YYYY-MM-DD` 或 `YYYYMMDD` | `Main.InvoiceDate`       | 需轉成 MIG 日期格式          |
|       3 | 發票時間           | `HH:mm:ss`                | `Main.InvoiceTime`       |                       |
|       4 | 賣方統一編號         | 例：`27216897`              | `Main.Seller.Identifier` | BAN                   |
|       5 | 賣方名稱           |                           | `Main.Seller.Name`       |                       |
|       6 | 買方統一編號         | 無統編→`0000000000`          | `Main.Buyer.Identifier`  | B2C 可約定保留 10 個 0      |
|       7 | 買方名稱/代碼        | 有統編→統編；無統編→`0000`         | `Main.Buyer.Name`        |                       |
|       8 | 發票類別           | 例：`07`                    | `Main.InvoiceType`       | 依 MIG InvoiceTypeEnum |
|       9 | 捐贈註記 `※`       | `0`：非捐贈、`1`：捐贈            | `Main.DonateMark`        | **前台必填**              |
|      10 | 載具類別號碼 `※`     | 例：`3J0002`                | `Main.CarrierType`       | 手機條碼固定 3J0002         |
|      11 | 載具顯碼 Id `※`    | 例：`/31K2UFO`              | `Main.CarrierId1`        |                       |
|      12 | 載具隱碼 Id2 `※`   | 無資料時可重複 11 值              | `Main.CarrierId2`        |                       |
|      13 | 紙本已列印註記 `※`    | `Y` / `N`                 | `Main.PrintMark`         |                       |
|      14 | 發票捐贈對象（愛心碼）`※` | 有愛心碼→填碼，無→單一空白            | `Main.NPOBAN`            | 需搭配 DonateMark=1      |
|      15 | 防偽隨機碼          | 4 碼                       | `Main.RandomNumber`      |                       |

> `legacyType` 固定填入 `C0401`，`rawLine` 存整行原文字。

#### 1.1.2 Details（明細）

> 同一張發票（同 `[1]`）可能有多筆明細，每行對應一個 `ProductItem`。

| CSV idx | 名稱     | 說明              | F0401 欄位                             | 備註                  |
| ------: | ------ | --------------- | ------------------------------------ | ------------------- |
|      16 | 品名     | 銷售中文            | `Details.ProductItem.Description`    |                     |
|      17 | 數量     |                 | `Details.ProductItem.Quantity`       |                     |
|      18 | 單價     |                 | `Details.ProductItem.UnitPrice`      |                     |
|      19 | 金額     | 該明細未稅金額         | `Details.ProductItem.Amount`         | 建議與計算結果檢核           |
|      20 | 明細排列序號 | 單張發票第幾種商品       | `Details.ProductItem.SequenceNumber` |                     |
|      24 | 課稅別    | 1=應稅、2=零稅、3=免稅… | `Details.ProductItem.TaxType`        | 與 Amount.TaxType 同源 |

> MIG 中 `Unit`、`Remark`、`RelateNumber` 等欄位，CSV 規格未提供 →
> 可預設為空字串或 null，僅在需要時擴充。

#### 1.1.3 Amount（彙總）

> 客戶 CSV 將彙總金額寫在同一行的後段欄位，實作上建議：
>
> * 仍以所有明細重新計算 Sales/Free/Zero/Tax/Total
> * 與 CSV 欄位 cross-check，不一致則記入 ImportFileLog。

| CSV idx | 名稱          | 說明              | F0401 欄位                     | 備註              |
| ------: | ----------- | --------------- | ---------------------------- | --------------- |
|      21 | 應稅銷售額合計 `※` | 新台幣             | `Amount.SalesAmount`         | 建議重算+比對         |
|      22 | 免稅銷售額合計 `※` | 新台幣             | `Amount.FreeTaxSalesAmount`  | 同上              |
|      23 | 零稅銷售額合計 `※` | 新台幣             | `Amount.ZeroTaxSalesAmount`  | 同上              |
|      24 | 課稅別         | 1=應稅、2=零稅、3=免稅… | `Amount.TaxType`             | 若全明細同稅別可直接使用    |
|      25 | 稅率          | 例：`0.05`        | `Amount.TaxRate`             |                 |
|      26 | 營業稅（該明細）    | 範例：`15`         | （建議加總後寫入 `Amount.TaxAmount`） | 此欄多行加總          |
|      27 | 發票總計        | 整張發票總金額         | `Amount.TotalAmount`         | MIG 規則校驗        |
|      28 | 扣抵金額        |                 | （無對應 F0401 欄位）               | 建議入系統自有欄位       |
|      29 | 信用卡號末四碼     |                 | （無對應 F0401 欄位）               | 同上，建議入 ext JSON |

---

## 2) A0501/C0501 → F0501（作廢發票）

### 2.0 高階欄位對照（邏輯欄位）

| 舊制欄位                        | 新制 F0501 欄位           | 轉換規則            |
| --------------------------- | --------------------- | --------------- |
| `InvoiceNumber`             | `CancelInvoiceNumber` | 原發票號碼           |
| `InvoiceDate`               | `InvoiceDate`         | 原發票日期           |
| `CancelDate` + `CancelTime` | `CancelDate`/`Time`   | 依 MIG（不強制 ISO）  |
| `CancelReason`/`ReasonCode` | `CancelReason`        | 字串／代碼           |
| *(保留)*                      | `legacyType`          | `A0501`/`C0501` |
| *(保留)*                      | `rawLine`             | 原始行             |

**注意**：F0501 是「作廢」，與 F0701（註銷）語意不同。

---

### 2.1 客戶 CSV C0501 欄位對照 → F0501

```text
C0501|PK00000007|2013-10-25|0000000000|27216897|2013-10-26|15:00:00|開立錯誤| | 
```

| CSV idx | 名稱       | 說明               | F0501 欄位                  |
| ------: | -------- | ---------------- | ------------------------- |
|       0 | 表格名稱     | `C0501`          | 不入 XML，用於 `legacyType`    |
|       1 | 發票號碼     | 被作廢之發票號碼         | `CancelInvoiceNumber`     |
|       2 | 發票日期     | 原發票日期            | `InvoiceDate`             |
|       3 | 買方統一編號   | 無統編→`0000000000` | `BuyerId`                 |
|       4 | 賣方統一編號   |                  | `SellerId`                |
|       5 | 作廢日期     |                  | `CancelDate`              |
|       6 | 作廢時間     |                  | `CancelTime`              |
|       7 | 作廢原因     | 例：`開立錯誤`         | `CancelReason`            |
|       8 | 專案作廢核准文號 | 原則上不會發生，無→單一空白   | `ReturnTaxDocumentNumber` |
|       9 | 備註       |                  | `Remark`                  |

* `legacyType`：固定寫入 `C0501`
* `rawLine`：存放整行原始 CSV

---

## 3) C0701 → F0701（註銷發票）

### 3.0 高階欄位對照（邏輯欄位）

| 舊制欄位                    | 新制 F0701 欄位           | 轉換規則    |
| ----------------------- | --------------------- | ------- |
| `InvoiceNumber`         | `VoidInvoiceNumber`   | 原發票號    |
| `VoidDate` + `VoidTime` | `VoidDate`/`VoidTime` | 依 MIG   |
| `VoidReason`            | `VoidReason`          | 字串      |
| *(保留)*                  | `legacyType`          | `C0701` |
| *(保留)*                  | `rawLine`             | 原始行     |

### 3.1 客戶 CSV C0701 欄位對照 → F0701

```text
C0701|PK12345678|20131025|0000000000|27216897|20131026|15:00:10|開立錯誤| | |
```

| CSV idx | 名稱       | 說明                    | F0701 欄位              | 備註 |
| ------: | -------- | --------------------- | --------------------- | -- |
|       0 | 表格名稱     | `C0701`               | 不入 XML，做 `legacyType` |    |
|       1 | 發票號碼     | 被註銷之發票號碼              | `VoidInvoiceNumber`   |    |
|       2 | 發票日期     | 原發票日期                 | `InvoiceDate`         |    |
|       3 | 買方統一編號   | 無統編→`0000000000`      | `BuyerId`             |    |
|       4 | 賣方統一編號   |                       | `SellerId`            |    |
|       5 | 註銷日期     |                       | `VoidDate`            |    |
|       6 | 註銷時間     |                       | `VoidTime`            |    |
|       7 | 註銷原因     | 例：`開立錯誤`              | `VoidReason`          |    |
|       8 | 專案作廢核准文號 | F0701 無對應欄位（可入內部擴充欄位） | -                     |    |
|       9 | 備註       |                       | `Remark`              |    |

---

## 4) B0401/D0401 → G0401（開立/傳送折讓）

| 舊制欄位              | 新制 G0401 欄位                       | 轉換規則            |
| ----------------- | --------------------------------- | --------------- |
| `AllowanceNumber` | `AllowanceNumber`                 | 折讓單號            |
| `InvoiceNumber`   | `Details[].OriginalInvoiceNumber` | 關聯原發票           |
| `Reason`          | `MainRemark` / `Details.Remark`   | 文字              |
| `SalesAmount`     | `Amount.TotalAmount`              | 金額              |
| `TaxAmount`       | `Amount.TaxAmount`                | 稅額              |
| `TotalAmount`     | （依 MIG 規則）                        | 合計              |
| 明細群               | `Details[]`                       | 名稱/數量/金額        |
| *(保留)*            | `legacyType`                      | `B0401`/`D0401` |
| *(保留)*            | `rawLine`                         | 原始行             |

---

## 5) B0501/D0501 → G0501（作廢折讓）

| 舊制欄位                        | 新制 G0501 欄位             | 轉換規則            |
| --------------------------- | ----------------------- | --------------- |
| `AllowanceNumber`           | `CancelAllowanceNumber` | 原折讓單號           |
| `CancelReason`              | `CancelReason`          | 文字／代碼           |
| `CancelDate` + `CancelTime` | `CancelDate`/`Time`     | 依 MIG           |
| *(保留)*                      | `legacyType`            | `B0501`/`D0501` |
| *(保留)*                      | `rawLine`               | 原始行             |

---

## 6) CSV 標頭建議（新制）

> 若將來不再接受舊制，建議前台/Agent 直接輸出「新制欄位」，後端僅負責轉 XML。

**F0401（開立）範例**

```text
Type,InvoiceNo,Date,Time,SellerId,SellerName,BuyerId,BuyerName,InvoiceType,DonateMark,CarrierType,CarrierId1,CarrierId2,PrintMark,NPOBAN,RandomNumber,ItemDescription,ItemQty,ItemUnitPrice,ItemAmount,ItemSeq,SalesAmount,FreeTaxSalesAmount,ZeroTaxSalesAmount,TaxType,TaxRate,TaxAmount,TotalAmount,DeductionAmount,CreditCardLast4,legacyType,rawLine
```

**G0401（折讓）範例**

```text
Type,AllowanceNo,AllowanceDate,SellerId,SellerName,BuyerId,BuyerName,AllowanceType,OriginalInvoiceSellerId,OriginalInvoiceBuyerId,OriginalInvoiceDate,OriginalInvoiceNumber,OriginalSequenceNumber,OriginalDescription,Quantity,Unit,UnitPrice,Amount,Tax,AllowanceSequenceNumber,TaxType,TaxAmount,TotalAmount,legacyType,rawLine
```

> 若上游仍送舊制：**仍可使用上述新制欄位命名**，但：
>
> * `legacyType` 填入舊制代號（例如 `C0401`）
> * `rawLine` 放原始行
> * 其他欄位則填入**映射後的新制值**（而非原始值）

---

```

如果你接下來想要，我也可以幫你產一個 `CsvToFgMapper` 的 Java skeleton，把這份 mapping 直接轉成程式框架（包含 group by 發票號碼、多筆明細組裝、金額重算與檢核）。
```
