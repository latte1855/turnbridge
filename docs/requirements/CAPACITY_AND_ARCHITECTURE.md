# 電子發票 Turnkey 系統 - 容量規劃與架構建議

> **文件版本**：v0.1  
> **日期**：2025-01-12  
> **用途**：基於業主提供的容量模型，提出系統架構、基礎設施與擴展建議

---

## 1️⃣ 容量模型與計算

### 系統級容量估算

| 指標 | 計算 | 估值 | 備註 |
|------|------|------|------|
| **客戶現況** | 加油站為主 | 120 家 | 目前全為加油站，正成長 |
| **目標租戶數** | 3 年內 | 300-500 家 | 中-積極成長；加入零售商 |
| **期預估量** | 6,000,000 張 ÷ 2 個月 | 6 M/2M | 業主標準（同業綠界參考） |
| **日均流量** | 6,000,000 ÷ 60 天 | **100,000 張／日** | 全系統平均（120 家現況） |
| **平均客戶量** | 100,000 ÷ 120 | **833 張／日** | 每家加油站平均（現況） |
| **單加油站日量** | 6×8×4×24 | 46,080 張／日 | 加油站容量上限（4 個加油島） |
| **尖峰因子** | 上下班時段 | **3-5 倍（設計用 4 倍）** | AM 7-9、PM 5-7 尖峰 |
| **尖峰流量** | 100,000 × 4 | **400,000 張／日尖峰** | 或約 46 req/s @ 尖峰時段 |
| **熱資料保留** | 1 年 | ~36.5 M 張 | 按日均 100K 計 |
| **冷資料** | 超過 1 年 | 分層存儲 | S3 Glacier 或歸檔媒體 |

### 成長情景分析（3-5 年規劃）

| 情景 | 3 年後客戶數 | 發票量變化 | 日均流量 | 尖峰流量 | 適用策略 |
|------|---------|---------|---------|----------|---------|
| **保守** | 250 家 (+108%) | 穩定 | ~167K 張 | ~668K 張 | 垂直擴展 + 讀寫分離 |
| **中度** ✅ | 350 家 (+192%) | +30% | ~130K 張 | ~520K 張 | 水平擴展 + 分片規劃 |
| **積極** | 500 家 (+317%) | +50-80% | ~150-180K 張 | ~600-720K 張 | 分片 + 多區部署 |

**建議選用「中度成長」為規劃基準**，裕度為 50%，容許後續彈性調整。

---

## 2️⃣ 推薦系統架構

### 2.1 基於中度成長情景的架構設計

基於 3 年中度成長情景（120 → 350 家客戶，日均 100-130K 張，尖峰 400-520K 張），建議採用**分層水平擴展**架構，預留分片空間。

### 2.2 多層架構設計

```
┌─────────────────────────────────────────────────────┐
│                  前端層                              │
│  Browser + PWA (React/Vue)                          │
│  行動 App (iOS/Android，離線支援)                   │
└─────────────────┬───────────────────────────────────┘
                  │ HTTPS / OAuth2 / JWT
┌─────────────────▼───────────────────────────────────┐
│                  API 閘道層                          │
│  Spring Cloud Gateway / Kong                        │
│  - Rate Limiting                                    │
│  - Request/Response logging                        │
│  - Tenant routing (tenant_id 提取)                  │
└─────────────────┬───────────────────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    │             │             │
┌───▼───┐  ┌──────▼──────┐  ┌──▼──────┐
│Upload │  │Query/Search │  │Admin &  │
│Service│  │Service      │  │Ops Tool │
└───┬───┘  └──────┬──────┘  └──┬──────┘
    │             │            │
    └─────────────┼────────────┘
                  │ (Tenant isolation: WHERE tenant_id = ?)
┌─────────────────▼───────────────────────────────────┐
│              快取層 (Redis)                          │
│  - 快取、分散鎖（配號分配 race condition）           │
│  - 驗證規則快取                                     │
│  - Rate limit 計數器（短期 counters）               │
│  - 不作為初期跨實例事件總線（Event Trigger 以 Spring ApplicationEvent 為主）
└─────────────────┬───────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────┐
│              資料層 (PostgreSQL)                     │
│  Shared schema + tenant_id                         │
│  - ImportFile / ImportFileLog (分頁查詢)            │
│  - Invoice / InvoiceItem (分區表)                   │
│  - InvoiceAssignNo (配號)                          │
│  - 索引：(tenant_id, invoice_date), (status), etc. │
└─────────────────┬───────────────────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    │             │             │
┌───▼────┐  ┌────▼────┐  ┌───▼──────┐
│工作隊列 │  │檔案儲存  │  │即時通知  │
│(Kafka) │  │(S3/NAS) │  │(WebSocket)
└────────┘  └─────────┘  └──────────┘
```

