# CSV → FG Mapping 規格（C0401 / C0501 / C0701 → F0401 / F0501 / F0701）

> 版本：v1.0  
> 來源：客戶 CSV 3.2.1 版規格 + MIG4.1（F0401/F0501/F0701）  
> 說明：  
> - C0401：正常交易 → F0401 平台存證開立發票  
> - C0501：作廢交易 → F0501 平台存證作廢發票  
> - C0701：註銷交易 → F0701 平台存證註銷發票  
> - CSV 以 `|` 分隔，無欄位名稱，需依 index 定義。  
> - `※` 欄位為 **前台必須提供、系統無法推導** 的欄位。

---

## 1. 共通概念

### 1.1 Record Type 判斷

| CSV index | 範例值  | 意義                   | 對應訊息 |
|----------:|---------|------------------------|----------|
| 0         | C0401   | 正常交易（開立發票）   | F0401    |
| 0         | C0501   | 作廢發票               | F0501    |
| 0         | C0701   | 註銷發票               | F0701    |

> 實作上：先依欄位 `[0]` 分流，再依發票號碼 `[1]` 將多筆明細 group 成一張發票。

---

## 2. C0401 → F0401 Mapping（開立發票）

### 2.1 CSV 範例（摘要）

```text
C0401|QQ12345678|2013-09-11|12:00:00|27216897|鎰德資訊有限公司|12345678|12345678|07|0|3J0002|/31K2UFO|/31K2UFO|N| |1234|92無鉛汽油|10|32|320|1|320|0|0|1|0.05|15|320|20|1234
```

### 2.2 Main（發票主檔）欄位

| CSV idx | 名稱             | 說明                        | F0401 欄位                         | 備註                          |
| ------: | -------------- | ------------------------- | -------------------------------- | --------------------------- |
|       0 | 表格名稱           | C0401                     | （僅用於 routing）                    |                             |
|       1 | 發票號碼           | 例：`QQ12345678`            | `Invoice.Main.InvoiceNumber`     | 必填                          |
|       2 | 發票日期           | `YYYY-MM-DD` 或 `YYYYMMDD` | `Invoice.Main.InvoiceDate`       | 需轉為 MIG 日期格式（純數字）           |
|       3 | 發票時間           | `HH:mm:ss`                | `Invoice.Main.InvoiceTime`       |                             |
|       4 | 賣方統一編號         | 例：`27216897`              | `Invoice.Main.Seller.Identifier` | BAN                         |
|       5 | 賣方名稱           |                           | `Invoice.Main.Seller.Name`       |                             |
|       6 | 買方統一編號         | 無統編 → `0000000000`        | `Invoice.Main.Buyer.Identifier`  | B2C 可約定仍填 `0000000000` 或轉空值 |
|       7 | 買方名稱/代碼        | 有統編→統編；無統編→`0000`         | `Invoice.Main.Buyer.Name`        |                             |
|       8 | 發票類別           | 例：`07`                    | `Invoice.Main.InvoiceType`       | 依 MIG InvoiceTypeEnum       |
|       9 | 捐贈註記 `※`       | `0`：非捐贈、`1`：捐贈            | `Invoice.Main.DonateMark`        | 前台必填，後端不推導                  |
|      10 | 載具類別號碼 `※`     | 例：`3J0002`                | `Invoice.Main.CarrierType`       | 手機條碼固定 `3J0002`             |
|      11 | 載具顯碼 Id `※`    | 例：`/31K2UFO`              | `Invoice.Main.CarrierId1`        |                             |
|      12 | 載具隱碼 Id2 `※`   | 空則可沿用欄 11                 | `Invoice.Main.CarrierId2`        |                             |
|      13 | 紙本已列印註記 `※`    | `Y` / `N`                 | `Invoice.Main.PrintMark`         |                             |
|      14 | 發票捐贈對象（愛心碼）`※` | 有愛心碼→填碼，無→單一空白            | `Invoice.Main.NPOBAN`            | 僅在 DonateMark=1 時有意義        |
|      15 | 防偽隨機碼          | 4 碼                       | `Invoice.Main.RandomNumber`      |                             |

