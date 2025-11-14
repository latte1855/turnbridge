# API / CLI 測試腳本（最小可行套件）

> 目的：提供整合測試時可立即使用的 curl / HTTPie / Postman 腳本。  
> 依據：`openapi-turnbridge-v1.yml`、`turnbridge-srs-v1.0.md §7`、`webhook-contract.md`。

---

## 1. 匯入 API

### 1.1 上傳 Invoice ZIP

```bash
TOKEN="<OAuth token>"
FILE="out/invoice_20251112_0001.zip"
MD5=$(md5 -q "$FILE")

curl -fS -H "Authorization: Bearer $TOKEN" \
  -F "file=@${FILE}" \
  -F "md5=${MD5}" \
  -F "encoding=UTF-8" \
  https://turnbridge.example.com/api/v1/upload/invoice
```

回應應含：

```json
{
  "importId": "imp_20251112_0001",
  "status": "RECEIVED"
}
```

### 1.2 查詢匯入狀態

```bash
curl -fS -H "Authorization: Bearer $TOKEN" \
  https://turnbridge.example.com/api/v1/imports/imp_20251112_0001
```

預期欄位：`status`、`successCount`、`errorCount`、`source_family`、`normalized_family`。

---

## 2. Webhook 測試

### 2.1 以 `httpie` 模擬接收端

```bash
python3 -m http.server 8080
# 或使用 ngrok / webhook.site
```

### 2.2 重新送出 DLQ Payload

```bash
curl -fS -H "Authorization: Bearer $TOKEN" \
  -X POST https://turnbridge.example.com/api/v1/webhooks/dlq/replay \
  -d '{"deliveryId":"d949bd44-...-e712"}'
```

### 2.3 驗證簽章

```bash
SECRET="abc123"
PAYLOAD='{"event":"invoice.status.updated","delivery_id":"..."}'
SIGNATURE=$(printf "$PAYLOAD" | openssl dgst -sha256 -hmac "$SECRET" | cut -d' ' -f2)
echo "sha256=$SIGNATURE"
```

將結果與 Header `X-Turnbridge-Signature` 比對。

---

## 3. Turnkey Flow Smoke Test

1. 執行 `turnbridge-cli pack --input sample.csv --max-lines 999 --zip`。  
2. 上傳後檢視 `/turnkey/INBOX` 是否生成對應 XML。  
3. 5 分鐘內應出現 `/turnkey/OUTBOX/*.ack`；以 `docs/operations/turnkey-healthcheck.md` 步驟驗證。  
4. 使用 `curl` 觸發 Webhook 重送，確認測試端點收到事件。

---

## 4. Postman / Newman

- Collection：`docs/integration/postman/turnbridge-api.postman_collection.json`
  1. 匯入後設定 `{{invoice_file}}`、`{{import_id}}` 等變數（見環境檔）。  
  2. 依序執行「Upload Invoice」→「Get Import」→「Register Webhook」。
- 環境檔：`docs/integration/postman/turnbridge-env.postman_environment.json`
  - 依實際環境更新 `base_url`、`token`、`import_id`、`invoice_file`、`invoice_md5`。  
  - 建議於 CI 前從 Vault 注入 token。
- Newman：`newman run docs/integration/postman/turnbridge-api.postman_collection.json -e docs/integration/postman/turnbridge-env.postman_environment.json`
  - 可搭配 `--env-var import_id=imp_xxx` 覆蓋單次測試。
  - CI 使用 `scripts/newman-smoke.sh`，並由 `.github/workflows/newman-smoke.yml` 自動執行（無 Token 時會自動略過）。

> 若新增腳本或 CLI，請在此檔增列章節並附上執行步驟。
> **環境變數覆寫**：`docs/integration/scripts/newman-smoke.sh` 會讀取下列變數覆蓋 Postman env：`NEWMAN_BASE_URL`、`NEWMAN_TOKEN`（或 `TOKEN`）、`NEWMAN_IMPORT_ID`、`NEWMAN_INVOICE_FILE`、`NEWMAN_INVOICE_MD5`。CI 可透過 Secrets 注入這些值。
