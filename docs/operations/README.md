# `docs/operations/`

## 目的
提供維運、稽核、客服人員可直接引用的 Runbook 與操作規程，涵蓋：

* 監控指標、告警門檻、儀表板連結
* 事件處理（Incident Playbook）與升級流程
* 人工重送、手動配號、Turnkey 介接檢查等 SOP
* 稽核證跡（Log/Report 保留與匯出）與資安控管

## 文件列表
| 檔名 | 內容 | 狀態 |
| --- | --- | --- |
| `monitoring.md` | 指標、告警門檻、觀測流程 | ✅ 初版 |
| `incident-playbook.md` | Incident 分級、處置 SOP | ✅ 初版 |
| `manual-resend.md` | 人工重送/審核作業 | ✅ 初版 |
| `turnkey-healthcheck.md` | Turnkey 目錄/排程檢查清單 | ✅ 初版 |

## TODO
1. 蒐集現行 Ops/SRE 流程並建檔，必要時補圖（Mermaid 或框架圖）。
2. 檢視 `AGENTS.md §5`、`SRS §5` 是否有新增流程需同步回寫 Runbook。
3. 於完成各文件後，在此 README 標記狀態（Draft/Review/Done）並安排定期複審。
4. 建立與 `DECISION_LOG` 的對應（例如 DEC-006、DEC-011）；新文件需標註決策 ID 與最後審閱日期。

> 維護者：SRE / Ops Squad。任何維運流程調整需在 `DECISION_LOG` 登記並回寫本目錄。