> Remainder fields (16+)，見 2.3、2.4。

---

### 2.3 Details（明細）欄位

> 同一張發票（同一 `[1] 發票號碼`），可能有多筆不同品項：
> 每一行 C0401 對應 F0401 的一個 `ProductItem`。

| CSV idx | 名稱     | 說明              | F0401 欄位                                     | 備註                   |
| ------: | ------ | --------------- | -------------------------------------------- | -------------------- |
|      16 | 品名     | 銷售中文            | `Invoice.Details.ProductItem.Description`    |                      |
|      17 | 數量     |                 | `Invoice.Details.ProductItem.Quantity`       |                      |
|      18 | 單價     |                 | `Invoice.Details.ProductItem.UnitPrice`      |                      |
|      19 | 金額     | 該明細之未稅金額        | `Invoice.Details.ProductItem.Amount`         | 與後續彙總需一致             |
|      20 | 明細排列序號 | 單張發票第幾種商品       | `Invoice.Details.ProductItem.SequenceNumber` |                      |
|      24 | 課稅別    | 1=應稅、2=零稅、3=免稅⋯ | `Invoice.Details.ProductItem.TaxType`        | 同時也影響 Amount.TaxType |

> ● `Unit`（單位）、`Remark`、`RelateNumber` 等 MIG 欄位，CSV 規格中未提供 →
> 可預設 `Unit = ""`、`Remark = ""`、`RelateNumber = ""`。

---

### 2.4 Amount（彙總）欄位

> 彙總欄位通常只在「最後一筆明細」或「重複於所有明細列」，
> **實作上建議以「同一發票的所有列計算 sum 後，與 CSV 欄位 cross-check」**。

| CSV idx | 名稱          | 說明                | F0401 欄位                            | 備註                     |
| ------: | ----------- | ----------------- | ----------------------------------- | ---------------------- |
|      21 | 應稅銷售額合計 `※` | 新台幣               | `Invoice.Amount.SalesAmount`        | 推薦由系統重算，與此欄比對          |
|      22 | 免稅銷售額合計 `※` | 新台幣               | `Invoice.Amount.FreeTaxSalesAmount` | 同上                     |
|      23 | 零稅銷售額合計 `※` | 新台幣               | `Invoice.Amount.ZeroTaxSalesAmount` | 同上                     |
|      24 | 課稅別         | 1：應稅、2：零稅、3：免稅⋯   | `Invoice.Amount.TaxType`            | 若整張皆同一種 TaxType 即可直接使用 |
|      25 | 稅率          | 例：`0.05`          | `Invoice.Amount.TaxRate`            |                        |
|      26 | 營業稅（該明細）    | 例：`15`            | （**無直接 F0401 欄位**）                  | 建議加總後寫入 TaxAmount      |
|      27 | 發票總計        | 整張發票總金額（含稅或不含稅依規） | `Invoice.Amount.TotalAmount`        |                        |
|      28 | 扣抵金額        |                   | **無對應 F0401 欄位**                    | 可存入內部 DB 擴充欄位          |
|      29 | 信用卡末四碼      |                   | **無對應 F0401 欄位**                    | 建議存於內部 Ext 欄位          |

**建議計算：**

* `TaxAmount`（F0401.Amount.TaxAmount） = 同一發票所有行的 `[26]` 加總
* 同時檢查：`SalesAmount + FreeTaxSalesAmount + ZeroTaxSalesAmount + TaxAmount == TotalAmount`（依 MIG 規則調整）

---

## 3. C0501 → F0501（作廢發票）

### 3.1 CSV 範例

```text
C0501|PK00000007|2013-10-25|0000000000|27216897|2013-10-26|15:00:00|開立錯誤| | 
```

