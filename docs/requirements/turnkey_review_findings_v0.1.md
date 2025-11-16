
# 電子發票 Turnkey 文件審閱紀要（問題清單 v0.1）
> 目的：彙整 *CAPACITY_AND_ARCHITECTURE.md*、*DECISION_LOG.md*、*turnkey_system_interview_draft.md*、*webhook_spec.md* 中需要修正或補充之處，供業主逐項回覆。  
> 狀態：初稿（依您回覆後，我會更新並定版為《容量與壓測規格》+《SRS》+《TDD/BDD 規格》）

---

## A. 乘載量假設與計算（需要校正）
1. **日量/峰值 vs API QPS / req size 的量綱混用**  
   - 目前容量文以「req/s」估算 Web 層（例如 *460–600 req/s*），但您已確認「**主要為批次檔案上傳**」，每支 API 一次上傳可含 *數百/數千張* 發票，**不適合用單純 QPS 表示整體吞吐**。  
   - **建議**：以「*發票/秒（items/s）*」與「*檔案/分鐘（files/min）*」雙軸表示，並標註典型檔案內容量（如 *2,000 張/檔*）。同時將「轉檔/上傳/回饋」各階段的吞吐單位一致化。  （來源：CAPACITY）

2. **網路頻寬估算偏低**  
   - 現文以 *500 bytes/req* 估算入站，對於批次 CSV（數百KB～數MB/檔）與 XML（數KB/張 × N）明顯偏低，應改為「*KB/張* × *張/秒*」或「*MB/檔* × *檔/分鐘*」。  
   - **建議**：提供三種檔案規模（小/中/大）的樣本大小與分佈，重估峰值帶寬與 egress。  （來源：CAPACITY）

3. **「日均 130 萬張」疑似單位誤植**  
   - Phase 3 段落出現「**日均 130 萬張**」描述，與前文「日均 **100–130K** 張」不一致，應為 **130K**。請確認並修正。  （來源：CAPACITY）

4. **DB 連線數估算偏高**  
   - 表中估 *600 → 2000~2500 connections*，以 JPA/Hikari 與批次處理實務，多為 *數十～數百* 已足夠；過高反致 context switch 與鎖競爭。  
   - **建議**：以 *50–100* 為初值，配合 **批次/佇列化** 平滑尖峰，真正需要高讀取時才加 Replica。  （來源：CAPACITY；INTERVIEW DRAFT）

5. **儲存量級需按「CSV+XML+回饋+Log」分攤**  
   - 現估「~12GB/月」過於保守；以 **3KB/張 XML × 100K/日 ≈ 300MB/日 ≈ 9GB/月**（僅 XML）估算，再加 CSV 原始/回饋/圖檔/Log 容量。  
   - **建議**：列出「每張/每檔」平均大小與保留策略（熱一年、冷歸檔），重算月/年容量。  （來源：CAPACITY）

---

## B. 架構與技術棧的一致性（需與現況對齊）
1. **前端技術已更新（回覆）**  
   - 原始容量文件示意含 *React/Vue/PWA*，舊系統為 **Thymeleaf + Bootstrap**，但業主已確認：**新系統將使用 jHipster v8.11.0 產生的 React 前端骨架**（位於 `src/main/webapp`），並在該基礎上實作功能；因此文件中所有前端技術欄位請以 jHipster React 為準。  （來源：業主回覆）
   - **建議**：已同步將前端技術更新為 jHipster React，PWA/原生 App 保留為 roadmap 項目。

2. **事件/佇列技術（回覆）**  
   - 業主明確回覆：**初期僅使用 Spring Boot 內建事件機制（ApplicationEvent / @EventListener）** 以解耦模組與建置 Webhook 擴充點；不在第一階段接入 Kafka / RabbitMQ。  
   - Redis 仍會用於快取與分散鎖，但不作為初期跨實例的事件總線；若未來需要跨實例或高吞吐，將再評估 Kafka / Redis Streams。  （來源：業主回覆）

