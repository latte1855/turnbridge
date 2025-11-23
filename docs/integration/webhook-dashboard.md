# Webhook Dashboard / TB Error Overview

> 目標：DEV‑009（Webhook Dashboard）之啟動文件，讓營運可以一覽 `invoice.status.updated` 與 DLQ 事件的 Turnkey/TB 錯誤分布，並直接由 Portal/DeliveryLog 下達重送指令。

## 1. 使用場景

- **TB Code 趨勢查詢**：顯示 `TB-xxxx` (F/G) 的錯誤筆數、所屬 `tbCategory`、可否自動重送，以及建議處置，協助快速判斷是否需調整資料。
- **Webhook DLQ 監控**：若 `webhook.delivery.failed` 與 DLQ 中含 TB code，顯示 `deliveryId/event/status/lastError`，並提供「重送」或「跳過」按鈕（雙人審核、OpenAPI 補充）。
- **匯入批次關聯**：可點選 TB Code，導向 Import Monitor / 結果檔，看到含該錯誤的批次與行號。

## 2. Backend 資料需求

### 2.1 主要資料來源

| 資料 | 來源 | 欄位 |
| --- | --- | --- |
| Turnkey TB 指標 | `turnkey_message` + `invoice` | `tbCode`, `tbCategory`, `tbResultCode`, `tbSourceMessage`, `importFile.id` |
| Webhook Delivery log | `webhook_delivery_log` | `deliveryId`, `event`, `status`, `dlqReason`, `payload`（含 data） |

### 2.2 建議 Endpoint

```http
GET /api/dashboard/webhook-tb-summary
```

回傳：`[{ tbCode, tbCategory, count, canAutoRetry, recommendedAction, lastSeen, sampleInvoiceNo, importFileId }]`

```http
GET /api/dashboard/webhook-dlq
```

回傳：`[{ deliveryId, event, status, dlqReason, attempts, webhookEndpointId, tbCode, tbCategory, invoiceNo, addressed }]`


## 3. UI 初步草案

1. **TB Code Panel**：卡片列表顯示熱門 TB code（排序依 count），每張卡片含 `category`, `status`, `recommendedAction`, `canAutoRetry` badge，可直接點擊「View Import」導向對應的 Import Monitor 明細。若後端尚未提供資料，畫面會顯示一筆 sample 供 UI 測試。
2. **DLQ Table**：承襲目前 `webhook-delivery-log` table，新增 `tb_code`, `tb_category`, `import_id`, `platformMessage`, `resend` action，並提供分頁（`page/size` query + `X-Total-Count` header + `JhiPagination`）。重送按鈕觸發 `/api/dashboard/webhook-dlq/{id}/resend`。
3. **Filter/Link**：可依 `tbCode`、`event`、`status` 過濾，並支援點擊到 Import Monitor + Webhook Delivery Log Detail。

## 4. Webhook Registration（`/webhook/endpoints`）

- **功能**：列表 + 分頁 + 篩選（名稱 / 事件 / 狀態），可建立、編輯、刪除端點並綁定租戶。Secret 由後端自動產生並只顯示一次。
- **表單與驗證**：
  - `顯示名稱 / 目標 URL / 狀態 / 租戶` 皆為必填欄位，UI 以紅色 `*` 標記，未填寫會在欄位顯示 danger 樣式並阻擋送出。
  - URL 必須符合 `https?://` 形式，會即時顯示錯誤訊息 `webhookRegistration.validation.url`。
  - 編輯時會先呼叫 `GET /api/webhook-endpoints/{id}` 載入原始資料並填入欄位，確保不會誤送舊值。
  - 儲存成功後會立即刷新列表；新增時若後端回傳 Secret，Portal 會跳出警示提示務必備份。
- **API**：
  - `GET /api/webhook-endpoints?page=&size=&sort=&name.contains=&events.contains=&status.equals=`（RLS 自動套用租戶）。
  - `POST /api/webhook-endpoints` / `PUT /api/webhook-endpoints/{id}` / `DELETE /api/webhook-endpoints/{id}`：Portal 表單透過 axios 直接呼叫。
  - `POST /api/webhook-endpoints/{id}/rotate-secret`：旋轉 HMAC Secret；Portal 會顯示一次性的 token 並提示使用者備份。
- **前端路由**：`/webhook/endpoints`（Header 右上角「Webhook 設定」選單）。使用 `react-jhipster` `ValidatedForm` + `JhiPagination`，與 Dashboard 共用多租戶 Header 策略。
- **Tenant 行為**：
  - 非管理者：Header 必須指定租戶（TenantSwitcher），表單不顯示 `Tenant` 欄位，由後端依 Header 與 RLS 自動帶入。
  - 管理者：可在 TenantSwitcher 選擇 All 或指定租戶；若為 All，需在表單下拉選擇 `Tenant` 才能儲存。

## 5. 接下來

1. 實作 `DashboardService`、Repository query。（已完成）
2. 前端建立 `dashboard/webhook` route + components。（已完成，後續可考慮加入圖表）
3. 按 `dev-roadmap` 撰寫測試（單元 + e2e）；當 TB 種類 >10 筆時，確認 pagination 仍運作。