### 2.2 核心元件與職責

#### API 層（REST / GraphQL）
- **端點**：
  - `POST /api/v1/upload/e0501` (上傳配號檔)
  - `POST /api/v1/upload/invoice` (上傳發票資料)
  - `GET /api/v1/imports/{importId}/logs` (查詢匯入紀錄)
  - `GET /api/v1/invoices?status=pending` (發票查詢)
  - `POST /api/v1/invoices/{id}/resend` (重送)

- **驗證與授權**：OAuth2 (Client Credentials) + JWT  
- **Tenant 隔離**：JWT 中嵌入 `tenant_id`，每個查詢自動追加 WHERE 條件

#### 檔案上傳與驗證層
- **異步處理**：接收上傳 → 即時回傳上傳紀錄 ID → 背景處理
- **驗證引擎**：
  - E0501：統編、期別、字軌、號段範圍檢查
  - Invoice：MessageType 分類、多明細聚合、金額驗證
  - 使用 InvoiceChecker 舊邏輯，重構成 Spring Validation Bean

- **SHA-256 驗證**：上傳完成後立即驗證，快速回饋錯誤

#### 轉檔與上傳層（Async Worker）
- **排程**：Spring Scheduler 每 5 分鐘拉取待轉檔紀錄
- **轉檔**：生成符合 MIG 4.1 的 XML
- **簽章與加密**：使用系統託管的 MOF 憑證
- **上傳**：HTTP POST 或 SFTP 至 Turnkey 平台
- **失敗重送**：Exponential backoff (1min, 5min, 15min, 1h, 4h)

#### 事件與通知層（Event-Driven）
- **內建 Event Trigger**（為未來 Webhook 預留）：
  - `ImportStarted`, `ImportValidated`, `ImportFailed`
  - `InvoiceProcessed`, `TurnkeyUploadSuccess`, `TurnkeyUploadFailed`
  - `ClientNotificationSent`

- **推播機制**：
  - **即時**：WebSocket 向登入客戶端推送
  - **異步**：E-Mail 通知（失敗或重要狀態變更）
  - **輪詢 API**：客戶端可定期查詢 `/api/v1/imports/{id}/status`

---

## 3️⃣ 基礎設施與容量規劃

### 3.1 計算資源估算

#### 日均 100-130K 張 / 尖峰 400-520K 張（中度成長情景）

| 組件 | 日均負載 | 尖峰負載 | 推薦規格 | 備註 |
|------|---------|---------|--------|------|
| **Web Server (Spring Boot)** | ~115 req/s | 460-600 req/s | 4-6 個 Pod (2 CPU, 2 GB) | 水平擴展，LB 分流；尖峰時自動擴至 10 個 Pod |
| **Database (PostgreSQL)** | ~600 conn | 2000-2500 conn | Primary (16 CPU, 32 GB) + 2x Replica (8 CPU, 16 GB) | 分區表；讀寫分離 |
| **Redis** | ~1200 ops/s | ~4800 ops/s | 2 個節點 (4 CPU, 8 GB) + Sentinel | 分散鎖、Pub/Sub、快取 |
| **Worker (Batch Processing)** | 可小時級批次 | 可分散到 3-5 個 Worker | 2-3 個 Worker Pod (2 CPU, 4 GB) | 自動轉檔、XML 生成、上傳 |
| **Storage (FileSystem / S3)** | ~12 GB/月 | -- | NAS 或 S3 with lifecycle policy | CSV 原始檔 + XML 備份；分層歸檔 |
| **Load Balancer** | -- | -- | ALB (AWS) / Azure LB | 自動健康檢查、連線限流 |

