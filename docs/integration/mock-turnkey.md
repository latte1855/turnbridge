# Mock Turnkey 環境指引

> 目的：在尚未取得財政部憑證或實體 Turnkey 之前，於 DEV/UAT 以腳本模擬 `/INBOX` → `/OUTBOX` 流程，讓 Backend/Webhook 可完整測試。

## 1. 需求
- Linux/macOS（或任何可執行 Python 3.9+ 的環境）
- Python 3.9+，可使用 `venv && pip install -r requirements.txt`（若需要 `PyYAML` 再行安裝）。
- 既有的目錄結構：`/turnkey/INBOX`、`/turnkey/OUTBOX`（可透過 `deploy/turnkey/ansible/turnkey.yml` 建立）。

## 2. 啟動模擬器
```bash
python docs/integration/scripts/mock-turnkey.py \
  --inbox /turnkey/INBOX \
  --outbox /turnkey/OUTBOX \
  --error-dir /turnkey/ERROR \
  --interval 30
```

- 預設會每 30 秒掃描一次 `INBOX/*.xml`。
- 檔名包含 `.error`（可透過 `--error-pattern` 調整）時，會產生 `.error.xml` 並寫入 ERROR 目錄，其餘生成 `.ack.xml`。
- 原始 XML 會移動到 `INBOX/_processed`，避免被重複處理。
- 搭配 `turnkey-flow.yaml` 中的目錄設定，可於 DEV 環境模擬 Turnkey 行為。

## 3. 單次處理模式
若只想處理現有檔案一次，可加入 `--once`：
```bash
python docs/integration/scripts/mock-turnkey.py --inbox ... --outbox ... --once
```

## 4. 配合後端測試
1. API 產生 F/G XML 至 `/turnkey/INBOX`。
2. 模擬器在 30 秒內生成 ACK/ERROR，Backend 解析 `/OUTBOX` 并觸發 Webhook。
3. 可搭配 `docs/integration/e2e-scenarios.md` 的案例驗證 Upload → Turnkey → Feedback 全流程。

## 5. 後續
- 若需要更貼近正式 Turnkey 的行為，可自訂 `mock-turnkey.py`，例如模擬無回應、隨機錯誤等。
- 取得財政部測試憑證後，將實際 Turnkey 安裝於相同路徑，即可無痛切換（Backend/監控設定不需調整）。