3. **第三方整合定位（回覆）**  
   - 已確認：**代理機制（第三方代表上傳）與 POS 直接整合為不同概念**。目前決策為「暫不支援代理機制（DEC-005 Deferred）」，但會支援第三方/零售 POS 與本系統的直接整合（透過 API / Webhook / Agent）—前提是該第三方具備串接能力；若無能力，則可使用我們提供的 Agent（Agent 尚未完成規劃與實作，屬 roadmap）。  
   - **建議**：在 DECISION_LOG 補註：第三方 POS 為系統整合（API/Webhook），代理機制為後期可選功能（需額外授權與帳務設計）。

---

## C. 業務流與批次策略（需明確化）
1. **每檔最大筆數與分割策略（回覆）**  
   - 業主回覆：目前維持舊系統定義 **每檔上限為 999 筆**（筆數以發票明細列數計算；例如 1 張發票有 3 筆明細則計為 3 筆）。若單張發票導致超出 999，該張發票必須整張移至下一檔，**不可將同一張發票的明細拆分到不同檔案**。  
   - 設計建議：保留伺服器端「自動分割」能力作為選項（當上傳端無法或未提供分割時，由 Backend 建立多個 ImportFile），但預設流程為：若上傳檔案內含超出 999 的情形，API 回應拒收並回傳分割建議（或錯誤原因），建議由上傳端（Agent）優先負責分割，以避免 Backend 在高流量下產生額外運算負荷與不必要的重試複雜度。  （來源：業主回覆）

2. **端到端延遲 SLO（批次 vs 近即時）**  
   - 文件混用「3–10 分鐘」與「排程 30 分鐘內」；若未來接 POS，**多為小檔高頻**，SLO 需改成 **秒級～分鐘級**。  
   - **建議**：分兩檔 SLO：**批次檔**（分鐘級）、**POS/即時 API**（秒級），並分別訂 P95/P99。  （來源：CAPACITY；INTERVIEW DRAFT）

3. **重送與冪等**  
   - 需確保同一檔/同一張發票多次送達時不重複入帳（檔案 SHA-256、發票鍵、idempotency-key）。  
   - **建議**：補「冪等鍵策略」與「去重索引（tenant_id + invoice_no + period）」；Webhook 亦需 delivery 去重窗口。  （來源：INTERVIEW DRAFT；WEBHOOK）

   4. **Backend 線上單張發票建立與即時處理**
      - 建議：Backend 應保留**線上建立/編輯單張發票（UI/API）**的功能，支援即時場景（POS 或客服建立）。該功能路徑可繞過批次檔案流程，直接進入驗證、轉檔與 Turnkey 封裝流程以取得即時回饋（ack/receipt）。
      - 設計要點：線上建立需使用相同的驗證/冪等/審計邏輯；若即時發票需納入批次匯出，系統應能以小檔（或單筆包）方式產生 ImportFile 並送入相同處理線路。
      - 建議將「線上單張發票」列為 Phase 1 的可選功能（對接 POS/客服場景），並在 API 規格中明確標示同步/非同步回覆契約。  （來源：業主回覆 / 設計建議）

---

## D. 資料庫與模型（需補充）
1. **分區策略與查詢路徑**  
   - 目前建議「按日期分區」；若零售 POS 導入，查詢可能以「門市/POS 單號」為主。  
   - **建議**：分區仍以日期，但補二級索引（tenant_id, store_id, pos_tx_time），同時規劃熱/冷表輪替。  （來源：CAPACITY）

2. **連線池與批次寫入**  
   - 建議把 Hikari pool size、批次 insert 大小、事務超時、JPA flush 策略列為「可調參」，並於壓測時校準最佳值。  （來源：CAPACITY）

3. **多租戶與 RLS（回覆與實作說明）**
   - 決策為 **Shared schema**（單一資料庫/單一 schema），多租戶透過資料列的 `tenant_id` 欄位來隔離；系統會以 **Row-Level Security (RLS)** 或應用層過濾器來保障租戶資料存取。  
   - 實作要點：
      - 所有業務表格至少需包含 `tenant_id`，且關鍵表格加入 `created_by` / `trace_id` / `created_at` 等審計欄位。  
      - 在 SRS 中明列 RLS 規則（super-admin/ops/admin/tenant-user 的存取範圍）、migration 步驟與測試矩陣。  
      - 不採用 schema-per-tenant 或 db-per-tenant 作為預設（可視作未來擴充選項），以降低運維與資源成本；若未來有隔離需求再評估 multi-DB 策略。  
   - 建議在驗收測試中加入租戶隔離測試案例（跨租戶資料不可見、RLS 規則繼承於 dev/staging/prod）。  （來源：DECISION_LOG；業主回覆）

