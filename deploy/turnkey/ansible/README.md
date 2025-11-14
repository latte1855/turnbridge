# Turnkey 基礎環境 Ansible Playbook

> 目的：將 `docs/integration/turnkey-flow.yaml` 的設定引用到 IaC，確保各環境的目錄/權限一致。

## 結構

```
deploy/turnkey/ansible/
  ├── inventory.ini   # 範例 inventory，請依環境調整
  ├── turnkey.yml     # 主 playbook（引用 docs/integration/turnkey-flow.yaml）
  └── templates/
      └── turnkey-systemd.service.j2
```

## 需求
- Ansible 2.12+
- 目標主機可透過 SSH 連線並具有 sudo 權限
- 事先手動完成 Turnkey Installer（官方要求），本 playbook 僅負責外部目錄/服務設定

## 使用方式

```bash
cd deploy/turnkey/ansible
ansible-playbook -i inventory.ini turnkey.yml \
  -e "target_user=turnkey"
```

Playbook 會：
1. 確保 `/turnkey`（或 `turnkey-flow.yaml` 中指定的 base）及 INBOX/OUTBOX/ERROR 目錄存在，並套用權限。
2. 將 `docs/integration/turnkey-flow.yaml` 複製到 `/etc/turnbridge/turnkey-flow.yaml` 供稽核/監控使用。
3. 生成 `turnkey-pickup.service`、`turnkey-receive.service` 兩支 systemd 服務骨架，可在部署後填入實際指令。

> 如需 Windows / Task Scheduler 版本，可在 `turnkey-flow.md` 的指南基礎上另行撰寫腳本。
