# 監控與告警指引（Monitoring Runbook）

> 目的：提供 SRE/Ops 監控 Turnbridge 系統的指標、告警閾值與排查流程。  
> 範圍：匯入流程、Normalization、Turnkey 介接、Webhook、基礎設施（DB/Cache/Filesystem）。

---

## 1. Observability Stack

| 元件 | 用途 | 備註 |
| --- | --- | --- |
| **Prometheus** | 指標收集（應用、DB、Redis、Webhook、Turnkey poller） | 保存 30 日；與 Alertmanager 串接 |
| **Grafana** | 儀表板與告警視覺化 | Dashboard：`Turnbridge/Main`、`Webhook`、`Ops-Manual` |
| **Loki / ELK** | Log 查詢 | 保存 90 日，支援 `tenant_id`、`import_id` 索引 |
| **Tempo / Jaeger** | Trace | 監控長時間匯入或 Turnkey 批次延遲 |

---

## 2. 核心指標

| 指標 | 說明 | 告警門檻 | 處置 |
| --- | --- | --- | --- |
| `import_throughput_per_min` | 每分鐘匯入筆數 | 低於 SLA（自動 vs 預估）連續 10 分鐘 | 檢查外部網路/Agent 佇列 |
| `normalize_error_rate` | Normalize ERROR / 總筆數 | > 2% 持續 5 分鐘 | 查詢 `ImportFileLog`，通知支援 |
| `turnkey_pickup_delay_seconds` | XML 產出至 Turnkey ACK 的時間 | > 600s | 檢查 `/INBOX` 堆積或 Turnkey 排程 |
| `turnkey_pickup_stage_files{stage,family}` | Turnkey SRC / Pack / Upload / ERR 目錄堆積檔數（依訊息別） | 任一 stage > 0 連續 10 分鐘 | 依 stage 排查（SRC=Backend、Pack/Upload=Turnkey） |
| `turnkey_pickup_last_scan_epoch` | 最近巡檢時間（Unix epoch） | 超過 10 分鐘未更新 | 檢查 `TurnkeyPickupMonitor` 排程 |
| `turnkey_pickup_alert_total` | 巡檢觸發警示次數計數器 | 異常增加 | 參考 `turnkey_pickup_stage_files` 查原因 |
| `turnkey_outbox_backlog` | `/OUTBOX` 待解析檔數 | > 5 檔 | 檢查 Parser 排程與權限 |
| `turnkey_process_result_total{result}` | ProcessResult 解析成功/失敗累計 | 失敗連續增加 | 檢查 `ProcessResult` 檔案與 TB 錯誤 |
| `turnkey_process_result_last_success_epoch` | 最近一次 ProcessResult 成功時間 | 超過 10 分鐘未更新 | 檢查 Parser 排程或 Turnkey 回饋 |
| `webhook_delivery_success_rate` | 1 小時成功率 | < 98% | 觸發 Webhook Runbook ／通知客戶 |
| `webhook_dlq_total` | DLQ 未處理列數 | > 0 30 分鐘 | 依 `manual-resend.md` 處理 |
| `db_connection_usage` | DB 連線池使用率 | > 85% 5 分鐘 | 規模化 Pod or 排查慢查詢 |
| `fs_inbox_usage_percent` | `/INBOX` 磁碟使用率 | > 80% | 清理舊檔或擴容 |

---

## 3. 告警等級

| 等級 | 條件 | 回應時間 | 責任人 |
| --- | --- | --- | --- |
| **P1** | 匯入/轉檔全停、Turnkey 回饋長時間中斷、Webhook 全部失敗 | 15 分鐘 | 24/7 值班 SRE + Tech Lead |
| **P2** | 單租戶重大異常、DLQ 堆積、Turnkey 延遲 > 30 分鐘 | 1 小時 | 值班 SRE + 模組負責人 |
| **P3** | 指標趨勢異常（但未影響 SLA）、單一事件重送 | 4 小時 | 值班 SRE |

---

## 4. 調查流程（例：Turnkey 延遲）

