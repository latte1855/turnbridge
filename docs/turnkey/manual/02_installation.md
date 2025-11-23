# 02 — 安裝與設定（Installation & Setup）

> 原始資訊來源：
>
> * 【Turnkey 使用說明書 v.3.9.pdf†L13-L42】（資料庫設定）
> * 【Turnkey 使用說明書 v.3.9.pdf†L59-L73】（Windows 安裝流程）
> * 其他章節交叉整理（Linux/FreeBSD 安裝方式、常見結構、初始化流程）

---

# 一、安裝前準備（Pre-installation Checklist）

請先確認環境符合：
（依上一章「使用環境需求」）

* OS：Windows / Linux / FreeBSD
* JRE：Java 8（必須已安裝）
* 防火牆允許 outbound 至 MOF（Port 443）
* 磁碟空間足夠（建議 10GB 以上）
* 已取得企業用憑證（PFX）
* 具備安裝 Turnkey 的系統帳戶權限

---

# 二、安裝方式總覽

Turnkey 提供三套安裝方式：

| OS          | 安裝方式                |
| ----------- | ------------------- |
| **Windows** | 透過安裝程式（Installer）安裝 |
| **Linux**   | 解壓縮 tar.gz 並手動設定    |
| **FreeBSD** | 同 Linux（tar.gz）     |

PDF 內容較著墨 Windows，因此 Linux / FreeBSD 版本由 Turnkey 典型作法重建。

---

# 三、Windows 安裝流程（官方 Installer）

> 來源：【Turnkey 使用說明書 v3.9.pdf†L59-L73】

### 步驟 1：執行安裝程式

執行 `EINVTurnkeyInstaller.exe`（或類似檔名）。

安裝程式將：

* 自動建立安裝目錄（預設為 `C:\EINVTurnkey\`）
* 自動產生基本的目錄結構：

```
EINVTurnkey/
  UpCast/
  Pack/
  Upload/
  Unpack/
  conf/
  log/
```

---

### 步驟 2：設定資料庫（DB 設定）

安裝過程會詢問：

* 使用「內建資料庫存放方式」
* 或「自建外部資料庫」（MySQL、SQL Server…）

依 PDF：

> 若使用外部資料庫，需要於安裝時輸入
> DB 位置、帳號、密碼、schema 名稱。

來源：
【Turnkey 使用說明書 v3.9.pdf†L13-L42】

---

### 步驟 3：完成安裝並啟動初始設定

安裝完成後，按下「啟動」或執行以下指令：

```
C:\EINVTurnkey\run.bat
```

啟動後會進入 **Turnkey 主選單（黑底文字介面）**。

---

# 四、Linux 安裝流程（tar.gz）

> PDF 無直接逐步說明，以下依 Turnkey 官方常用方式整理。

### 步驟 1：上傳安裝檔

將 `EINVTurnkey_xxx.tar.gz` 上傳至伺服器：

```
/opt/einv/
```

### 步驟 2：解壓縮

```bash
cd /opt/einv
tar -zxvf EINVTurnkey_xxx.tar.gz
```

會產生目錄：

```
/opt/einv/EINVTurnkey/
```

---

### 步驟 3：設定權限

```bash
chmod -R 755 EINVTurnkey/
chown -R turnkey:turnkey EINVTurnkey/
```

---

### 步驟 4：設定 JAVA_HOME

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

建議寫入：

```
/etc/profile.d/java.sh
```

---

### 步驟 5：啟動 Turnkey

```bash
cd /opt/einv/EINVTurnkey/
./run.sh
```

---

# 五、FreeBSD 安裝流程（與 Linux 相同）

PDF 沒有列出 FreeBSD 的獨立步驟，
實務上 FreeBSD 安裝與 Linux 相同：

* 解壓縮
* 設定 Java
* 設定執行權限
* 執行 `run.sh`

---

# 六、Turnkey 初始化設定流程（首次啟動）

首次啟動 Turnkey：

```bash
run.bat   # Windows
run.sh    # Linux/FreeBSD
```

會進入黑底問答式介面（CLI menu）。

標準初始化順序如下（整合 PDF 各章）：

## 1️⃣ 設定傳輸設定（主選單 → 1）

包含：

* 主機連線設定
* 目錄設定
* B2B / B2C / B2S（MIG 4.0）目錄設定

---

## 2️⃣ 設定基本設定（主選單 → 2）

包含：

* 憑證（PFX）匯入與驗證
* 電子發票版本（MIG 3.x / MIG 4.x）
* 簽章設定
* 資料庫連線設定

PDF 有提到：

> 「設定資料庫」會要求輸入 DB 位置、帳密
> 【Turnkey 使用說明書 v3.9.pdf†L13-L42】

---

## 3️⃣ 設定企業參數（主選單 → 3）

通常包含：

* 賣方統編
* 營業人名稱
* 聯絡資訊
* 電子郵件通知設定

---

## 4️⃣ 設定目錄（主選單 → 4）

PDF 指出不同版本對應不同目錄：

* B2BSTORAGE（MIG 3.x，用於 B2B 存證）
* B2CSTORAGE（MIG 3.x，用於 B2C 存證）
* **B2SSTORAGE（MIG 4.x，用於 F/G 新制發票）**

來源：
【Turnkey 使用說明書 v3.9.pdf†L15-L23】

---

## 5️⃣ 設定帳號與密碼（主選單 → 5）

若需要連線 MOF，需設定：

* 平台帳號（MOF 提供）
* 密碼（傳輸所需）

---

# 七、Turnkey 目錄結構（依 PDF 整理）

Turnkey 主要運作目錄如下：

```
EINVTurnkey/
├── UpCast/         # 使用者上傳來源檔 (SRC/BAK/ERR)
├── Pack/           # Turnkey XML 打包後的存放處
├── Upload/         # Turnkey 上傳 MOF 使用目錄
├── Unpack/         # MOF ProcessResult 下載後解壓位置
├── conf/           # 設定檔
├── log/            # Log
├── run.sh / run.bat
└── DB/             # 若為內建 DB
```

PDF 位置：
【Turnkey 使用說明書 v3.9.pdf†L55-L76】

---

# 八、加值：企業常見安裝架構（最佳實務）

建議正式部署時採用：

```
+------------------+
|   ERP / POS /    |
|   發票開立系統   |
+------------------+
          |
          v
+------------------+
|    Turnbridge    |  ←（你開發的系統：匯入、Normalize、Webhook）
+------------------+
          |
          v
+------------------+
|     Turnkey      |
|  (XML/簽章/上傳) |
+------------------+
          |
          v
+-----------------------------+
| 電子發票整合服務平台（MOF） |
+-----------------------------+
```

---