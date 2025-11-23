# 06 — Turnkey 設定（Configuration）

> 本章來源整合：
>
> * Turnkey 使用說明書 v3.9.pdf（傳輸設定、目錄設定、基本設定、憑證設定）
> * 第 3–5 章 PDF 內相關操作畫面
> * Turnkey 一般運作規範（目錄結構、消息版本設定）
>
> 部分內容因 PDF 分散，已依 Turnkey 實際行為完整重建。

---

# 一、設定總覽（Configuration Overview）

Turnkey 設定可分為以下幾大類：

1. **傳輸設定（Transport Settings）**
2. **基本設定（Basic Settings）**
3. **業者資料設定（Seller Profile）**
4. **目錄設定（Directory Settings）**
5. **憑證設定（Certificate Settings）**
6. **系統設定（System Settings）**

啟動 Turnkey（run.sh / run.bat）後，即可進入主選單設定。

---

# 二、傳輸設定（Main Menu → 1）

傳輸設定包含：

* MOF 連線方式（HTTPS）
* 使用的通道（Platform Mode）
* 帳號密碼
* Proxy 設定（若企業使用 Proxy）

典型選單如下：

```
1) 傳輸設定
   ├── 1) 電子發票平台帳號設定
   ├── 2) 傳輸通道設定（HTTPS 443）
   ├── 3) Proxy 設定
   ├── 4) 測試連線
   └── 0) 返回
```

---

## (1) 平台帳號設定

輸入：

* 平台帳號（通常為賣方統編＋專屬代號）
* 密碼

Turnkey 會立即加密存入設定檔。

---

## (2) 傳輸通道設定

Turnkey v3.9 使用：

| 使用通道  | Port    | 備註            |
| ----- | ------- | ------------- |
| HTTPS | **443** | 預設、正式使用       |
| SFTP  | 2222    | 舊版（MIG 3.x）可選 |

Turnkey v3.9 預設只使用 HTTPS。

---

## (3) Proxy 設定（若企業使用 Proxy）

輸入：

* Proxy Host
* Proxy Port
* Proxy Username（可選）
* Proxy Password（可選）

必要時也可在 `run.sh` 增加 JVM 層設定：

```
-Dhttp.proxyHost=...
-Dhttp.proxyPort=...
```

---

## (4) 測試連線

等同於第 4 章介紹的連線測試功能。
Turnkey 會測試：

* 帳號密碼
* HTTPS 連線
* TLS 正常與否
* MOF 回應

---

# 三、基本設定（Main Menu → 2）

此為 Turnkey 最關鍵的設定區，包括：

* 電子發票版本（MIG 3.x / MIG 4.x）
* 簽章憑證（PFX）
* 資料庫設定
* 上傳時間、排程
* 設定檔儲存/還原

---

## (1) 設定憑證（PFX）

Turnkey 需要：

* 軟體憑證（pfx）
* 憑證密碼

憑證用途：

* XML 簽章
* 連線驗證（部分平台模式需）

Turnkey 會驗證：

* 憑證是否有效
* 是否可成功載入
* 密碼是否正確
* 憑證用途是否正確（有效期限、Key Usage）

---

## (2) 訊息版本選擇（Message Version）

Turnkey v3.9 在設定訊息來源（Src）時需指定版本：

```
請選擇來源訊息版本：
1) v4.1
2) v3.2 (已不使用)
```

（來源：PDF 中多次提到 v4.1 選項）

✔ **MIG 4.1 = F/G 系列（存證發票）
此版本需設定 B2SSTORAGE 目錄**

---

## (3) 設定資料庫（使用外部 DB 時）

若使用外部資料庫，需提供：

* DB 主機
* Port
* DB 名稱
* 使用者名稱
* 密碼

PDF 指出：
「設定資料庫」是 Turnkey 初始化重要步驟。
【Turnkey v3.9.pdf†L13-L42】

Turnkey 會用資料表管理：

