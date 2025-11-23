# 07 — 憑證管理（Key & Certificate Management）

> 內容來源：
>
> * Turnkey 使用說明書 v3.9.pdf（憑證設定、簽章、連線機制）
> * 第 6 章、目錄設定、資料上傳流程等章節中提到的憑證使用
>
> PDF 未提供連續完整敘述，本章依 Turnkey 實際運作結構完整重建。

---

# 一、憑證用途與角色

Turnkey 使用之憑證為 **軟體憑證（PFX 檔）**。
憑證用途分為三大部分：

## 1. **XML 簽章（必需）**

Turnkey 在每筆發票或折讓 XML 打包前，需要以企業憑證進行：

* **電子簽章（Signature）**
* **時間戳記驗證（Timestamp）**

加值中心、企業都必須配置憑證。

---

## 2. **平台身份驗證（部分模式需要）**

部分歷史模式（例如 SFTP）需要憑證做認證。
v3.9 主要使用 HTTPS，不需憑證交換，但仍需憑證簽章 XML。

---

## 3. **上傳資料完整性保護**

簽章可確保資料：

* 不被竄改
* 可驗證來源（Issuer/Subject）
* 確保上傳資料符合電子發票法規要求

---

# 二、憑證格式與需求

Turnkey v3.9 使用：

| 項目       | 說明                    |
| -------- | --------------------- |
| 格式       | **PFX / P12**         |
| Key Type | RSA 2048 位元           |
| 憑證用途     | Digital Signature（必要） |
| 密碼       | Turnkey 啟動時需輸入        |

企業通常由：

* 工商憑證系統（GCA）
* 金融機構
* 加值中心

取得 PFX 檔。

---

# 三、憑證匯入（Turnkey 主選單 → 5）

啟動 Turnkey 後：

```
5) 憑證設定
   ├── 1) 匯入憑證
   ├── 2) 顯示憑證資訊
   ├── 3) 測試憑證
   └── 0) 返回
```

選擇 **1) 匯入憑證** 後：

### Step 1 — 輸入 PFX 路徑

範例：

```
/opt/einv/certs/company.pfx
```

或 Windows：

```
C:\EINVTurnkey\certs\company.pfx
```

### Step 2 — 輸入密碼

Turnkey 會驗證密碼，可立即看出密碼是否正確。

---

# 四、憑證資訊檢視

選擇：

```
5 → 2) 顯示憑證資訊
```

Turnkey 會列出：

* Issuer
* Subject
* 有效期間（Not Before / Not After）
* 演算法（RSA）
* 指紋（SHA-1 / SHA-256）

企業應確認：

* 憑證未過期
* 憑證用途包含 **Digital Signature**

---

# 五、憑證測試（Signature Validation）

選擇：

```
5 → 3) 測試憑證
```

Turnkey 會：

* 嘗試用憑證簽章一份測試檔
* 驗證能否成功
* 驗證 Key 是否有效
* 驗證 password 是否正確

測試成功畫面示例：

```
憑證測試成功：簽章功能可正常使用
```

若失敗：

```
錯誤：無法載入 Private Key
錯誤：密碼不正確
錯誤：憑證已過期
```

---

# 六、憑證常見錯誤與診斷

| 錯誤訊息                | 原因        | 解法                          |
| ------------------- | --------- | --------------------------- |
| Cannot load PFX     | 密碼錯誤      | 檢查密碼、重新匯入                   |
| No private key      | 憑證格式異常    | 重新向憑證單位申請                   |
| Certificate expired | 憑證過期      | 立即更新                        |
| Invalid key usage   | 憑證非用於簽章   | 重新申請含 Digital Signature 的版本 |
| PKCS12 error        | 內部錯誤或檔案毀損 | 重新下載或複製                     |

---

# 七、憑證過期與更新流程（企業必備流程）

企業端必須建立憑證更新 SOP：

1. 憑證過期前（建議提前 30 天）
2. 重新至 GCA 或金融憑證中心申請新憑證
3. 匯入新 PFX
4. 與原憑證交替測試
5. 完成後切換為新憑證上傳
6. 確認 ProcessResult 均能正確回傳

Turnbridge（你的系統）也應：

* 監控憑證有效期間
* 提示企業是否即將過期
* 若接 API，可產生「憑證即將過期」Webhook

---

# 八、憑證存放建議（安全性）

企業應確保：

* PFX 不可置於雲端可公開存取的位置
* Turnkey 目錄 755 / 750 權限
* 憑證密碼不應存於明碼
* 建議使用 OS 的 Keyring/Secrets Manager 做二次保護

---

# 九、Turnbridge（你系統）與憑證整合

Turnbridge 不會直接用憑證做簽章（簽章交給 Turnkey），
但你需要管理：

* 憑證註冊（Turnbridge → Turnkey）
* Turnkey 憑證測試結果
* 憑證到期提醒
* 企業端變更憑證後的 XML 重送
* 自動化檢查憑證有效期間（建議放 Cron）

---
