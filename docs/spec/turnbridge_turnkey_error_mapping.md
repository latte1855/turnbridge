# Turnbridge × Turnkey 錯誤碼對照表

> 目的：建立 **單一錯誤語言**，讓：
>
> - 上游客戶
> - Turnbridge（我們的系統）
> - Turnkey
> - MOF 平台（ProcessResult）
>
> 在「錯誤回報 / Webhook / 重送策略」上有一致的分類與錯誤碼。

> ⚠️ 說明：
> - 下列 **Turnkey/MOF 錯誤碼多為「類型／示意」**，實際數值請以 MIG/官方文件為準。
> - Turnbridge 這邊定義的是**自家錯誤碼（`TB-xxxx`）與錯誤分類**，未來可由 codex 自動補實際 mapping。
> - Turnkey/MOF 錯誤來源對應 `/docs/turnkey/manual/08_result_codes.md`、`05_turnkey_runtime.md` 中的目錄與 ProcessResult 記錄；系統會由 `TurnkeyProcessResultService` 解析 `turnbridge.turnkey.process-result-base` 目錄中的 XML。

---

## 0. 錯誤分層模型（Layers）

Turnbridge 錯誤處理分成四層：

1. **TB-IMPORT**：上游匯入（CSV/JSON）與基本驗證錯誤  
2. **TB-NORMALIZE**：舊制欄位 → 新制 F/G Canonical 時的錯誤  
3. **TB-TURNKEY**：Turnkey 端錯誤（MIG 格式 / 目錄 / 憑證 / 連線）  
4. **TB-PLATFORM**：MOF 平台 ProcessResult 錯誤（生命週期 / 重複 / 資料不符）

Turnbridge 對外只暴露 **TB-xxxx** 錯誤碼與錯誤分類，  
內部則維護：

- `sourceLayer`（IMPORT / NORMALIZE / TURNKEY / PLATFORM）
- `sourceRawCode`（Turnkey/MOF 原始錯誤碼）
- `sourceRawMessage`（原始訊息）

---

## 1. Turnbridge 錯誤碼命名規則（TB-xxxx）

| 範圍 | 類別 | 說明 |
|------|------|------|
| TB-1xxx | IMPORT | 上游匯入/欄位/檔案錯誤 |
| TB-2xxx | NORMALIZE | Legacy → F/G 轉換錯誤 |
| TB-3xxx | TURNKEY_MIG | Turnkey MIG 格式/憑證/目錄錯誤 |
| TB-4xxx | PLATFORM_LIFECYCLE | MOF 生命週期錯誤 |
| TB-5xxx | PLATFORM_DATA | MOF 資料內容/金額/載具錯誤 |
| TB-6xxx | TRANSPORT | 連線、防火牆、SSL 錯誤 |
| TB-9xxx | SYSTEM | 其他系統性錯誤（未分類） |

> ✅ 這是 Turnbridge 「自己的」錯誤碼設計，  
> Turnkey/MOF 的錯誤則透過 `sourceRawCode` 保留原始資訊。

---

## 2. IMPORT（上游匯入層）錯誤碼

### 2.1 CSV 檔案層級錯誤

| TB Code | 錯誤分類 | 說明 | 重送建議 |
|--------|----------|------|----------|
| TB-1001 | IMPORT.FILE_FORMAT_INVALID | 檔案不是 UTF-8 / 不支援的分隔符號 | ❌ 不可自動重送，需重新匯出 |
| TB-1002 | IMPORT.FILE_EMPTY | 檔案無資料或只有標頭 | ❌ 不重送 |
| TB-1003 | IMPORT.FILE_TOO_LARGE | 單檔超過系統限制（例如 > 999 明細） | ❌ 需拆檔重送 |

### 2.2 單行欄位錯誤（尚未到 Turnkey）

| TB Code | 錯誤分類 | 說明 | 對應欄位 |
|--------|----------|------|----------|
| TB-1101 | IMPORT.FIELD_MISSING | 必填欄位缺漏（例如 InvoiceNo） | 發票號碼、日期… |
| TB-1102 | IMPORT.FIELD_INVALID_FORMAT | 格式錯誤（非數字/日期格式錯誤） | 日期、時間、金額 |
| TB-1103 | IMPORT.FIELD_LENGTH_EXCEEDED | 長度超過 CSV / MIG 限制 | 名稱、地址 |
| TB-1104 | IMPORT.TAX_AMOUNT_MISMATCH | 稅額/金額計算不一致（前端就判掉） | 單行金額/總計 |

---

## 3. NORMALIZE（舊制 A/B/C/D → F/G）錯誤碼

