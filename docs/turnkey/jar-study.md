# Turnkey JAR 研究紀錄（DEV‑006）

此次 Phase 2 的 XML Builder 會使用 Turnkey 提供的 JAR/Ecosystem。以下整理 `/docs/turnkey/libs/` 內目前取得的三個檔案與已勘查的內容，後續實作時可直接引用。

## 1. 可用 JAR 一覽

| 檔名 | 說明 | 主要內容 |
| --- | --- | --- |
| `einvoice-tky-gateway-api-3.1.2.jar` | Turnkey Gateway API 主體 | `gov/nat/einvoice/tky/gateway/**` (Transformer/Validator/Connector)、`xsd/v3x/v4x`、`gateway/Turnkey_*.properties`、`einvapi.yaml` |
| `einvoice-tky-turnkey-3.2.0.jar` | Turnkey 批次上拋與 REST 模組 | 尚未展開（預期包含 REST client、批次程式入口） |
| `einvoice-common-qrcode-1.0.1-SNAPSHOT.jar` | QR Code 產生工具 | 尚未展開（Phase2 暫不需） |

> 後續 DEV-006 主要會用 `gateway-api` 這顆；其他兩個 jar 可能在 Turnkey 佈署或 QR code 相關功能才會用到。

## 2. `einvoice-tky-gateway-api-3.1.2.jar` 結構重點

`jar tf docs/turnkey/libs/einvoice-tky-gateway-api-3.1.2.jar` 可看到下列群組：

* `xsd/common`, `xsd/v31`, `xsd/v40`, `xsd/v41`… → 官方 MIG/XSD。
* `gov/nat/einvoice/tky/gateway/process/transformer/prog/v4X` → 程式化的 XML 產生器（F/G 訊息）。
* `gov/nat/einvoice/tky/gateway/process/validate/prog/v4X` → XSD 驗證/商業規則檢查。
* `gov/nat/einvoice/tky/gateway/msg/**` → JAXB/Jackson 訊息物件（F0401、G0401…）。
* `gateway/Turnkey_*.properties` → 轉檔/上拋時需的設定檔。
* `einvapi.yaml` → 官方 OpenAPI。

> 因為 jar 內已整合 Transformer/Validator，我們可以直接呼叫 Gateway 提供的 API 生成 F/G XML 並做 XSD 驗證，而不是自行維護 JAXB。

## 3. 下一步（DEV‑006）

1. 透過 `javap` 或直接實作範例程式，確認 `gov.nat.einvoice.tky.gateway.process.transformer.prog.v4X.*` 的入口類（例如 `TransformerProgV41`）及需要的參數。
2. 決定如何把 Jar 納入 Spring Boot（專案本地 libs 或 Maven system scope）。
3. 撰寫 XML Builder Service：  
   - 將 `Invoice`/`InvoiceItem` DTO 轉成 Gateway API 所需結構。  
   - 呼叫 Transformer 產生 XML，並透過其 Validator 或 XSD 進行驗證。  
   - 把生成的 XML 存放到指定目錄/DB。
4. 將研究進度寫入 dev-roadmap（DEV-006）並附上測試指引。

> 若需要進一步解析 `einvoice-tky-turnkey-3.2.0.jar`、`einvoice-common-qrcode.jar`，可重複上述流程並將結果補在本檔案。

## 4. 目前觀察到的驗證限制

- Gateway JAR 中的 Validator（`gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.*`）在初始化時會抓取內部的 `CBSet`（Cross-Check Set）；未提供原廠的 CBSet 設定檔時會丟出 `NullPointerException`。
- 目前暫時跳過驗證（僅生成 XML），後續若取得 Turnkey 配套設定，可再啟用官方 Validator 進行完整檢核。

## 5. 實作現況（2025-11-18）

- `TurnkeyXmlBuilder` 已整合 Gateway JAR，支援 F0401/F0501/F0701/G0401/G0501：  
  * 透過 `Invoice` + `ImportFileItem.rawData` 補齊捐贈、載具、作廢原因、折讓原始發票等欄位。  
  * 每次建檔皆呼叫 `gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.*`，若 CBSet 缺失僅記錄 `warn`，避免阻斷流程。  
  * 對應整合測試：`TurnkeyXmlBuilderIT`。
- `TurnkeyXmlExportService` 會批次取出 `InvoiceStatus.NORMALIZED` 的資料、透過 Builder 產出 XML，並依 `turnbridge.turnkey.inbox-dir` 設定寫入 INBOX，成功/失敗都回寫 `ImportFileLog`（`XML_GENERATED` / `XML_GENERATE_FAILURE`），整合測試：`TurnkeyXmlExportServiceIT`；`TurnkeyXmlExportScheduler` 依 `turnbridge.turnkey.export-cron` 週期執行，`export-batch-size` 可調整單次處理量。
