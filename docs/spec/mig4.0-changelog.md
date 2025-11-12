# MIG 4.0 異動紀錄彙整

> **參考文件**：詳見 [MIG 訊息格式整併對應表](./mig-message-type-mapping.md)

## 存證類發票整併
- 刪除 A0401、C0401，新增 F0401 平台存證開立發票（整併 A0401 及 C0401）
- 刪除 A0501、C0501，新增 F0501 平台存證作廢發票（整併 A0501 及 C0501）
- 刪除 A0601
- 刪除 C0701，新增 F0701 平台存證註銷發票
- 刪除 B0401、D0401，新增 G0401 平台存證開立(傳送)折讓證明單（整併 B0401 及 D0401）
- 刪除 B0501、D0501，新增 G0501 作廢折讓證明單

## F0401 平台存證開立發票
- 移除 Attachment 欄位
- 移除 CheckNumber 發票檢查碼欄位
- CarrierId1、CarrierId2 長度由 64 位修改為 400 位
- 增加 ZeroTaxRateReason 零稅率原因欄位
- 增加 Reserved1 及 Reserved2 保留欄位
- 調整 CarrierType、RandomNumber 備註說明
- 調整發票捐贈對象(NPOBAN)欄位型態，改為長度 10 位的字串
- 明細 ProductItem 從 999 項修改為 9999 項
- 明細 Description 長度由 256 位修改為 500 位
- 明細 SequenceNumber 長度由 3 位修改為 4 位
- 明細 Remark 長度由 40 位修改為 120 位
- 明細 RelateNumber 長度由 20 位修改為 50 位
- 明細增加 TaxType 欄位
- 彙總 TaxAmount 長度調整 totalDigits 為 20 位，fractionDigits 為 0 位
- 彙總 ExchangeRate 長度調整 totalDigits 為 13 位，fractionDigits 為 5 位
- SalesAmount 欄位中文名稱更正為應稅銷售額合計

## F0501 平台存證作廢發票
- 增加 Reserved1 及 Reserved2 保留欄位

## F0701 平台存證註銷發票
- 增加 Reserved1 及 Reserved2 保留欄位

## G0401 平台存證開立(傳送)折讓證明單
- 移除 Attachment 附件欄位
- 明細 ProductItem 從 999 項修改為 9999 項
- 明細 OriginalDescription 長度由 256 位修改為 500 位
- 明細 AllowanceSequenceNumber 及 OriginalSequenceNumber 長度由 3 位修改為 4 位
- 明細 Tax 長度調整 totalDigits 為 20 位，fractionDigits 為 0 位
- 彙總 TaxAmount 長度調整 totalDigits 為 20 位，fractionDigits 為 0 位

## 其他
- 增修「第參章、訊息彙整表」
- 增加 E0502、E0503、E0504 訊息規格
- A0101 交換開立發票：刪除 CheckNumber、Attachment，增加 ZeroTaxRateReason、Reserved1/2，明細序號、ProductItem、Description、SequenceNumber、Remark、RelateNumber、TaxType 調整
- A0201、A0301、B0101 等訊息皆有保留欄位、長度調整
- 調整多個資料元規格、備註、欄位長度