> C0501 與 C0701 欄位相同，意義不同（作廢 vs 註銷）。

### 3.2 C0501 → F0501 Mapping

| CSV idx | 名稱       | 說明               | F0501 欄位                  |
| ------: | -------- | ---------------- | ------------------------- |
|       0 | 表格名稱     | `C0501`          | （僅 routing，用於判斷 F0501）    |
|       1 | 發票號碼     | 被作廢之發票號碼         | `CancelInvoiceNumber`     |
|       2 | 發票日期     | 原發票日期            | `InvoiceDate`             |
|       3 | 買方統一編號   | 無統編→`0000000000` | `BuyerId`                 |
|       4 | 賣方統一編號   |                  | `SellerId`                |
|       5 | 作廢日期     |                  | `CancelDate`              |
|       6 | 作廢時間     |                  | `CancelTime`              |
|       7 | 作廢原因     | 例：`開立錯誤`         | `CancelReason`            |
|       8 | 專案作廢核准文號 | 原則上不會發生，無則單一空白   | `ReturnTaxDocumentNumber` |
|       9 | 備註       |                  | `Remark`                  |

---

## 4. C0701 → F0701（註銷發票）

### 4.1 CSV 範例

```text
C0701|PK12345678|20131025|0000000000|27216897|20131026|15:00:10|開立錯誤| | |
```

### 4.2 C0701 → F0701 Mapping

| CSV idx | 名稱       | 說明               | F0701 欄位               | 備註 |
| ------: | -------- | ---------------- | ---------------------- | -- |
|       0 | 表格名稱     | `C0701`          | （僅 routing，用於判斷 F0701） |    |
|       1 | 發票號碼     | 被註銷之發票號碼         | `VoidInvoiceNumber`    |    |
|       2 | 發票日期     | 原發票日期            | `InvoiceDate`          |    |
|       3 | 買方統一編號   | 無統編→`0000000000` | `BuyerId`              |    |
|       4 | 賣方統一編號   |                  | `SellerId`             |    |
|       5 | 註銷日期     |                  | `VoidDate`             |    |
|       6 | 註銷時間     |                  | `VoidTime`             |    |
|       7 | 註銷原因     | 例：`開立錯誤`         | `VoidReason`           |    |
|       8 | 專案作廢核准文號 | **F0701 無對應欄位**  | （可入內部擴充欄位）             |    |
|       9 | 備註       |                  | `Remark`               |    |

---

## 5. 實作建議

1. **以「發票號碼 + 訊息別」為 group key**

   * C0401 多列 → 一張 F0401（多個 ProductItem）
   * C0501、C0701 通常單列 → 一筆 F0501/F0701。

2. **※ 欄位必須由前台提供，不可省略**

   * 捐贈註記（欄 9）
   * 載具類別 / 顯碼 / 隱碼（欄 10~12）
   * 列印註記（欄 13）
   * 愛心碼（欄 14）
   * 應稅 / 免稅 / 零稅額合計（欄 21~23）
     → 這些應在「匯出 CSV 的 POS/加值前端」先計算好。

3. **彙總欄位建議「重算 + cross-check」**

   * 由所有明細金額（欄 19）及課稅別（欄 24）重新計出 Sales/Free/Zero，
   * 與欄位 21/22/23/27 比對，不一致時視為錯誤（ImportFileLog 記錄）。

4. **無對應 F0401 的欄位（28 扣抵金額 / 29 信用卡末四碼）**

   * 建議存入 Turnbridge 內部的擴充欄位（例如 `extra_json`），
   * 方便日後查核，但不送入 Turnkey XML。

---

> 如需，我可以再幫你把這份 Mapping 拆成
> `docs/turnkey/AGENTS_MAPPING_v1.md` 裡的其中一節，
> 或直接產一個「Java/Enum-based Mapper Skeleton」（例如 `CsvToFgMapper.java`），
> 把上面 Mapping 轉成程式碼框架。

