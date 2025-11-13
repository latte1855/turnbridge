# `docs/operations/`

## 目的
提供維運、稽核、客服人員可直接引用的 Runbook 與操作規程，涵蓋：

* 監控指標、告警門檻、儀表板連結
* 事件處理（Incident Playbook）與升級流程
* 人工重送、手動配號、Turnkey 介接檢查等 SOP
* 稽核證跡（Log/Report 保留與匯出）與資安控管

## 預計文件
| 檔名（建議） | 內容 |
| --- | --- |
| `monitoring.md` | Metrics/Logs/Tracing 指標、Grafana/Alertmanager 設定 |
| `incident-playbook.md` | 常見事件類型、處置步驟、RTO/RPO 目標 |
| `manual-resend.md` | 人工重送/審核流程與權限控制（對應 SRS §5、AGENTS §5） |
| `turnkey-healthcheck.md` | Turnkey 目錄/排程檢查清單 |

## TODO
1. 蒐集現行 Ops/SRE 流程並建檔，必要時補圖（Mermaid 或框架圖）。
2. 將 `AGENTS.md §5`、`SRS §5` 之人工流程細節拆成 Runbook。
3. 於完成各文件後，在此 README 的表格中標記狀態（Draft/Review/Done）。
4. 建立與 `DECISION_LOG` 的對應（例如 DEC-006、DEC-011）以追蹤稽核需求。

> 維護者：SRE / Ops Squad。任何維運流程調整需在 `DECISION_LOG` 登記並回寫本目錄。