這一層錯誤屬於 Turnbridge 自己的 Normalize/MIG 前置檢核，不會碰到 Turnkey。

| TB Code | 錯誤分類 | 說明 |
|--------|----------|------|
| TB-2001 | NORMALIZE.LEGACY_TYPE_UNKNOWN | `legacyType` 非預期（非 A0401/C0401/C0501/C0701/B0401/D0401/B0501/D0501） |
| TB-2002 | NORMALIZE.MAPPING_RULE_MISSING | 找不到對應欄位 Mapping 規則（AGENTS_MAPPING_v1.md 缺項） |
| TB-2003 | NORMALIZE.MULTI_ITEM_OVERFLOW | 同一發票明細數量 > 999（不符規則） |
| TB-2004 | NORMALIZE.INVALID_TAX_TYPE | 舊制稅別值無法對應至 MIG 4.1 稅別代碼 |
| TB-2005 | NORMALIZE.INVOICE_LIFECYCLE_INCONSISTENT | 同一張發票之 F0401/F0501/F0701 送入順序不合理（在 Normalize 就被擋） |

---

## 4. TURNKEY（MIG / 憑證 / 目錄）錯誤碼對照

Turnkey 端錯誤主要來源：

- UpCast/**ERR** 目錄  
- `tk.log` / `error.log`  
- 連線測試 / 憑證測試

### 4.1 MIG 格式錯誤（UpCast/ERR）

> Turnkey 原始錯誤：  
> - `欄位缺漏`  
> - `欄位長度錯誤`  
> - `日期格式錯誤`  
> - `金額試算錯誤` 等

| TB Code | Turnbridge 類別 | Turnkey 來源（pattern） | 重送建議 |
|--------|-----------------|-------------------------|----------|
| TB-3001 | TURNKEY.MIG_FIELD_MISSING | `必填欄位缺漏` / `Required field missing` | ❌ 不可自動重送，需修正資料 |
| TB-3002 | TURNKEY.MIG_FORMAT_INVALID | `日期格式錯誤` / `Invalid date` / `Invalid format` | ❌ 修正資料 |
| TB-3003 | TURNKEY.MIG_LENGTH_INVALID | `資料長度超出限制` | ❌ 修正資料 |
| TB-3004 | TURNKEY.MIG_AMOUNT_MISMATCH | `金額試算錯誤` / `Tax amount mismatch` | ❌ 修正邏輯或來源資料 |
| TB-3005 | TURNKEY.MIG_CARRIER_INVALID | `載具格式錯誤` / `/` 開頭不正確 | ❌ 修正載具 |

### 4.2 憑證設定錯誤

| TB Code | Turnbridge 類別 | Turnkey 來源（pattern） | 說明 |
|--------|-----------------|-------------------------|------|
| TB-3101 | TURNKEY.CERT_PASSWORD_INCORRECT | `Cannot load PFX` / `Bad password` | PFX 密碼錯誤 |
| TB-3102 | TURNKEY.CERT_EXPIRED | `Certificate expired` | 憑證已過期 |
| TB-3103 | TURNKEY.CERT_KEY_USAGE_INVALID | `Invalid key usage` | 憑證用途不含 Digital Signature |
| TB-3104 | TURNKEY.CERT_FILE_MISSING | `PFX file not found` | 憑證檔案不存在 |

### 4.3 目錄 / 權限 / 排程錯誤

| TB Code | 類別 | Turnkey 來源（pattern） | 說明 |
|--------|------|-------------------------|------|
| TB-3201 | TURNKEY.DIR_PERMISSION_DENIED | `Permission denied` | SRC/BAK/ERR 目錄無寫入權限 |
| TB-3202 | TURNKEY.DIR_NOT_FOUND | `Directory not found` | 目錄未建立或路徑錯誤 |
| TB-3203 | TURNKEY.SERVICE_NOT_RUNNING | `Scheduler stopped` / log 長時間無更新 | Turnkey 排程未執行 |

### 4.4 連線類錯誤（Turnkey ↔ MOF）

| TB Code | 類別 | Turnkey 來源（pattern） | 說明 |
|--------|------|-------------------------|------|
| TB-6001 | TRANSPORT.CONNECT_TIMEOUT | `Connection timeout` | 連線逾時 |
| TB-6002 | TRANSPORT.SSL_HANDSHAKE_FAILED | `SSL handshake failed` | TLS/憑證錯誤 |
| TB-6003 | TRANSPORT.HOST_UNREACHABLE | `Host not reachable` | 防火牆或 DNS 問題 |
| TB-6004 | TRANSPORT.AUTH_FAILED | `Authentication failed` | 平台帳號/密碼錯誤 |

> 這些錯誤通常屬於暫時性或設定問題，不是單筆發票資料錯誤。  
> Turnbridge 可針對 `TB-600x` 加上「可重送」標記，但通常需 SRE/管理者先確認。

---

## 5. PLATFORM（MOF ProcessResult）錯誤碼對照

這一層錯誤是 **MOF 平台在已收到資料後** 才回覆的錯誤，  
Turnbridge 透過解析 `ProcessResult` 得知。

> ⚠️ 下表的「平台錯誤碼」為**類型示意**，實際需由 codex 解析 DB / XML 後補齊。

### 5.1 生命週期錯誤（Lifecycle Errors）

| TB Code | 類別 | 平台錯誤類型（示意） | 說明 | 重送建議 |
|--------|------|-----------------------|------|----------|
| TB-4001 | PLATFORM.LIFECYCLE_INVALID_ORDER | `發票未開立無法作廢` / `Invoice not found for cancel` | 未先送 F0401 就送 F0501 | ❌ 不重送，需修正流程 |
| TB-4002 | PLATFORM.LIFECYCLE_STATE_NOT_ALLOWED | `Current status not allowed for this operation` | 不合法狀態轉換（例：②→④→⑥ 錯順序） | ❌ 不重送 |
| TB-4003 | PLATFORM.LIFECYCLE_ALREADY_CANCELLED | `Invoice already voided/cancelled` | 重複作廢 | ❌ 不重送 |
| TB-4004 | PLATFORM.LIFECYCLE_ALREADY_REVOKED | `Invoice already revoked` | 重複註銷 | ❌ 不重送 |

### 5.2 資料內容錯誤（Data / Amount / Carrier）

| TB Code | 類別 | 平台錯誤類型（示意） | 說明 |
|--------|------|-----------------------|------|
| TB-5001 | PLATFORM.DATA_INVOICE_NOT_EXISTS | `Invoice not exists` | 原發票不存在（折讓/作廢/註銷時） |
| TB-5002 | PLATFORM.DATA_DUPLICATE | `Duplicate invoice` | 同張發票重複開立 |
| TB-5003 | PLATFORM.DATA_AMOUNT_MISMATCH | `Tax amount mismatch` / `Amount sum invalid` | 稅額/金額不符 |
| TB-5004 | PLATFORM.DATA_TAXTYPE_INVALID | `Invalid tax type` | 稅別不合法 |
| TB-5005 | PLATFORM.DATA_CARRIER_INVALID | `Invalid carrier` | 載具不被接受 |

### 5.3 系統性錯誤（Platform-side System Errors）

| TB Code | 類別 | 平台錯誤類型（示意） | 說明 |
|--------|------|-----------------------|------|
| TB-9001 | SYSTEM.PLATFORM_MAINTENANCE | `System maintenance` | 平台維護 |
| TB-9002 | SYSTEM.PLATFORM_TIMEOUT | `Platform timeout` | 處理逾時 |
| TB-9003 | SYSTEM.PLATFORM_ERROR | `Internal server error` | MOF 內部錯誤 |

這類錯誤多半可透過「延遲重送」或「人工確認後重送」處理。

### 5.4 常見 ProcessResult 錯誤對照（Turnkey 使用說明書 v3.9）

| ProcessResult ErrorCode | 官方說明               | 對應 TB Code | 來源 |
| ---------------------- | ------------------ | ------------ | ---- |
| 1001                   | XML 格式錯誤           | TB-3002      | 手冊 §4-A |
| 1002                   | 必填欄位缺漏           | TB-3001      | 手冊 §4-A |
| 1003                   | 資料長度超過限制         | TB-3003      | 手冊 §4-A |
| 1004 / 1005            | 日期或數值格式錯誤        | TB-3002      | 手冊 §4-A |
| 1006                   | 稅額計算錯誤           | TB-3004      | 手冊 §4-A |
| 1007                   | 字軌/發票號碼格式不符      | TB-3002      | 手冊 §4-A |
| 2001–2006              | B2B 生命週期錯誤        | TB-4001 / TB-4002 / TB-4003 | 手冊 §4-B |
| 3001                   | 發票不存在             | TB-5001      | 手冊 §4-C |
| 3002                   | 重複上傳             | TB-5002      | 手冊 §4-C |
| 3003 / 3004            | 折讓單不存在或已作廢       | TB-5001 / TB-5002 | 手冊 §4-C |
| 4001                   | 載具格式錯誤            | TB-5005      | 手冊 §4-D |
| 4002 / 4003 / 4004     | 手機條碼/統編/外籍 ID 不合法 | TB-5005      | 手冊 §4-D |
| 5001 / 5002 / 5003     | 稅額/金額/銷售額不符        | TB-5003      | 手冊 §4-E |
| 5004                   | 稅別錯誤              | TB-5004      | 手冊 §4-E |
| 9001                   | 平台維護              | TB-9001      | PDF 附錄 |
| 9002                   | 平台逾時              | TB-9002      | PDF 附錄 |
| 9003 / 9004            | 平台處理錯誤            | TB-9003      | PDF 附錄 |
| E410 / E411            | 作廢/註銷流程錯誤         | TB-4001 / TB-4003 | Turnkey log |
| E420 / E430            | 原發票不存在 / 重複上傳     | TB-5001 / TB-5002 | Turnkey log |

> `TurnkeyErrorMapper` 會先比對此表；若無明確 mapping，再依前綴或數值區間推導 TB code，最後才回傳 `TB-9003`。

> **UI / 結果檔對應**：`GET /api/import-files/{id}/result` 與 Portal Import Monitor 會直接顯示本表對應欄位（`tbCode/tbCategory/tbCanAutoRetry/tbRecommendedAction/tbResultCode/tbSourceCode/tbSourceMessage`），方便營運根據 TB 建議採取動作。

---

## 6. 重送策略矩陣（By TB Code 範圍）

| TB Code 範圍 | 典型錯誤類別 | 是否允許自動重送 | 備註 |
|--------------|--------------|------------------|------|
| TB-1xxx | IMPORT | ❌ | 必須修正上游 CSV/JSON |
| TB-2xxx | NORMALIZE | ❌ | 調整 Mapping 或原始資料 |
| TB-3xxx | TURNKEY_MIG | ❌ | 通常為資料問題，修正後再送 |
| TB-31xx | CERT | 🚫 | 必須先修正憑證/密碼再進行 |
| TB-32xx | DIR | ⚠️ | 修正權限/目錄後可重送同批 |
| TB-4xxx | PLATFORM_LIFECYCLE | ❌ | 多為流程問題，不可重送同訊息 |
| TB-5xxx | PLATFORM_DATA | ❌ | 資料問題，需修正後重新開立/補件 |
| TB-6xxx | TRANSPORT | ⭕（但需保護機制） | 多為網路/平台暫時性問題，可重送 |
| TB-9xxx | SYSTEM | ⚠️ | 視實際情況決定 |

> 建議 Turnbridge 在 DB 中對每一筆錯誤紀錄欄位：
>
> - `canAutoRetry`（boolean）
> - `recommendedAction`（"FIX_DATA" / "CHECK_NETWORK" / "CONTACT_ADMIN" 等）

---

## 7. Webhook Payload 中的錯誤欄位建議

### 成功（Success）

```json
{
  "status": "SUCCESS",
  "invoiceNo": "AB12345678",
  "messageType": "F0401",
  "tbCode": null,
  "tbCategory": null,
  "sourceLayer": "PLATFORM",
  "sourceRawCode": "0",
  "sourceRawMessage": "Success",
  "legacyType": "C0401",
  "rawLine": "C0401|....",
  "timestamp": "2025-11-19T12:34:56+08:00"
}
```

### 失敗（Error）

```json
{
  "status": "ERROR",
  "invoiceNo": "AB12345678",
  "messageType": "F0401",
  "tbCode": "TB-4001",
  "tbCategory": "PLATFORM.LIFECYCLE_INVALID_ORDER",
  "sourceLayer": "PLATFORM",
  "sourceRawCode": "2001",                // 實際平台錯誤碼
  "sourceRawMessage": "發票未開立無法作廢",
  "legacyType": "C0501",
  "rawLine": "C0501|....",
  "canAutoRetry": false,
  "recommendedAction": "FIX_LIFECYCLE_FLOW",
  "timestamp": "2025-11-19T12:45:01+08:00"
}
```

---

## 8. 實作建議（給你和未來的 codex/代理人）

1. **先硬編 TB-xxxx 範圍**（如本文件）
2. Turnbridge 在程式碼中建立一個集中管理的 `ErrorMappingRegistry`：

   * key：Turnkey/MOF 的 `sourceRawCode` 或錯誤字串 pattern
   * value：TB Code / TB Category / canAutoRetry / recommendedAction
3. 未來由 codex 去掃：

   * Turnkey `error.log`
   * `PROCESS_RESULT_LOG`
   * 實際 `ProcessResult` XML
     自動補上「實戰錯誤碼」與 mapping。

---
