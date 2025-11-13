# Turnkey Flow 應用指引

> 目的：說明如何將 `turnkey-flow.yaml` 的設定套用到實際的部署腳本（Systemd / Kubernetes / Ansible），確保各環境的 Turnkey 目錄與排程一致。

---

## 1. 目錄建立與權限（Ansible 範例）

```yaml
# playbooks/turnkey-directories.yml
- hosts: turnkey_servers
  become: true
  vars_files:
    - ../../docs/integration/turnkey-flow.yaml
  tasks:
    - name: Ensure base directory exists
      file:
        path: "{{ directories.base }}"
        state: directory
        owner: "{{ directories.inbox.permissions.user }}"
        group: "{{ directories.inbox.permissions.group }}"
        mode: "{{ directories.inbox.permissions.mode }}"

    - name: Create INBOX/OUTBOX/ERROR
      file:
        path: "{{ item.path }}"
        state: directory
        owner: "{{ directories.inbox.permissions.user }}"
        group: "{{ directories.inbox.permissions.group }}"
        mode: "{{ directories.inbox.permissions.mode }}"
      loop:
        - "{{ directories.inbox }}"
        - "{{ directories.outbox }}"
        - "{{ directories.error }}"
```

> 使用方式：`ansible-playbook playbooks/turnkey-directories.yml -i inventories/prod`

---

## 2. Systemd 服務套用

### 2.1 產生 service 檔

```bash
yq '.turnkey_pickup.service_name' docs/integration/turnkey-flow.yaml
# 輸出 turnkey-pickup
```

`/etc/systemd/system/turnkey-pickup.service`
```ini
[Unit]
Description=Turnkey Pickup
After=network.target

[Service]
User=turnkey
ExecStart=/opt/turnkey/bin/pickup --inbox /turnkey/INBOX
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

### 2.2 套用

```bash
systemctl daemon-reload
systemctl enable --now turnkey-pickup
systemctl status turnkey-pickup
```

同理可為 `turnkey_receive` 產生服務；健康檢查指令可寫入 `docs/operations/turnkey-healthcheck.md` 所列腳本。

---

## 3. Kubernetes CronJob（XML Export & Parser）

`deploy/k8s/xml-exporter.yaml`
```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: turnkey-xml-exporter
spec:
  schedule: "*/5 * * * *"
  jobTemplate:
    spec:
      template:
        spec:
          containers:
            - name: exporter
              image: registry/turnbridge-xml-exporter:latest
              env:
                - name: TURNKEY_INBOX
                  value: "/turnkey/INBOX"
          restartPolicy: OnFailure
```

Cron expression、batch size 等參數對應 `turnkey-flow.yaml` 中的 `xml_export.schedule`、`xml_export.batch_size`。

---

## 4. 監控與告警映射

| YAML 欄位 | Grafana/Prometheus 指標 | 備註 |
| --- | --- | --- |
| `alerts[0].metric = turnkey_inbox_backlog` | `turnkey_inbox_backlog` | 觸發 P2 告警後依 `monitoring.md` 流程處理 |
| `alerts[1].metric = turnkey_outbox_backlog` | `turnkey_outbox_backlog` | 配合 Parser CronJob 指標 |
| `alerts[2].metric = turnkey_error_files` | `turnkey_error_files` | 一有值即為 P1 |

---

## 5. DEV / UAT 參考值

| 環境 | base 路徑 | 排程頻率 | 備註 |
| --- | --- | --- | --- |
| DEV | `/mnt/turnkey/dev` | 10 分鐘 | 可使用模擬 Turnkey 或腳本 |
| UAT | `/turnkey` | 5 分鐘 | 使用正式 Turnkey + 測試憑證 |
| PROD | `/turnkey` | 5 分鐘 | 依 `turnkey-flow.yaml` 預設 |

> 將此檔與 `turnkey-flow.yaml` 一起納入 Infra Repo，可確保 IaC 與文件一致。
