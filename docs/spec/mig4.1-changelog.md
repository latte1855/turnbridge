# MIG 4.1 異動紀錄彙整

> **參考文件**：詳見 [MIG 訊息格式整併對應表](./mig-message-type-mapping.md)

## 依據統一發票使用辦法第 20 條之 1 修正
- 折讓證明單由賣方營業人開立並傳輸至平台存證，調整送方及收方
- 折讓種類增加註釋，明確規定由賣方開立及作廢並上傳
- BAN 資料元規格限制只允許數字

## A0101 交換開立發票
- 修改訊息功能說明
- SalesAmount、TotalAmount、TaxAmount 欄位內容不應為負數，XML Schema 增加 minInclusive="0"

## B0101 交換開立折讓證明單
- 修改訊息功能說明
- AllowanceType 欄位為必填，固定值為 2，備註明確規定
- 增加 OriginalInvoiceSellerId、OriginalInvoiceBuyerId 欄位
- TaxAmount 欄位內容不應為負數，XML Schema 增加 minInclusive="0"

## B0102 交換開立折讓證明單接收確認
- 修改訊息功能說明
- AllowanceType 欄位為必填，備註參考 AllowanceTypeEnum

## B0201/B0202 交換作廢折讓證明單
- 修改訊息功能說明
- 新增 AllowanceType 欄位為必填

## F0401 平台存證開立發票
- SalesAmount、FreeTaxSalesAmount、ZeroTaxSalesAmount、TotalAmount、TaxAmount 欄位內容不應為負數，XML Schema 增加 minInclusive="0"

## G0401 平台存證開立折讓證明單
- 修改訊息功能說明
- AllowanceType 欄位為必填，固定值為 2
- 增加 OriginalInvoiceSellerId、OriginalInvoiceBuyerId 欄位
- TaxAmount 欄位內容不應為負數，XML Schema 增加 minInclusive="0"

## G0501 平台存證作廢折讓證明單
- 修改訊息功能說明
- AllowanceType 欄位為必填

## E0502/E0503/E0504 進項存證發票/折讓單檔
- 增加註銷資訊
- TaxAmount 欄位內容不應為負數，XML Schema 增加 minInclusive="0"

## 其他
- 多項資料元規格、備註、欄位長度調整
- 增加多個新資料元規格（ZeroTaxRateReasonEnum、PartyInfoType、RoutingInfoType、InfoType、ResultType、ResultDetailType）
- 修正部分文字及編號排版錯亂
