# Agent 自動測試樣板（Node/Java）

> 目的：協助代理端驗證 999 不拆單、映射正確、HMAC 簽章、API 上傳與 Webhook 驗證。所有程式皆含繁體中文註解；Java 範例含 Javadoc。

## 目錄結構
```
tools/agent-tests/
├─ node/
│  ├─ package.json
│  ├─ config.example.json
│  ├─ hmac.js
│  ├─ split-and-upload.spec.mjs
│  └─ samples/
│     ├─ invoice_legacy_A0401.csv
│     └─ e0501_sample_big5.csv
└─ java/
   ├─ pom.xml
   └─ src/main/java/com/example/agenttest/
      ├─ AgentSmokeTest.java
      ├─ CsvSplitter.java
      ├─ HmacSigner.java
      └─ HttpClientUtil.java
```

## Node 版本
1. `npm install`
2. `API_BASE`、`TOKEN` 以環境變數或 `config.example.json` 提供。
3. `node split-and-upload.spec.mjs` 將示範：
   - 依 999 明細不拆單切檔
   - 以 multipart 上傳並列印 `importId`
   - 可擴充 HMAC 驗簽或 Webhook 模擬

## Java 版本
1. `mvn -f tools/agent-tests/java/pom.xml package`
2. `java -cp target/turnbridge-agent-tests-1.0.0.jar com.example.agenttest.AgentSmokeTest`
3. 程式示範：
   - 使用 `CsvSplitter` 切檔
   - 透過 `HttpClientUtil` 上傳 CSV
   - 使用 `HmacSigner` 計算簽章（可與後端對照）

> TODO：Node/Java 兩邊可加入自動測試框架（Jest/JUnit）並整合至 CI。