1. Grafana 告警顯示 `turnkey_pickup_delay_seconds > 600s`。  
2. 透過 Loki 查 `/INBOX` 新增檔案記錄，確認是否堆積。  
3. SSH 連線 Turnkey 主機（或使用 API）檢查排程 log。  
4. 若 Turnkey 正常，檢查 Backend XML 產生服務（排程是否失敗）。  
5. 通知整合工程師，必要時執行 `manual-resend` 或啟動備援流程。  
6. 事件結束後，填寫 Incident timeline，並評估是否需調整排程頻率或容量。

---

## 5. Grafana/Alertmanager 設定範例

### 5.1 Turnkey 堆積告警（Per Stage/Family）

PromQL：

```promql
turnkey_pickup_stage_files{stage="SRC"} > 0
```

* Alert 條件：同一 `stage` & `family` 連續 10 分鐘 > 0。  
* 標籤：`stage`, `family`, `tenant`（若要分租戶可加 recording rule）。  
* 訊息模板：

```
[Turnkey SRC 堆積] stage={{ $labels.stage }} family={{ $labels.family }} files={{ $value }}。
請檢查 Backend XML Export 與 Turnkey SRC 目錄。
```

Pack/Upload/ERR 可同一規則複用（只需變更 `stage`）。

### 5.2 巡檢排程落掉

```promql
time() - turnkey_pickup_last_scan_epoch > 600
```

* 表示 10 分鐘沒巡檢，需檢查 Backend cron job。

### 5.3 Alertmanager 接收器

* **P2**：Turnkey stage >0；通知整合工程師 + 值班 SRE。  
* **P3**：巡檢落掉（可能是排程或 JVM 異常）；由 SRE 追蹤。  
* `turnkey_pickup_alert_total` 可做為 Dashboard Counter，若增速異常再觸發 P3。

---

## 6. 定期檢查

- 每日：確認 `/INBOX`、`/OUTBOX` 檔案數、磁碟使用率。  
- 每週：審視 Webhook DLQ，列出常見失敗租戶並追蹤。  
- 每月：Review 指標與容量（與 `CAPACITY_AND_ARCHITECTURE.md` 比對）。  
- 每季：演練 Turnkey 中斷與 Webhook 全停的 Incident。

> 若新增指標或調整門檻，請回寫本檔、`AGENTS.md §13` 與 `DECISION_LOG`（DEC-006/012）。

---

## 7. Runbook：Portal 手動匯出 & 追蹤

1. 以 ADMIN 帳號登入 Portal → 點選「營運工具」→「Turnkey 匯出」。
2. 輸入批次大小（可留空則使用預設值）並點「執行匯出」，系統會呼叫 `POST /api/turnkey/export` 並立即回傳批次與實際處理筆數。
3. 同頁面底部會顯示「匯出事件紀錄」（ImportFileLog 摘要），確認是否出現 `XML_GENERATED`／`XML_DELIVERED_TO_TURNKEY`。若想查看 detail，可按「查看內容」開啟彈窗，系統會自動格式化 JSON detail 便於複製或交付給值班工程師；如為 `XML_DELIVERY_FAILURE`，依彈窗提示排查。
4. 右側的「Turnkey 拾取巡檢」卡片會呼叫 `GET /api/turnkey/pickup-status` 顯示 `SRC/Pack/Upload/ERR` 滯留數、各訊息別細項以及最後巡檢時間，可快速判斷是否需要登入 Turnkey 主機查閱；若卡片顯示錯誤或久未更新，請改由 Grafana 監控或直接檢視 B2SSTORAGE 目錄。
4. 右側提供快速連結：匯入作業（Import Monitor）、Webhook 儀表板、Grafana Turnkey Dashboard。匯出後務必確認 Turnkey SRC/Pack/Upload 是否清空，必要時追蹤 `turnkey_pickup_stage_files` 指標。
5. 若匯出成功但 Turnkey 仍未拾取，依《Turnkey 使用說明書 v3.9》檢查 B2SSTORAGE/排程、並於 ImportFileLog 下載原始 XML 驗證內容。
