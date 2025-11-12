# Turnbridge 電子發票加值中心系統 RFP
*(for MIG 4.1 Integration Platform)*

## 一、專案概要

### 1.1 專案名稱
**Turnbridge 電子發票加值中心系統**  
（Bridge for Turnkey e-Invoice Value-Added Services）

### 1.2 建置目的
建立一套符合 **財政部電子發票 Turnkey MIG 4.1** 規範的加值中心系統，
作為多家廠商發票交換的中介平台，負責：

1. 接收各業者上傳之發票檔案（CSV / ZIP）。
2. 進行檢核、轉換 Canonical 格式、產出符合 MIG 4.1 的 XML。
3. 加密並轉送財政部 Turnkey 平台，接收回饋檔（ACK / RSP）。
4. 將結果回拋給業者（Portal、API、E-Mail、或 Client App）。

### 1.3 系統角色
| 角色 | 功能說明 |
|------|-----------|
| **客戶端（廠商）** | 上傳發票、接收回饋（使用 Portal 或 Local Agent App） |
| **Turnbridge 加值中心** | 主系統（Web + API Server）負責收發、驗證、轉檔 |
| **財政部 Turnkey** | 電子發票交換平台（MIG4.1） |
| **Local Agent App** | 安裝於客戶內網，用於無 IT 能力者：轉檔、加密、傳送、收回饋 |

---

## 二、系統架構概述

### 2.1 架構類型
**三層架構（含 Agent Edge Layer）**
```
Client App ─► Turnbridge Backend ─► Turnkey Gateway
    ▲                │
    │                └── Internal Modules: Upload / Parse / Convert / Dispatch
    └─── Portal UI [jHipster v8.11.0 產生 React UI 擴充 (React / Tailwind)] 
```

### 2.2 核心技術棧
| 層級 | 技術 |
|------|------|
| **Backend** | Spring Boot 3.4.x (Java 21), JPA, QueryDSL, JaVers, MapStruct |
| **API Docs** | OpenAPI 3.0 / Swagger UI |
| **Database** | PostgreSQL 16, Liquibase |
| **Cache / Queue** | Redis, Spring TaskExecutor |
| **Storage** | Local FileSystem / MinIO |
| **Frontend** | React 19 + Tailwind + shadcn/ui |
| **Client App** | JavaFX / Electron |
| **CI/CD** | Maven + Docker + Kubernetes Ready |

---

## 三、系統模組說明

### 3.1 上傳與批次管理模組（Upload Jobs）
| 功能 | 說明 |
|------|------|
| **發票上傳** | 支援 CSV / ZIP，多 profile 自動偵測（profile 指多種格式，目前先支援二種：E0501發票配號檔、發票格式檔，詳見 legacy-system-docs 目錄文件）|
| **重送防呆** | Header `Idempotency-Key` 防止重複上傳 |
| **批次管線** | 狀態流轉：`RECEIVED → PARSING → VALIDATING → PACKING → SENT → RESULT_READY` |
| **非同步背景處理** | TaskExecutor 模擬轉換流程 |
| **結果回饋** | 產出 `result.csv`（含錯誤碼 / 錯誤訊息） |

**主要 API**
| Method | Path | 功能 |
|---------|------|------|
| `POST` | `/api/invoices/upload` | 上傳 CSV/ZIP 檔案（自動判斷格式，支援 E0501/Invoice）|
| `GET` | `/api/upload-jobs/{jobId}` | 查詢批次狀態 |
| `GET` | `/api/upload-jobs/{jobId}/items` | 查詢批次明細 |
| `GET` | `/api/upload-jobs/{jobId}/result` | 下載回饋 CSV |

---

### 3.2 上傳明細（UploadJobItem）
對應每一筆發票記錄，記錄解析結果與錯誤訊息。
| 欄位 | 說明 |
|------|------|
| `lineNo` | 原始行號 |
| `buyerId` | 買方統編 |
| `amountIncl` | 含稅金額 |
| `status` | `QUEUED` / `OK` / `ERROR` |
| `resultCode` | 錯誤碼 |
| `resultMsg` | 錯誤訊息 |
| `rawPayload` | 原始上傳 JSON |
| `rawHash` | SHA256 校驗值 |
| `profileDetected` | 系統自動判斷的 Profile 名稱 |

---

