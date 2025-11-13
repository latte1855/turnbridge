# Turnkey 目錄與排程健康檢查（Healthcheck）

> 目的：確保與財政部 Turnkey 系統的連線、目錄權限、排程與回饋解析正常運作。  
> 適用：日常檢查、部署後驗證、Incident 排查。

---

## 1. 檢查頻率與責任

| 頻率 | 項目 | 負責角色 |
| --- | --- | --- |
| 每日 | 目錄容量、Turnkey 排程狀態、最新 ACK/ERROR | Ops 值班 |
| 每週 | 權限/憑證有效性、備援主機同步 | Ops + 系統管理 |
| 重大變更後 | 全流程驗證（INBOX→MOF→OUTBOX→Webhook） | Integration + Ops |

---

## 2. 目錄檢查清單

```bash
# 確認磁碟使用率
df -h /turnkey

# 檢查 INBOX / OUTBOX / ERROR 目錄大小與檔案數
for dir in INBOX OUTBOX ERROR; do
  echo "== $dir ==";
  find /turnkey/$dir -maxdepth 1 -type f | wc -l;
  du -sh /turnkey/$dir;
done
```

| 項目 | 門檻 | 處置 |
| --- | --- | --- |
| `/INBOX` 檔案 > 10 (運行中) | 代表 Turnkey 未即時 pickup | 檢查 Turnkey 排程或上傳速率 |
| `/OUTBOX` 檔案 > 5 | 代表回饋未解析 | 檢查 Parser 排程或權限 |
| `/ERROR` 有新檔 | 需立即分析並通知相對應客戶 |

---

## 3. 排程狀態

| 排程 | 功能 | 檢查方式 |
| --- | --- | --- |
| Turnkey Pickup | 讀取 `/INBOX` → 傳送 MOF | `systemctl status turnkey-pickup` 或查看 Windows 排程 |
| Turnkey Receive | 下載 MOF 回饋 → `/OUTBOX` | `systemctl status turnkey-receive` |
| Backend Parser | 解析 `/OUTBOX` | `kubectl get cronjobs turnkey-parser` |
| Backend XML Export | 產生 XML → `/INBOX` | `kubectl get cronjobs xml-exporter` |

若任一排程中止，需依 Incident Playbook P1/P2 流程處理。

---

## 4. 檔案驗證

### 4.1 XML 取樣

1. 隨機抽取當日 XML，使用 `xmllint` 驗證：  
   ```
   xmllint --noout --schema MIG41_F0401.xsd FG0401_20251112_0001.xml
   ```
2. 確認 `MessageID` 與系統 `TurnkeyMessage.message_id` 一致。

### 4.2 回饋對應

1. 從 `/OUTBOX` 取最新 ACK/ERROR：  
   ```
   tail -n 20 /turnkey/OUTBOX/*.log
   ```
2. 確認對應的 `invoice_no`/`import_id` 在 DB 內已更新。

---

## 5. 連線與憑證

- **網路**：確保 Turnkey 主機能連線至 MOF（ping、traceroute）。  
- **憑證**：檢查 Turnkey 使用的 TLS/簽章憑證到期日（`openssl x509 -enddate`）。  
- **帳號權限**：確認寫入 `/INBOX` 的 service account 未過期；備援帳號亦需定期驗證。

---

## 6. 全流程驗證（Smoke Test）

1. 產生測試 Invoice CSV（10 筆）→ API 上傳。  
2. 確認 Backend 產生 XML，並在 `/INBOX/manual-test/` 可見。  
3. 監看 Turnkey log，確保檔案於 5 分鐘內被 pickup。  
4. 解析 `/OUTBOX` 回饋，確認系統狀態更新並觸發 Webhook。  
5. 將驗證結果記錄於 Ops Log（含時間、檔名、狀態）。

---

## 7. 常見問題排查

| 問題 | 現象 | 處理 |
| --- | --- | --- |
| 目錄無法寫入 | `Permission denied` | 檢查掛載、ACL；可重新授權或切換備援路徑 |
| Turnkey log 停止更新 | Log timestamp 停在數小時前 | 重啟 Turnkey 服務，若仍無法，升級 P1 事件 |
| `/ERROR` 出現大量檔案 | 同一時間新增多個 | 下載檔案比對錯誤碼，回報 Integration/Dev |
| Parser 無法解析回饋 | Backend log 出現 XML parse error | 保留原檔，提報開發修正 XSD 版本或 parser bug |

---

## 8. 紀錄與稽核

- 每日檢查結果需填寫 Ops Checklist（含時間、執行者、異常描述）。  
- 若發生異常，需建立工單並連結到 Incident 或 Manual Resend 流程。  
- 檢查報告保存至少一年以應付客戶稽核。

> 若 Turnkey 版本或目錄策略有變更，記得同步更新本檔與 `DECISION_LOG`。
