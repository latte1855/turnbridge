# `docs/integration/`

## 目的
集中維護所有「對外整合」相關資料，包含：

* Turnkey 目錄/排程介接的流程圖與實作細節
* Webhook、API、SFTP、批次匯入等雙向測試腳本與驗收腳本
* 與第三方系統（POS、ERP、加值中心）的欄位對映與範例檔

## 既有來源（待搬遷）
| 來源 | 內容 | 狀態 |
| --- | --- | --- |
| `docs/turnkey/MIG4.1.pdf` | 官方 MIG 4.1 規格 | 保留於 `turnkey/`（唯讀） |
| `docs/turnkey/Turnkey使用說明書 v3.9.pdf` | Turnkey 原廠手冊 | 保留於 `turnkey/`（唯讀） |
| `docs/requirements/webhook_spec.md` | Webhook 契約 | 需拆成「契約」+「實作指引」兩段 |

## TODO
1. 搬移 Turnkey 目錄/流程 YAML（原 `docs/turnkey/*.yaml` 於 2025-02 清理時移除）之最新版本或重建等價內容。
2. 將 `webhook_spec.md` 中的驗簽、範例 payload、重試策略整理成 `webhook.md` 子章節。
3. 補齊 API/CLI 測試腳本說明（Postman 或 curl collection）。
4. 依 `AGENTS.md §5` 建立「Inbound → Turnkey → Feedback」實測案例列表。

> 維護者：整合工程師（Integration Squad）。任何對外介面變更需在此留下測試證據。