### 3.3 Profile 偵測與格式轉換模組
| 模組 | 說明 |
|------|------|
| **ProfileDetectorService** | 依 CSV 表頭自動判斷 Profile（目前僅支援 E0501 配號檔、Invoice 發票格式檔，格式細節請參考 legacy-system-docs/E0501_template.md 與 Invoice_template.md）|
| **ProfileRegistry / ProfileHandler** | 定義各 Profile 欄位對映與驗證邏輯 |
| **InvoiceParseService** | 解析 CSV/ZIP、批次寫入 UploadJobItem |
| **Profile 設定檔** | YAML 定義欄位 alias 與規則 |

---

### 3.4 MIG 4.1 轉檔模組（下一階段）
| 功能 | 說明 |
|------|------|
| **Canonical Converter** | 將資料轉 CanonicalInvoice DTO |
| **XML Renderer** | 產出符合 MIG 4.1 的 XML |
| **Encryptor** | AES+RSA 加密傳送 |
| **Dispatcher** | 上傳至 Turnkey 並接收回饋 |

---

### 3.5 儲存體抽象層（StorageProvider）
| 功能 | 說明 |
|------|------|
| **LocalFsStorageProvider** | 本機測試用 |
| **MinioStorageProvider** | 正式環境使用 S3 介面 |
| **統一介面** | `store`, `open`, `exists` |

---

### 3.6 Agent Client（Edge App）
| 功能 | 說明 |
|------|------|
| 檔案監控 | 偵測新檔自動上傳 |
| 憑證管理 | 管理簽章與金鑰 |
| 加解密工具 | 提供離線轉檔與上傳 |
| 網路補傳 | 斷線緩存與自動重送 |
| UI | 查詢上傳記錄與設定 |

---

### 3.7 安全與驗證
- JWT 認證與權限分層
- 防重送 `Idempotency-Key`
- 檔案大小限制（>50MB 使用 SFTP）
- 審計追蹤（MDC traceId）

---

## 四、非功能需求

| 類別 | 規格 |
|------|------|
| 效能 | 10,000 筆 CSV 解析 ≤ 5 秒 |
| 可用性 | 99.9%，支援水平擴展 |
| 維運 | Actuator / Prometheus metrics |
| 多租戶 | Shared schema (tenant_id) + Row-Level Security (RLS) 為預設；Schema-per-tenant/DB-per-tenant 作為未來高隔離選項 |
| 安全 | AES256 加密、SHA256 校驗 |
| 國際化 | zh-TW / en-US |
| 稽核留存 | ≥7 年 |
| 開放性 | RESTful API 整合 ERP/CRM |

---

## 五、開發與測試策略

| 階段 | 內容 |
|------|------|
| M0 | 上傳 / 解析 / 結果產生 |
| M1 | Canonical 轉換與 MIG 4.1 |
| M2 | Turnkey 傳送與回饋整合 |
| M3 | Local Agent App |
| M4 | Portal UI + Dashboard |
| M5 | 報表與通知 |

**現階段規劃說明**
- 目前僅在規劃階段，雖有部分功能實作，僅限檔案上傳與初步解析設計。
- Profile 支援格式與解析規則，請參考 docs/legacy-system-docs 目錄下 E0501_template.md、Invoice_template.md。

**測試**
- Unit / Integration / E2E / Performance / UAT

---

## 六、文件交付項目

| 文件 | 說明 |
|------|------|
| 系統設計書 | 架構、模組、介面說明 |
| API 文件 | Swagger / OpenAPI |
| DB 文件 | ERD / Liquibase |
| 部署文件 | Docker / K8s |
| 測試報告 | 測試案例與結果 |
| 使用手冊 | Portal / Agent 指南 |
| 維運手冊 | 監控與異常處理流程 |

---

## 七、預期成果與價值
- ✅ 法規相容（MIG 4.1）  
- ✅ 自動化管線（上傳→轉檔→回饋）  
- ✅ 擴充性（多 Profile、多業者）  
- ✅ 安全性（加密、防重送）  
- ✅ 即時性（背景任務與通知）  
- ✅ 可延伸至 EDI、報稅、AI OCR

---

## 八、後續規劃
1. Invoice Canonical Converter
2. Agent 自動更新模組
3. Webhook 通知 API
4. ERP 插件
5. 多租戶 Dashboard

---

## 九、預算與期程（草案）
| 里程碑 | 工期 | 交付物 |
|---------|------|--------|
| M0 | 4 週 | 上傳/解析/結果產生 |
| M1 | 4 週 | Canonical 轉換+XML |
| M2 | 6 週 | Turnkey 整合 |
| M3 | 4 週 | Local Agent |
| M4 | 6 週 | Portal UI+Dashboard |
| **總計** | **24 週** | **完整系統可上線運作** |