* 上傳紀錄
* ProcessResult
* Log
* Service 狀態

---

## (4) 排程設定（Schedule）

Turnkey 會根據排程自動：

* 處理 SRC → MIG 檢查
* Pack → Upload
* Download → Unpack
* ProcessResult 更新 DB

預設排程一般為：

```
每 30 秒檢查一次目錄
每 1 分鐘檢查上傳/下載狀態
```

---

## (5) 設定檔寫回

基本設定完成後，需選擇：

```
11) 儲存設定
12) 還原預設值
```

Turnkey 會更新：

```
conf/Turnkey.conf
conf/db.conf
conf/certificate.conf
```

---

# 四、業者資料設定（Main Menu → 3）

設定與賣方相關資訊：

* 統一編號（SellerId）
* 公司名稱
* 聯絡資訊
* 寄送 Email（ProcessResult 錯誤通知）

Turnkey 本身不產生業務資料，
僅用於：

* 顯示
* Log
* 設定檔內的驗證

---

# 五、目錄設定（Main Menu → 4）

此處為 Turnkey 最重要的設定之一。

Turnkey 依 MIG 版本分為：

| 目錄             | 用途          | MIG       |
| -------------- | ----------- | --------- |
| **B2BSTORAGE** | 舊版 B2B 存證   | 3.x       |
| **B2CSTORAGE** | 舊版 B2C 存證   | 3.x       |
| **B2SSTORAGE** | ⭐ 新版存證（F/G） | 4.0 / 4.1 |

來源：
【Turnkey v3.9.pdf†L15-L23】（目錄與版本對應）

---

## (1) B2SSTORAGE（建議使用）

MIG 4.1（F0401, F0501, F0701）的存證目錄。

目錄結構：

```
UpCast/B2SSTORAGE/
    F0401/
        SRC/
        BAK/
        ERR/
    F0501/
        SRC/
        BAK/
        ERR/
    F0701/
        SRC/
        BAK/
        ERR/
```

---

## (2) 設定步驟

選擇：

```
4) 目錄設定
  ├── 1) B2SSTORAGE 設定
  ├── 2) B2BSTORAGE 設定（舊制）
  └── 3) B2CSTORAGE 設定（舊制）
```

進入後會要求設定：

* Source（SRC）
* Backup（BAK）
* Error（ERR）
* Pack Source
* Upload Source
* 訊息版本（v4.1）

---

# 六、憑證設定（Main Menu → 5）

可進行：

* PFX 憑證匯入
* 憑證密碼更新
* 憑證有效性檢查
* 憑證資訊顯示（Issuer、Subject、有效期間）

Turnkey 在每次打包 XML 時會使用此憑證簽章。

---

# 七、啟動／停止服務（Main Menu → 6）

Turnkey 提供：

```
1) 啟動服務
2) 停止服務
3) 顯示服務狀態
```

若以 daemon 模式運作（Linux），建議使用 systemd：

```
systemctl start turnkey
systemctl stop turnkey
systemctl status turnkey
```

---

# 八、其他工具（Main Menu → 7）

包含：

* Log 檢視
* 測試憑證
* 測試目錄權限
* 測試上傳／下載流程（手動）
* Debug 模式開啟

---

# 九、設定檔（Config Files）

Turnkey 所有設定都寫入：

```
conf/
  ├── Turnkey.conf
  ├── db.conf
  ├── certificate.conf
  └── proxy.conf
```

Turnbridge 若需自動寫入設定可直接修改這些檔案。

---

# 十、最佳實務（Turnbridge 建議）

企業端（Turnbridge）應管理：

* 所有 F/G 來源目錄
* XML 產生與 Normalize
* 上傳批次記錄
* ProcessResult 解析
* 重傳流程
* WEBHOOK 推播（你的系統）

Turnkey 只負責：

* 格式檢查
* 簽章
* 上傳/下載
* 資料庫更新
* Log

---