#### 網路頻寬
- **入站**（日均）：100K 張 × 500 bytes ÷ 86400 s ≈ **0.6 Mbps**
- **入站**（尖峰）：460 req/s × 500 bytes ≈ **230 Mbps** (尖峰時段 2 小時)
- **出站**（XML 上傳到 MOF）：同上級別
- **推薦**：骨幹線路 ≥ 500 Mbps；支援 CDN / 多線路容錯

### 3.2 資料庫設計

#### 表結構最佳化

```sql
-- 核心表（必須分區）
CREATE TABLE invoice (
  id BIGSERIAL,
  tenant_id INTEGER NOT NULL,
  invoice_no VARCHAR(10) NOT NULL,
  invoice_date DATE NOT NULL,
  amount DECIMAL(15, 2),
  status VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) PARTITION BY RANGE (invoice_date);

-- 分區策略：按月或季度分區，便於旋轉與歸檔
CREATE TABLE invoice_2024_q1 PARTITION OF invoice
  FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');

-- 關鍵索引（減少全表掃描）
CREATE INDEX idx_invoice_tenant_date ON invoice (tenant_id, invoice_date DESC);
CREATE INDEX idx_invoice_status ON invoice (status) WHERE status != 'CONFIRMED';
CREATE INDEX idx_invoice_no ON invoice (tenant_id, invoice_no);
```

#### 連接池配置
```properties
# Hikari Connection Pool
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

#### 查詢最佳化
- **分頁查詢**：ImportFileLog 使用 OFFSET LIMIT，需加索引
- **Tenant 隔離**：所有查詢必須包含 `WHERE tenant_id = ?`
- **冷資料歸檔**：超過 1 年的發票遷至冷存儲（按月備份），主表僅保留 1 年熱資料

### 3.3 快取策略（Redis）

```properties
# Redis 快取配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
spring.redis.timeout=2000ms

# 快取區域分割
# 0: Event triggers & real-time counters
# 1: Validation rules & static data
# 2: Session & Rate limiting
# 3: Distributed locks (配號分配)
```

#### 常用快取 Key 設計

```java
// Event Trigger Channel (Pub/Sub)
"event:import:${tenantId}:${importId}" → ImportEvent message
"event:invoice:${tenantId}:${invoiceId}" → InvoiceEvent message

// 驗證規則快取（TTL: 1 day）
"validation:banlist:${tenantId}" → Set<String> (有效客戶統編)
"validation:invoicetype:${tenantId}" → allowedTypes

// Rate Limit（TTL: 60s）
"ratelimit:upload:${tenantId}:${apiKey}" → Counter

// 分散鎖（自動序號分配）
"lock:invoice_assignno:${tenantId}:${year}:${month}:${track}" → 配號分配中...
```

---

## 4️⃣ 擴展與高可用方案

### 4.1 水平擴展（Horizontal Scaling）

#### Web 層
- **Kubernetes**：使用 HPA (Horizontal Pod Autoscaler)
  ```yaml
  apiVersion: autoscaling/v2
  kind: HorizontalPodAutoscaler
  metadata:
    name: einvoice-gateway-hpa
  spec:
    scaleTargetRef:
      apiVersion: apps/v1
      kind: Deployment
      name: einvoice-gateway
    minReplicas: 3
    maxReplicas: 10
    metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  ```

- **負載均衡**：使用 Spring Cloud LoadBalancer 或 Nginx
- **Session 管理**：Spring Session + Redis（分散 Session）

#### 資料庫層
- **讀寫分離**：
  - Primary 寫入，Replica 讀查詢（利用 QueryDSL 或 JPA 事務注解）
  - 設定 Read Replica，查詢 ImportFileLog 時指向 Replica

- **數據分區（Sharding）**：
  - 若未來超過 500 租戶，考慮按 tenant_id 分片
  - 每個分片獨立 PostgreSQL 實例（Database per tenant）

#### Worker 層
- **消息隊列**：Kafka 或 RabbitMQ（應對批次轉檔高峰）
- **消費者組**：多個 Worker 進程，並行處理隊列中的轉檔任務

### 4.2 高可用部署

#### 3 節點 PostgreSQL 主從架構
```
┌──────────────┐
│  Primary DB  │ (寫入、同步複製)
│  8 CPU, 16GB │
└──────┬───────┘
       │ Streaming replication
    ┌──┴──┐
    │     │
