# TurnBridge（橋票雲）

## 目錄
- apps/turnbridge-backend：JHipster Monolith（React + Spring Boot）
- apps/turnbridge-agent：Edge 代理（之後）
- docs/turnkey：規格與樣本
- deploy：本地 Docker 組態（Postgres/Redis/MinIO）
- samples：CSV 範例

## 快速啟動（本地）
```bash
cd deploy
docker compose up -d
```

## JHipster 問答建議
- Base name: `turnbridgeBackend`
- Package: `com.asynctide.turnbridge`
- Build: Maven
- Reactive: No（WebMVC）
- Auth: JWT
- DB: PostgreSQL（dev/prod 同）
- Cache: Redis ✅ + Hibernate 2nd level ✅
- Other technologies: ✅ API first development；WebSockets 可保留（批次進度即時更新）

## API-First Workflow
1. 將 `docs/turnkey/turnkey-inbound-outbound.yaml` 複製到：
   `apps/turnbridge-backend/src/main/resources/openapi/turnkey.yaml`
2. 編譯時（mvn verify）JHipster 會用 openapi-generator 產生介面原型。
3. 在 `*Resource` / `*Delegate` 補上實作即可。