---

## E. Webhook 與外部整合（需再精煉）
1. **安全與防重放**  
   - 已定義 HMAC 簽章，但少「**時間戳時效**」、「**nonce/replay 防護**」。  
   - **建議**：新增 `X-Timestamp` 容忍窗口（±5 分鐘）、`X-Nonce` 以及重放拒收規則。  （來源：WEBHOOK）

2. **節流與退避**  
   - 已有指數退避 1m/5m/15m；需明確 **429/5xx** 的差異化策略與 **每 endpoint 同時重試上限**。  
   - **建議**：增加 per-subscriber 速率上限與佇列長度。  （來源：WEBHOOK）

3. **事件定義與對帳**  
   - 建議把 **turnkey.daily-summary** 與 **本地統計** 的對帳規則（允差、缺漏補送）寫入 SLO 與告警。  （來源：WEBHOOK；INTERVIEW DRAFT）

---

## F. 監控與壓測（需具體化）
1. **指標映射**：將 SIG/MIG 指標與業務 KPI（成功率、延遲）落到 Prometheus 指標名稱與告警門檻。  （來源：CAPACITY）  
2. **壓測腳本分層**：
   - 批次上傳檔案（files/min，含不同大小檔）  
   - 轉檔/送交 Worker 吞吐（items/s）  
   - 回饋解析與對帳（每日批/即時 ack）  
   - Webhook 投遞（並發訂閱者數、失敗率、DLQ）  
3. **合格門檻**：用 Baseline/Growth/Peak 三檔，分別定 *P50/P95* 與錯誤率門檻。  （來源：CAPACITY）

---

## G. 決策與路線圖（需同步）
1. **更新 DECISION_LOG**：新增「**第三方 POS 整合**」決策、事件/佇列選型、與 SLO 分檔（批次 vs 即時）。  （來源：DECISION_LOG）  
2. **SRS 與容量文件互參**：避免數字不一致（如 130K vs 1.3M、req/s vs items/s）。  （來源：CAPACITY）

---

## ✅ 業主待回覆（請逐點回填）
- [ ] A-1：吞吐呈現方式採用「發票/秒 + 檔案/分鐘」？是否保留 QPS 僅供 API 層參考？  
- [ ] A-2：請提供「小/中/大」檔案平均大小（CSV、XML）與分佈，以便重估頻寬與儲存。  
- [ ] A-3：確認 Phase 3 的「日均 130K/1.3M」哪個正確？  
- [ ] B-2：事件中介選型偏好（Kafka / RabbitMQ / Redis Stream / 僅 DB+Scheduler）？  
- [x] C-1：單檔上限已確認為 999（發票明細列數計算）；是否同意由 Agent 優先負責分割，Backend 僅在必要時提供自動分割/拒收建議？  
- [ ] C-2：SLO 分檔：批次（分鐘級）與 POS 即時（秒級）是否同意？其 P95 目標？  
- [ ] C-4：Backend 線上單張發票建立與即時處理是否納入 Phase 1（或僅作為可選/Phase 1 選項）？  
- [ ] D-1：POS 導入後的查詢維度（store_id / pos_tx_time）是否需要？  
- [x] D-3：多租戶採用 Shared schema（以 tenant_id + RLS/應用層過濾）已確認，是否同意在 SRS 中列出 RLS 規則與審計欄位？  
- [ ] E-1：Webhook 是否接受 **mTLS** 或 **IP allowlist** 作為進一步加強？  
- [ ] F-3：Baseline/Growth/Peak 的目標是否採用上一版建議（可再調整）？

---

> 參考來源：  
> - CAPACITY_AND_ARCHITECTURE.md（容量、架構與成本）  
> - DECISION_LOG.md（既有架構決策）  
> - turnkey_system_interview_draft.md（業務流程與現行作業）  
> - webhook_spec.md（事件推播與驗證）
