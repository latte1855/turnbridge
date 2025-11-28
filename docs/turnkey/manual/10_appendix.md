以下為 **第 10 章 — 附錄 (`10_appendix.md`)**
本章整合 Turnkey 使用說明書 v3.9.pdf 的附錄資訊、補充圖表、
以及實務上 Turnkey + Turnbridge 系統需要的參考資料。

內容包括：

* 附錄一：目錄結構大全
* 附錄二：F/G（MIG 4.1）訊息一覽表
* 附錄三：B2B/B2C/B2S 差異
* 附錄四：Turnkey log 與錯誤排查
* 附錄五：常見 FAQ
* 附錄六：Turnbridge 整合作業建議

---

# 10 — 附錄（Appendix）

> 本章內容根據：
>
> * Turnkey 使用說明書 v3.9.pdf
> * 你所需建置的 Turnbridge 系統
> * 電子發票 MIG 4.1 / 平台實際規範
>
> 建立為可直接放入 repo 的完整參考資料。

---

# 附錄一：Turnkey 目錄結構大全（完整版）

Turnkey 安裝後之完整目錄：

```
EINVTurnkey/
├── UpCast/
│   ├── B2BSTORAGE/       # B2B（舊版 MIG 3.x）
│   ├── B2CSTORAGE/       # B2C（舊版 MIG 3.x）
│   └── B2SSTORAGE/       # 存證 F/G（MIG 4.x）
│       ├── F0401/
│       │   ├── SRC/
│       │   ├── BAK/
│       │   └── ERR/
│       ├── F0501/
│       │   ├── SRC/
│       │   ├── BAK/
│       │   └── ERR/
│       └── F0701/
│           ├── SRC/
│           ├── BAK/
│           └── ERR/
│
├── Pack/                  # 打包後等待上傳
├── Upload/                # 上傳 MOF 使用
├── Unpack/                # 下載 ProcessResult 解壓後內容
├── log/                   # Turnkey log
├── conf/                  # Turnkey 設定
├── DB/                    # 內建資料庫（若使用）
├── run.sh / run.bat
└── version.txt
```

Turnbridge 需特別關注：

* `SRC`（資料投入點）
* `ERR`（MIG 格式錯誤之來源）
* `Unpack/ProcessResult`（平台回覆資料來源）
* 產檔暫存（`turnbridge.turnkey.inbox-dir`）與最終 B2S 目錄需區分：INBOX 以 `INBOX/<tenant>/<yyyyMMdd>/<importId>/` 分層，方便 Portal 追蹤；真正讓 Turnkey 讀取的路徑必須符合上圖（例如 `UpCast/B2SSTORAGE/F0401/SRC/`），**不可** 再含租戶代碼，否則 Turnkey 會視為未知目錄。

---

# 附錄二：所有 F/G（MIG 4.1）訊息一覽

整理自 MIG 4.1 及你前面要求的完整訊息索引表。

## F 系列（存證發票）

| 訊息代碼      | 名稱   | 說明             |
| --------- | ---- | -------------- |
| **F0401** | 存證開立 | 取代 A0401、C0401 |
| **F0501** | 存證作廢 | 取代 A0501、C0501 |
| **F0701** | 存證註銷 | 取代 C0701       |

## G 系列（存證折讓）

| 訊息代碼      | 名稱     | 說明             |
| --------- | ------ | -------------- |
| **G0401** | 存證折讓開立 | 取代 B0401、D0401 |
| **G0501** | 存證折讓作廢 | 取代 B0501、D0501 |

（所有欄位樹狀圖已在你前面要求時分別產出，此處作為索引。）

---

# 附錄三：B2B / B2C / B2S 差異

MIG 版本與目錄對照如下：

| 存證類型    | MIG 版本 | 目錄         | 用途        | 備註                      |
| ------- | ------ | ---------- | --------- | ----------------------- |
| **B2B** | 3.x    | B2BSTORAGE | 企業對企業發票   | 舊制、不可與 F/G 混用           |
| **B2C** | 3.x    | B2CSTORAGE | 企業對個人（載具） | 舊制、不可與 F/G 混用           |
| **B2S** | 4.x    | B2SSTORAGE | F/G（新制存證） | **Turnkey v3.9 強烈建議使用** |

