# Incident Playbook

> 目標：在匯入、Turnkey、Webhook 或基礎設施發生重大異常時，提供一致的處置流程。  
> 適用範圍：SRE、Ops、待命工程師、產品經理。

---

## 1. 通用流程（Major Incident Flow）

1. **偵測**：告警（Prometheus/Grafana）、客戶回報或自動健康檢查觸發。  
2. **分級**：依 Monitoring Runbook 的 P1/P2/P3 判定。  
3. **指派**：值班 SRE 建立 Incident Channel（Slack/Teams），指派 Incident Commander (IC) 與 Communication Owner (CO)。  
4. **抑制**：暫停有風險的自動作業（例如批次重送），確保資料不再惡化。  
5. **排查**：IC 統籌技術成員（Backend、Integration、DBA 等）進行 Root Cause 分析。  
6. **回報**：每 30 分鐘更新狀態；P1 需同步通知 PM/客戶窗口。  
7. **恢復**：完成修復、驗證並恢復服務。  
8. **結案**：24 小時內產出 Incident Report（含 RCA、預防措施）。

---

## 2. 常見事件與處置

### 2.1 匯入/Normalize 大量失敗（P2/P1）

- **症狀**：`normalize_error_rate` 飆升，Agent 回報大量匯入錯誤。  
- **檢查**：  
  1. `ImportFileLog` 是否出現相同錯誤碼（例如欄位缺漏）。  
  2. 最近是否變更欄位對映或 MIG 規則。  
  3. 是否為單一租戶或全域。  
- **處置**：  
  - 單租戶：通知客戶修正檔案，必要時啟用寬鬆模式。  
  - 全域：回滾最新部署或熱修；暫停新匯入，待驗證後再開放。

### 2.2 Turnkey 傳輸延遲/停擺（P1）

- **症狀**：`turnkey_pickup_delay_seconds` > 600s，/INBOX 堆積。  
- **檢查**：  
  1. Turnkey 主機是否離線或排程未執行。  
  2. 檔案是否卡在 `/ERROR`。  
  3. Backend 是否產出大量 XML（容量突增）。  
- **處置**：  
  - 通知 Turnkey 維運人員，確認服務狀態。  
  - 若為 Backend 產出過多，暫停批次排程並實施流量控管。  
  - 一旦 Turnkey 恢復，依 `manual-resend.md` 控制重送節奏。

### 2.3 Webhook 大量失敗（P2）

- **症狀**：`webhook_delivery_success_rate` < 98% 或 DLQ 堆積。  
- **檢查**：  
  1. 是否為特定租戶 URL 失效。  
  2. 是否最近更換簽章 secret 或 TLS 憑證。  
  3. Queue/Worker 是否正常。  
- **處置**：  
  - 若僅單一租戶，通知其修復 URL；可暫時停用該 Webhook。  
  - 全域失敗則視為 P1，可能是網路或程式缺陷，立即回滾或套用修補。  
  - 將 DLQ 交由 `manual-resend` 流程處理。

### 2.4 DB/Redis 連線耗盡（P1/P2）

- **症狀**：連線池滿載、應用大量 5xx。  
- **檢查**：  
  - 觀察慢查詢、Redis latency。  
  - 檢視最近部署或批次作業是否產生尖峰。  
- **處置**：  
  - 暫時擴充連線池或節流背景排程。  
  - 排查 SQL/Redis 指令並對應到程式碼，必要時回滾。

---

## 3. Incident Report 模板

1. 摘要（Summary + Impact + Duration）  
2. 時間線（Detection → Mitigation → Resolution）  
3. 根因分析（技術 + 流程）  
4. 行動項目（Short-term / Long-term）  
5. 相關文件與 PR（含 `DECISION_LOG` 更新）

---

## 4. 演練與改進

- P1 每半年演練一次（Turnkey 中斷 + Webhook 全停）；P2 每季挑選常見場景演練。  
- 演練完成後需更新本 Playbook、Runbook 與監控門檻。  
- 任何新的 Incident 類型需於本檔新增章節。
