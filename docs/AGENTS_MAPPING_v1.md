# 舊制 A/B/C/D → 新制 F/G 完整欄位對照（MIG 4.x 以 F/G 為唯一輸出）

> 依據：`turnbridge-srs-v1.0.md`、`DECISION_LOG_v0.3.md`、`mig4.0-changelog.md`
> 原則：
>
> 1. Turnkey XML **僅輸出 F/G 新制**（F0401/F0501/F0701、G0401/G0501）
> 2. 仍接受舊制（A/B/C/D）上傳；**CSV 需保留 `legacyType` 與 `rawLine`**，並映射成新制欄位入庫
> 3. **單檔 999 明細；不可拆單**（發票的所有明細需在同一檔）

## 0. 名詞速覽

- F0401：平台存證 **開立發票**（合併 A0401、C0401）
- F0501：平台存證 **作廢發票**（合併 A0501、C0501）
- F0701：平台存證 **註銷發票**（取代 C0701）
- G0401：平台存證 **開立/傳送折讓證明單**（合併 B0401、D0401）
- G0501：平台存證 **作廢折讓證明單**（合併 B0501、D0501）

---

## 1) A0401/C0401 → F0401（開立發票）

| 舊制欄位（示意）                      | 新制 F0401 欄位           | 轉換規則                        |
| -------------------------------- | --------------------- | --------------------------- |
| `InvoiceNumber`                  | `InvoiceNo`           | 保留字軌+8碼（例：AB12345678）       |
| `InvoiceDate` + `InvoiceTime`    | `DateTime`            | 合併為 ISO8601（含時區）            |
| `BuyerId` / `Identifier`         | `BuyerId`             | 統編 / 載具碼（需正規化）              |
| `BuyerName`                      | `BuyerName`           | 長度依 MIG                     |
| `MainRemark`                     | `Remark`              | 文字欄位                        |
| `SalesAmount`                    | `SalesAmount`         | 金額精度依 MIG                   |
| `TaxAmount`                      | `Tax`                 | 稅額                          |
| `TotalAmount`                    | `Total`               | 合計金額                        |
| `TaxType`                        | `TaxType`             | 0/5%/免稅等                      |
| `ItemName[]`                     | `Items[].Description` | 一對一映射                       |
| `ItemCount[]`                    | `Items[].Qty`         | 數量                          |
| `ItemPrice[]`                    | `Items[].UnitPrice`   | 單價                          |
| `ItemAmount[]`                   | `Items[].Amount`      | 小計                          |
| *(保留)*                           | `legacyType`          | 固定寫入 `A0401` 或 `C0401`        |
| *(保留)*                           | `rawLine`             | 放原始 CSV 行文字                   |

**必填檢核（節錄）**

- `sum(Items[].Amount) + Tax == Total`
- 稅別/小數位符合 MIG
- 期別、字軌、號碼合法（由後端綜合驗證）

---

## 2) A0501/C0501 → F0501（作廢發票）

| 舊制欄位                        | 新制 F0501 欄位           | 轉換規則            |
| --------------------------- | --------------------- | --------------- |
| `InvoiceNumber`             | `InvoiceNo`           | 原發票號碼           |
| `CancelDate` + `CancelTime` | `CancelDateTime`      | ISO8601         |
| `CancelReason`/`ReasonCode` | `Reason`/`ReasonCode` | 依 MIG 列舉        |
| *(保留)*                      | `legacyType`          | `A0501`/`C0501` |
| *(保留)*                      | `rawLine`             | 原始行             |

**注意**：F0501 是「作廢」，與 F0701（註銷）語意不同。

---

## 3) C0701 → F0701（註銷發票）

| 舊制欄位                    | 新制 F0701 欄位      | 轉換規則    |
| ----------------------- | ---------------- | ------- |
| `InvoiceNumber`         | `InvoiceNo`      | 原發票號    |
| `VoidDate` + `VoidTime` | `RevokeDateTime` | ISO8601 |
| `VoidReason`            | `Reason`         | 字串      |
| *(保留)*                  | `legacyType`     | `C0701` |
| *(保留)*                  | `rawLine`        | 原始行     |

---

## 4) B0401/D0401 → G0401（開立/傳送折讓）

| 舊制欄位              | 新制 G0401 欄位    | 轉換規則            |
| ----------------- | -------------- | --------------- |
| `AllowanceNumber` | `AllowanceNo`  | 折讓單號            |
| `InvoiceNumber`   | `RefInvoiceNo` | 關聯原發票           |
| `Reason`          | `Reason`       | 文字              |
| `SalesAmount`     | `SalesAmount`  | 金額              |
| `TaxAmount`       | `Tax`          | 稅額              |
| `TotalAmount`     | `Total`        | 合計              |
| 明細群               | `Details[]`    | 名稱/數量/金額        |
| *(保留)*            | `legacyType`   | `B0401`/`D0401` |
| *(保留)*            | `rawLine`      | 原始行             |

---

## 5) B0501/D0501 → G0501（作廢折讓）

| 舊制欄位                        | 新制 G0501 欄位           | 轉換規則            |
| --------------------------- | --------------------- | --------------- |
| `AllowanceNumber`           | `AllowanceNo`         | 原折讓單號           |
| `CancelReason`              | `Reason`/`ReasonCode` | 依 MIG           |
| `CancelDate` + `CancelTime` | `CancelDateTime`      | ISO8601         |
| *(保留)*                      | `legacyType`          | `B0501`/`D0501` |
| *(保留)*                      | `rawLine`             | 原始行             |

---

## 6) CSV 標頭建議（新制）

**F0401（開立）**

```
Type,InvoiceNo,DateTime,BuyerId,BuyerName,SalesAmount,Tax,Total,TaxType,Items,Remark,legacyType,rawLine
```

`Items` 欄位可採 JSON；或改多列表達：`Item1.Description,Item1.Qty,...`

**G0401（折讓）**

```
Type,AllowanceNo,RefInvoiceNo,DateTime,Reason,SalesAmount,Tax,Total,Details,legacyType,rawLine
```

> 若上游仍送舊制：**仍用上述新制標頭**，但 `legacyType` 填入舊制代號、`rawLine` 放原行，其餘欄位為**映射後的新制值**。
