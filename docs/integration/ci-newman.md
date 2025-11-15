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

## 2. GitHub Actions（已建立 `.github/workflows/newman-smoke.yml`）

該 Workflow：
- 於 `workflow_dispatch` 或 `docs/integration/**`/`scripts/**` 有變動時觸發。
- 若 `TURNBRIDGE_TOKEN` secret 未設定，會提示並跳過測試（避免 CI 無條件失敗）。
- 安裝 Newman 後執行 `docs/integration/scripts/newman-smoke.sh`，並上傳 JSON 報告。

> 若要實際對接測試環境，請於 GitHub 專案 Secrets 新增 `TURNBRIDGE_TOKEN`，必要時新增 `NEWMAN_BASE_URL` 等自訂變數。

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

## 4. Secrets 與測試資料

| 變數 | 說明 | 建議來源 |
| --- | --- | --- |
| `TURNBRIDGE_TOKEN` / `NEWMAN_TOKEN` | OAuth access token，用於呼叫 Upload/Import/Webhook API | GitHub Secrets / Jenkins Credentials |
| `NEWMAN_BASE_URL` | API Base URL (`https://turnbridge.dev.example.com`) | CI 環境變數 |
| `NEWMAN_IMPORT_ID` | 可選，若需直接查詢現有匯入 | CI 變數或 `test-scripts.md` 指引 | 
| `NEWMAN_INVOICE_FILE` / `NEWMAN_INVOICE_SHA256` | 若要上傳固定 ZIP，可於 CI 事前產生再覆蓋 | build 步驟 / Artifact |

> **建議流程**：在 workflow 之前執行腳本產生測試 ZIP（或重複使用 sample），再以環境變數覆蓋給 `newman-smoke.sh`；Secrets 需於 GitHub/ Jenkins 中設定，避免出現在 repo。

---

## 5. 報告與告警
- JSON 報告存放於 `workspace/e2e-reports/`；可透過 jq 解析並上傳至監控系統。
- 若失敗，可連結到 `docs/integration/e2e-scenarios.md` 對應案例，快速排查。
- 建議設置 Slack/Email 通知，並在 `DECISION_LOG` 中記錄重大失敗案例（關聯 DEC-011）。

---

## 6. TODO
- 依 Pipeline 工具補充對應的變數覆蓋方式（例如 GitLab CI、Azure DevOps）。  
- 若 Newman 報告需轉換為 HTML，可使用 `newman-reporter-htmlextra` 並附於 artifact。  
- 之後可將 `turnkey-flow.yaml` 檢查也納入同一 Pipeline（IaC 檢核）。
