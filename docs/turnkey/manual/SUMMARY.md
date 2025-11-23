# Turnkey 使用說明書 v3.9 — 章節摘要（SUMMARY）

> 本文件為 Turnkey 使用說明書 v3.9 的章節索引重建版本，  
> 依據 PDF 實際內容解析後整理而成。

---

## 壹、前言（推定）
說明 Turnkey 的角色、目的、使用者對象與注意事項。

---

## 貳、安裝與設定
- 設定資料庫（外部資料庫 / 內建檔案系統）  
  【來源：12†Turnkey 使用說明書 v.3.9.pdf†L13-L42】
- 安裝與更新流程  
  - Windows 安裝流程  
    【來源：12†Turnkey 使用說明書 v.3.9.pdf†L59-L73】
  - Linux / FreeBSD 安裝流程（推定）
- 啟動與初始化設定（推定）

---

## 參、Turnkey 整體運作流程及說明  
【來源：0†Turnkey 使用說明書 v.3.9.pdf†L17-L39】

### 一、Turnkey 整體運作流程  
- Turnkey 與企業內部系統整合方式  
- Turnkey 端錯誤 vs. 大平台端 ProcessResult 錯誤  
- 流程步驟：  
  1. 企業開立發票 → 2. Turnkey 檢查欄位格式 →  
  3. 打包 XML → 4. 上傳至平台 →  
  5. 平台處理 → 6. 回傳 ProcessResult →  
  7. Turnkey 下載結果 → 8. 更新資料表 →  
  9. 企業端處理錯誤

### 二、B2B 交換發票生命週期  
【來源：13†Turnkey 使用說明書 v.3.9.pdf†L21-L34】

### 三、存證發票生命週期  
【來源：13†Turnkey 使用說明書 v.3.9.pdf†L49-L59】

---

## 肆、連線測試（推定）
平台連線方式、Port、防火牆設定。

---

## 伍、Turnkey 執行（啟動與操作 GUI）
包含匯入憑證、設定連線、啟動背景服務等功能。

---

## 陸、Turnkey 設定（Config）
應包含：
- 伺服器設定  
- 傳輸設定  
- 憑證管理  
【來源：4†Turnkey 使用說明書 v.3.9.pdf†L43-L52】

---

## 柒、憑證管理
- 憑證類型（PFX 等）  
- 憑證密碼驗證機制  
【來源：4†Turnkey 使用說明書 v.3.9.pdf†L31-L52】

---

## 捌、錯誤訊息代碼與 ProcessResult 回應
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L47-L50】

包含：
- Turnkey 錯誤碼  
- 大平台（MOF）ProcessResult 錯誤碼  
- 錯誤處理方式建議

---

## 玖、Turnkey 資料庫與資料備份
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L23-L44】

- 設定類資料  
- 交易類資料  
- 檔案系統備份  
- 資料表（TURNKEY_SYSEVENT_LOG、MESSAGE_LOG、…）

---

## 拾、附錄
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L45-L63】

包含：

### 一、錯誤訊息代碼及處理方法  
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L47-L48】

### 二、Turnkey 功能與訊息代號格式對照表  
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L49-L50】

### 三、Turnkey 資料表及欄位說明  
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L51-L52】

### 四、Turnkey 監控功能  
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L53-L59】

### 五、Turnkey 使用問答集  
【來源：4†Turnkey 使用說明書 v.3.9.pdf†L53-L71】

### 六、Turnkey Gateway 元件說明  
【來源：3†Turnkey 使用說明書 v.3.9.pdf†L69-L75】

---
