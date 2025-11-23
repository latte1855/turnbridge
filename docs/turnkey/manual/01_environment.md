# 01 — 使用環境需求（Environment Requirements）

> 來源依據：
>
> * 【Turnkey 使用說明書 v.3.9.pdf†L13-L42】（資料庫設定與安裝前置需求）
> * 其他章節相關內容交叉比對（系統需求、JRE 版本、OS 支援）

---

# 一、系統需求概述

Turnkey 為 **Java-based** 之電子發票傳輸工具，
支援 Windows / Linux / FreeBSD 等多種環境，可在企業端內部伺服器運行。

系統需求分為：

* 作業系統需求
* Java Runtime Environment（JRE）需求
* 磁碟空間需求
* 網路連線需求（防火牆 Port）
* 資料庫需求（依設定不同，可使用外部 DB）

下方逐項說明。

---

# 二、作業系統需求（OS Requirements）

Turnkey 可執行於下列環境：

| 作業系統        | 支援版本                            |
| ----------- | ------------------------------- |
| **Windows** | Windows Server / Windows 10 以上  |
| **Linux**   | CentOS / RedHat / Ubuntu 等主流發行版 |
| **FreeBSD** | 官方認可版本具相容性                      |

安裝方式依平台不同需要：

* Windows：使用 GUI Installer（PDF 提到安裝流程）
* Linux / FreeBSD：使用壓縮檔方式（tar.gz）展開安裝

---

# 三、Java 執行環境需求（JRE）

Turnkey 運作需安裝：

* **JRE 1.8（Java 8）**

建議採用：

* Oracle JRE 8
* OpenJDK 8（企業通常也可使用）

### ⚠️ 注意

* 必須確保 Turnkey 運作用的 JRE 與系統內其他 Java 應用程式 **版本一致**
* 避免因 JAVA_HOME 指向錯誤，造成啟動失敗或無法載入 Lib

---

# 四、磁碟空間需求（Disk Requirements）

Turnkey 操作時會大量產生：

* XML 打包檔
* 簽章檔案
* ProcessResult 回覆檔
* Pack / Upload / Unpack 目錄資料
* SRC / BAK / ERR 目錄資料（依存證類型 B2B/B2C/B2S）

因此磁碟需求建議：

| 項目                  | 建議容量          |
| ------------------- | ------------- |
| Turnkey 安裝目錄        | 500MB 以上      |
| UpCast（SRC/BAK/ERR） | 10GB 以上（依企業量） |
| Pack / Upload 目錄    | 至少 5GB        |
| Log 目錄              | 2~5GB         |

企業大量開立發票（大量營收業者）
建議專門配置獨立磁碟區。

---

# 五、網路防火牆與 Port 設定（Network Requirements）

Turnkey 與電子發票整合服務平台（MOF）溝通時需要：

| 功能        | Port          | 協議       |
| --------- | ------------- | -------- |
| 發票上傳 / 取回 | **443**       | HTTPS    |
| 早期 SFTP   | 2222（部分環境仍使用） | SFTP/SSH |

### 📌 現行 Turnkey v3.9

主力上傳皆使用 **443**。

企業需確認：

* 防火牆允許 outbound 至 MOF IP
* 支援 TLS1.2

---

# 六、資料庫需求（Database Requirements）

Turnkey 提供兩種資料儲存方式：

## 1. 內建檔案型（預設）

PDF 指出：

* 安裝 Turnkey 後會建立內建資料檔
  【Turnkey v.3.9.pdf†L13-L42】

適合：

* 小型企業
* 不需與外部系統共享 DB 的情境

---

## 2. 外部資料庫（需自行設定）

Turnkey 可改設定使用外部資料庫，常見：

* MySQL
* SQL Server
* Oracle
* PostgreSQL（依企業需求）

PDF 提到：

* 「設定資料庫」為安裝流程中重要步驟
  【Turnkey v.3.9.pdf†L13-L42】

此模式適合：

* 加值中心
* 大型企業（與 ERP / POS 串接）
* 要做備援 / 高可用性

---

# 七、憑證需求（Certificate Requirements）

Turnkey 使用：

* **軟體憑證（PFX 格式）**
* 用於 XML 簽章、上傳驗證
* 密碼須於 Turnkey 初始化時輸入

相關內容於憑證管理章節詳述。

---

# 八、系統帳戶與權限（Permissions）

企業需確保執行 Turnkey 的系統帳戶具備：

* 讀寫 UpCast / Pack / Upload / Unpack 目錄
* 執行外部程序（如 openssl / Java）
* 寫入 log 檔的權限
* 讀取 PFX 憑證的權限

Linux/FreeBSD：

```bash
chmod -R 755 EINVTurnkey/
chown -R turnkey:turnkey EINVTurnkey/
```

---

# 九、建議系統架構（Best Practice）

企業端建議架構：

```
+--------------------+
|   ERP / POS / OMS  |
+--------------------+
          |
          v
+--------------------+
|     Turnbridge     | ←（你要開發的系統）
+--------------------+
          |
          v
+--------------------+
|       Turnkey      |
|  - 格式檢查        |
|  - XML 打包/簽章   |
|  - 上傳/下載       |
+--------------------+
          |
          v
+-----------------------------+
| 電子發票整合服務平台（MOF） |
+-----------------------------+
```

Turnbridge 建議在 Turnkey 前做 Normalize + 檔案管理。

---