重要結論：

✔ Turnkey v3.9 官方推薦使用 **B2S（MIG 4.1）**
✔ A/B/C/D 格式皆已整併為 F/G

---

# 附錄四：Log 與錯誤排查指南

Turnkey log 位於：

```
log/tk.log
log/error.log
```

## 常見排查方式

| 問題          | 檢查位置                 | 可能原因              |
| ----------- | -------------------- | ----------------- |
| MIG 格式錯誤    | UpCast/ERR           | CSV 格式、欄位不符、不合法載具 |
| 上傳失敗        | log/tk.log           | 憑證錯誤、連線失敗、MOF 當機  |
| MOF 回覆錯誤    | Unpack/ProcessResult | 生命週期錯誤、重複發票       |
| Turnkey 不動作 | log/tk.log           | 目錄權限不足、排程異常       |
| 憑證無法簽章      | error.log            | PFX 密碼錯誤、憑證過期     |
| 無結果回覆       | 防火牆 / Proxy          | 出口 443 被擋住        |

---

# 附錄五：常見 FAQ（整理自 PDF 及實務）

## Q1. Turnkey 上傳成功，但平台顯示沒有資料？

A：Turnkey 顯示成功代表 zip 已成功送出，
但平台可能 ProcessResult ERROR。

請檢查：

```
Unpack/B2SSTORAGE/.../ProcessResult
```

---

## Q2. 企業改用 MIG 4.1，需要調整什麼？

* 切換至 **B2SSTORAGE**
* 改用 **F/G 訊息**
* CSV 來源需有 `legacyType` 與 `rawLine`
* 不可再使用 C0401/C0501/C0701（舊制）

---

## Q3. ProcessResult 顯示「發票不存在」？

可能原因：

* 同一張發票使用不同字軌
* 字軌設定錯誤
* 上游資料重複上傳
* 未依生命週期順序

---

## Q4. UpCast/SRC 目錄一直累積檔案？

Turnkey 可能：

* 無權限讀取目錄
* 排程停止
* XML 無法產生 Pack
* 憑證錯誤導致 pack 阶段卡住

---

## Q5. Turnkey 要不要備援？

Turnkey 本身不是 stateful service，
但需要備援其 **目錄** 與 **資料庫**。

最佳做法：

* 多台 Turnkey（Active/Standby）
* 共享 DB
* 共享目錄（NFS / NAS）

---

# 附錄六：Turnbridge 系統整合作業建議（你的系統）

你的系統 **Turnbridge** 是真正的企業端整合平台。
Turnkey 只是一個「MIG 檢查 + XML 打包 + 上傳」工具。

Turnbridge 應負責下列事項：

## ✔ CSV / JSON Normalize

建立：

* `legacyType`
* `rawLine`
* 新制欄位映射（F0401/F0501/F0701）
* 多明細整併
* 上游資料防呆（稅額、金額、載具、統編）

## ✔ XML 產生

Turnbridge 產生：

* 完整 XML（依 MIG 4.1）
* 正確編碼 UTF-8
* 標準欄位順序
* 多筆 XML → 多個檔案

Turnkey 只會檢查，不會自動補欄位。

## ✔ 上傳控管與紀錄

Turnbridge 必須：

* 建立「上傳批次流水號」
* 與 Turnkey 的 Batch/File Log 串接
* 顯示上傳/錯誤進度

## ✔ ProcessResult 解析

Turnbridge 需要：

* 解析 `Unpack/.../ProcessResult`
* 建立錯誤碼 mapping 表
* 重送與補正邏輯
* Webhook 推播（給客戶系統）

## ✔ Log 與監控（SRE 功能）

Turnbridge 要監控：

* UpCast/ERR 增加
* Pack 卡住
* Upload 卡住
* ProcessResult 未更新
* 憑證到期
* Disk 快滿

---
