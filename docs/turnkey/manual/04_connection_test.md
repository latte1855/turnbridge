# 04 — 連線測試（Connection Test）

> 內容來源：
>
> * Turnkey 使用說明書 v3.9.pdf（連線設定章節）
> * Turnkey 實際運作規範
> * MOF（電子發票整合服務平台）連線要求
>
> PDF 對此章節描述較簡短，因此本章依官方手冊邏輯完整重建。

---

# 一、目的

Turnkey 在實際上傳電子發票前，需要確認：

1. **平台帳號（MOF Account）有效**
2. **密碼正確**
3. **防火牆 / Proxy 允許連線**
4. **Turnkey 端可與 MOF 連線成功**

本章節提供 **Turnkey 內建連線測試功能** 的操作方式與注意事項。

---

# 二、Turnkey 連線使用的 Port 與協定

Turnkey 與 MOF 電子發票整合服務平台間使用下列協定：

| 功能          | Port    | 協定       | 備註           |
| ----------- | ------- | -------- | ------------ |
| 上傳 / 下載     | **443** | HTTPS    | v3.9 預設      |
| （舊制）封存 / 傳輸 | 2222    | SFTP/SSH | MIG 3.x 部分仍需 |

※ v3.9 起主力已全面使用 **443**，除非企業仍需歷史 SFTP 功能。

防火牆需允許：

* outbound → MOF IP
* inbound 無需設定（Turnkey 不需被外部主動連入）

---

# 三、進入 Turnkey 連線測試功能

啟動 Turnkey：

```
run.bat   # Windows
run.sh    # Linux
```

進入主選單後：

1. **輸入 `1`**：傳輸設定
2. **輸入 `5`**（依 Turnkey 版本，通常為 *連線測試 / 測試連線功能*）

在新版 Turnkey 中，選項名稱通常為：

```
5) 測試電子發票平台連線
```

---

# 四、連線測試流程

進入後 Turnkey 會顯示：

```
請輸入營業人平台帳號：
```

接著：

```
請輸入密碼：
```

如果帳號密碼正確並可連線，會顯示：

```
連線測試成功
```

若失敗，Turnkey 會依原因輸出訊息，例如：

| 錯誤訊息                      | 代表含意                |
| ------------------------- | ------------------- |
| `Authentication failed`   | 帳號或密碼錯誤             |
| `Connection timeout`      | 防火牆阻擋、網路無法通         |
| `SSL handshake failed`    | TLS 錯誤、憑證問題         |
| `Host not reachable`      | MOF 主機無法抵達（IP 過濾阻擋） |
| `503 Service Unavailable` | MOF 當機或維護           |

---

# 五、測試結果判讀（官方＋實務重建）

### ✔ 若成功：

* Turnkey 可與 MOF 正常通訊
* 上傳/下載將可運作
* 帳號密碼正確
* TLS/HTTPS 正常
* 防火牆允許 outbound 至 MOF

### ❌ 若失敗，請依錯誤碼檢查：

| 類型       | 檢查項目                       |
| -------- | -------------------------- |
| 帳密問題     | 平台帳號／密碼是否輸入錯誤              |
| 防火牆問題    | outbound 443 是否放行          |
| Proxy 問題 | proxy 需設定在 Turnkey conf    |
| 憑證問題     | Java truststore 是否缺漏       |
| OS 時間錯誤  | 企業端系統時間與 NTP 差距過大導致 SSL 失敗 |

### 📌 實務建議：

* 若使用 Proxy，需在 `conf/` 內增加 Proxy 設定
* 若時間誤差超過 ±300 秒，MOF 會拒絕 TLS 連線
* 若公司有自家防火牆，需白名單加入 MOF IP

---

# 六、MOF 連線測試常見問題（FAQ）

### 1. 為什麼 Turnkey 顯示「無法連線」但網路其實正常？

可能原因：

* 公司防火牆阻擋 HTTPS outbound
* MOF IP 未加入 whitelist
* 需要 proxy，但未設定在 Turnkey
* Java 預設使用 IPv6，需改成 IPv4

解法（於 run.sh 前加）：

```
-Djava.net.preferIPv4Stack=true
```

---

### 2. 為什麼密碼輸入正確，卻一直顯示 Authentication Failed？

原因：

* MOF 平台帳號密碼與「營業人電子發票平台」登入密碼不同
* 密碼包含無法輸入的特殊字元
* 帳號被鎖定

建議重新設定密碼。

---

### 3. 連線測試成功但上傳失敗？

通常原因：

* MIG 格式檢查錯誤 → Turnkey 格式錯誤
* Platform ProcessResult 錯誤 → 生命週期未依規定
* XML 打包失敗（未簽章成功）

可查看：

```
log/tk.log
log/error.log
Unpack/ProcessResult/
```

---

# 七、企業端（Turnbridge）應建立的自動檢查（強烈建議）

雖然 Turnkey 提供連線測試，
但真正穩定上線前，你的系統（Turnbridge）應建立：

| 項目     | Turnbridge 建議                       |
| ------ | ----------------------------------- |
| 上傳測試   | 產生一筆測試 F0401 → Turnkey → MOF        |
| 下載測試   | 模擬 ProcessResult 下載並解析              |
| 重送測試   | 故意產生 MIG 錯誤、生命週期錯誤並驗證提醒             |
| Log 檢查 | 解析 Turnkey log 是否正常更新               |
| 目錄權限測試 | 檢查 UpCast/Pack/Upload/Unpack 是否允許寫入 |

---