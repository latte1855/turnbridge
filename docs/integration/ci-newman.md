# Newman Smoke 測試整合指南

> 目的：說明如何在 CI Pipeline 中執行 `scripts/newman-smoke.sh`，並將測試報告存檔/產生告警。

---

## 1. 先備條件
- `npm install -g newman` 已在 CI 基底映像或步驟中安裝。
- `docs/integration/postman/turnbridge-api.postman_collection.json`
- `docs/integration/postman/turnbridge-env.postman_environment.json`  
  > 建議於 CI 中以 Secret/Vault 覆蓋 `token`、`base_url` 等敏感欄位。
- `scripts/newman-smoke.sh` 可在 repo 內呼叫。

---

## 2. GitHub Actions 範例

```yaml
name: Smoke Tests
on:
  workflow_dispatch:
  push:
    branches: [ main ]

jobs:
  newman-smoke:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - run: npm install -g newman
      - name: Run smoke script
        env:
          TOKEN: ${{ secrets.TURNBRIDGE_TOKEN }}
          base_url: https://turnbridge.example.com
        run: |
          export NEWMAN_GLOBALS_TOKEN="$TOKEN"
          ./docs/integration/scripts/newman-smoke.sh
      - name: Upload report
        uses: actions/upload-artifact@v4
        with:
          name: newman-report
          path: workspace/e2e-reports/newman-smoke-*.json
```

> 可於 Workflow 內加入條件，僅在 API/整合目錄變動時執行。

---

## 3. Jenkins Declarative Pipeline

```groovy
pipeline {
  agent { label 'node18' }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Install Newman') {
      steps { sh 'npm install -g newman' }
    }
    stage('Smoke Test') {
      environment {
        TOKEN = credentials('turnbridge-token')
      }
      steps {
        sh '''
          export TOKEN=$TOKEN
          ./docs/integration/scripts/newman-smoke.sh
        '''
      }
    }
  }
  post {
    always {
      archiveArtifacts artifacts: 'workspace/e2e-reports/newman-smoke-*.json', allowEmptyArchive: true
    }
  }
}
```

---

## 4. 報告與告警
- JSON 報告存放於 `workspace/e2e-reports/`；可透過 jq 解析並上傳至監控系統。
- 若失敗，可連結到 `docs/integration/e2e-scenarios.md` 對應案例，快速排查。
- 建議設置 Slack/Email 通知，並在 `DECISION_LOG` 中記錄重大失敗案例（關聯 DEC-011）。

---

## 5. TODO
- 依 Pipeline 工具補充對應的變數覆蓋方式（例如 GitLab CI、Azure DevOps）。  
- 若 Newman 報告需轉換為 HTML，可使用 `newman-reporter-htmlextra` 並附於 artifact。  
- 之後可將 `turnkey-flow.yaml` 檢查也納入同一 Pipeline（IaC 檢核）。