┌───▼──┐ ┌───▼──┐
│Replica│ │Replica│ (讀取、異步複製)
│4C,8GB │ │4C,8GB │
└───────┘ └───────┘
```

- **自動容錯移轉**：使用 Patroni 或 etcd 實現自動 Failover
- **備份策略**：
  - 日增量備份 → S3 (WAL archiving)
  - 週完整備份 → 冷存儲

#### Redis 高可用
```
3-node Redis Sentinel:
┌──────────┐  ┌──────────┐  ┌──────────┐
│Sentinel 1│  │Sentinel 2│  │Sentinel 3│
└────┬─────┘  └─────┬────┘  └──────┬───┘
     │              │             │
     └──────────────┼─────────────┘
                    │ (監控)
            ┌───────▼────────┐
            │  Redis Master  │
            │  (2 CPU, 4 GB) │
            └────────┬───────┘
                     │ (複製)
            ┌────────▼──────┐
            │  Redis Slave  │
            │ (2 CPU, 4 GB) │
            └───────────────┘
```

#### 應用層容錯
- **Circuit Breaker**：使用 Resilience4j 隔離 MOF Turnkey 上傳調用
- **重試機制**：Exponential backoff，最多 5 次
- **超時控制**：API 端點 30s 超時，Worker 任務 5min 超時

---

## 5️⃣ 監控與可觀測性

### 5.1 監控指標

| 類別 | 指標 | 告警閾值 | 工具 |
|------|------|---------|------|
| **Web 層** | API 響應時間 | >2000ms | Prometheus + Grafana |
| | 錯誤率 (5xx) | >1% | |
| | QPS | >500 req/s | |
| **資料庫** | 連接數 | >80% pool | |
| | 查詢響應時間 | >1000ms | pg_stat_statements |
| | 複製延遲 | >1s | |
| **Redis** | 內存使用 | >80% | redis_exporter |
| | 命令延遲 | >50ms | |
| **業務** | 轉檔成功率 | <95% | Custom metrics |
| | MOF 上傳延遲 | >10min | |

### 5.2 日誌與追蹤

- **集中式日誌**：ELK Stack (Elasticsearch + Logstash + Kibana)
- **分佈式追蹤**：Jaeger / Zipkin
- **審計日誌**：所有手動操作、權限變更記錄至獨立 Audit Table

---

## 6️⃣ 安全性建議

### 6.1 認證與授權
- **API 認證**：OAuth2 + JWT（短期令牌 15 min，長期 Refresh Token 7 days）
- **多因素認證 (MFA)**：可選，針對 Admin 與 Agent 角色
- **RBAC**：細粒度權限（上傳、查詢、手動轉檔、審核）

### 6.2 資料保護
- **傳輸層**：HTTPS/TLS 1.3 + HSTS
- **儲存層**：PostgreSQL 敏感欄位加密（使用 pgcrypto 或應用層加密）
- **備份加密**：S3 server-side encryption (SSE-S3)

### 6.3 MOF 憑證管理
- **憑證託管**：由系統 Admin 統一管理，不暴露給客戶
- **更新提醒**：每季度提醒 Admin 檢查憑證到期日
- **金鑰分離**：簽章金鑰與加密金鑰分別存儲

---

## 7️⃣ 成本估算

### 現況成本（120 家加油站，日均 10 萬張）

**雲端部署（AWS）**：月 ~$2,200 / 年 ~$26,400  
**自建部署（IDC）**：月 ~$15,000 / 年 ~$180,000

### 3 年後成本估算（350 家，日均 130K 張）—— 推薦採用中度成長情景

#### 雲端部署（AWS / Azure）

| 資源 | 規格 | 月成本 | 年成本 | 備註 |
|------|------|--------|--------|------|
| **EC2 (Web)** | 4-6 個 Pod (2C2GB) → 尖峰 10 Pod | $350-500 | $4,200-6,000 | HPA 自動擴展 |
| **RDS (PostgreSQL)** | db.r5.4xlarge Primary + 2x Replica | $2,500-3,000 | $30,000-36,000 | 分區表、讀寫分離 |
| **ElastiCache (Redis)** | 2-3 節點 (4C8GB) + Sentinel | $600-800 | $7,200-9,600 | 高可用配置 |
| **S3 (Storage)** | 1.5 TB 活躍 + 15 TB 冷存儲 | $300-400 | $3,600-4,800 | 分層歸檔策略 |
| **ALB + NAT Gateway** | 多可用區負載均衡 | $100-150 | $1,200-1,800 | 容錯與成本優化 |
| **Data Transfer** | 跨可用區、跨區域複製 | $200-300 | $2,400-3,600 | 根據複製策略變動 |
| **Monitoring + Logging** | CloudWatch / DataDog 進階方案 | $800-1,000 | $9,600-12,000 | 詳細指標與審計 |
| **Backup + Disaster Recovery** | S3 Glacier、跨區備份 | $200-300 | $2,400-3,600 | 99.99% 可用性 |
| **VPN / Security** | AWS PrivateLink、WAF | $100-200 | $1,200-2,400 | 安全連線與防護 |
| **總計** | -- | **$5,200-6,400** | **$62,400-76,800** | 高彈性、按量計費 |

#### 自建部署（IDC 或 On-Premise）

| 成本項 | 現況 (120 家) | 3 年後 (350 家) | 備註 |
|--------|-------------|-------------|------|
| **硬體** | $50,000 (一次) | $150,000 (擴充) | 伺服器、儲存陣列、網路交換機 |
| **軟體授權** | $5,000/年 | $10,000/年 | 若有商用 DB、監控工具授權 |
| **維運人力** | $100,000/年 | $150,000/年 | 2-3 名 DevOps + DBA |
| **機房/帶寬** | $50,000/年 | $80,000/年 | 託管費、電源、冷卻、網路線路 |
| **總計** | **$155,000/年** | **$240,000/年** | 初始投資高，擴展成本線性增加 |

### 成本決策建議

- **Phase 1-2（現在至 1 年內）**：採用雲端（AWS / Azure）
  - 成本低（$2-3K/月）
  - 無初期資本開支
  - 快速擴展、無需預先投資硬體
  
- **Phase 3（1-3 年）**：評估自建或混合
  - 若成長超預期（>350 家），考慮遷移至自建（成本更優)
  - 或維持雲端但導入 RI (Reserved Instance) 與 Savings Plans（降低 30-40% 成本）

---

## 8️⃣ 實施路線圖（Phased Approach）

### Phase 1（第 1-2 個月）：MVP
 - [ ] 核心 API：E0501 / Invoice 上傳與驗證
 - [ ] Web 前端：基本上傳與查詢（React，由 jHipster v8.11.0 產生的前端骨架）
- [ ] 資料庫：Shared schema，單一實例 PostgreSQL + 本地儲存
- [ ] 無 Redis、無高可用
- **容量**：120 家加油站，日均 10 萬張

### Phase 2（第 3-4 個月）：增強
- [ ] 內建 Event Trigger 與 WebSocket 通知
- [ ] Redis 快取與分散鎖
- [ ] 手動轉檔 UI + 二階段審核流程
- [ ] 基礎監控（Prometheus + Grafana）
- [ ] PWA 行動應用開發開始
- **容量**：支援 200 家客戶，日均 15 萬張

### Phase 3（第 5-6 個月）：生產化
- [ ] 高可用 PostgreSQL 主從 + 2x Read Replica
- [ ] Kubernetes 容器化部署 + HPA 自動擴展
- [ ] 分佈式追蹤 (Jaeger) 與集中日誌 (ELK)
- [ ] 行動 App PWA 版本上線
- [ ] S3 冷存儲與生命週期管理
- **容量**：支援 300-350 家，日均 130K 張，尖峰 520K

### Phase 4（後續，6 個月後）：優化與擴展
- [ ] 原生行動 App (iOS / Android)
- [ ] 外部 Webhook 支援（與第三方系統集成）
- [ ] Database per tenant 選項（Premium 功能）
- [ ] AI 輔助異常檢測與預測
- [ ] 數據分片（Sharding by tenant_id）
- **容量**：支援 500+ 家，預留 2 倍成長空間

---

## 9️⃣ 決策矩陣與建議

| 決策項 | 選項 | 建議（現況 → 中度成長） | 理由 |
|--------|------|----------------------|------|
| **多租戶方案** | Shared schema / Schema per tenant / DB per tenant | Shared schema（Phase 1-2）→ Schema per tenant（Phase 3+） | 成本與隔離平衡；預留分片空間 |
| **快取方案** | 無 / Redis / Memcached | Redis（必須，Phase 1 即導入） | 支援 Event trigger、分散鎖、Rate limit、突發流量緩衝 |
| **部署方式** | IDC / 雲端 / 混合 | 雲端優先（Phase 1-3），3 年後評估自建 | 初期成本低、彈性高；若成長超預期再自建 |
| **監控方案** | 自建 / SaaS | SaaS (DataDog / AWS CloudWatch 進階) | 無運維負擔、快速上手、自動擴展 |
| **Load Balancer** | Nginx / HAProxy / Cloud LB | AWS ALB（Phase 2+）或 Nginx（自建時） | 多可用區、自動健康檢查、支援 WebSocket |
| **DB 分區策略** | 按日期 / 按 tenant_id / 按金額 | 按 invoice_date 月度分區（Phase 2） | 便於歸檔冷資料、加快熱查詢 |

---

## 🔟 後續行動清單

- [ ] 業主確認容量情景（保守 / **中度** / 積極），決定資源投入與時程
- [ ] 選擇雲端供應商（AWS / Azure / GCP）或自建 IDC
- [ ] 組建開發團隊：後端 (Spring Boot) 2-3 人、前端 2 人、DevOps 1 人
- [ ] 準備 Phase 1 MVP 交付時程（6-8 週）
- [ ] 建立 SLA 與監控告警基礎設施
- [ ] 準備 TDD / BDD 測試用例（包含容量與負載測試）
- [ ] 評估第三方工具與授權（憑證管理、簽章模組、檔案轉換）

---

（文件完）

---

## 9️⃣ 決策矩陣與建議

| 決策項 | 選項 | 建議 | 理由 |
|--------|------|------|------|
| **多租戶方案** | Shared schema / Schema per tenant / DB per tenant | Shared schema（初期）→ Schema per tenant（中期） | 成本與隔離平衡 |
| **快取方案** | 無 / Redis / Memcached | Redis（必須） | 支援 Event trigger、分散鎖、Rate limit |
| **部署方式** | IDC / 雲端 / 混合 | 建議雲端 (AWS/Azure) | 成本低、擴展靈活、維運省力 |
| **監控方案** | 自建 / SaaS | SaaS (DataDog / New Relic) | 初期成本低、無運維負擔 |
| **圖表庫** | Grafana / Kibana / 自建 | Prometheus + Grafana | 開源、輕量、易擴展 |

---

## 🔟 後續行動清單

- [ ] 業主確認容量模型中的懸而未決參數（客戶構成、峰值因子、成長預期）
- [ ] 選擇雲端或自建部署方案
- [ ] 確定 Phase 1 MVP 的交付時程與人力配置
- [ ] 建立監控與告警基礎設施
- [ ] 準備 TDD / BDD 測試案例（容量測試、負載測試）

---

（文件完）
