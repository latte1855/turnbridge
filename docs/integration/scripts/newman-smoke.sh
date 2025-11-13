#!/usr/bin/env bash
set -euo pipefail

COLLECTION="${COLLECTION:-docs/integration/postman/turnbridge-api.postman_collection.json}"
ENV_FILE="${ENV_FILE:-docs/integration/postman/turnbridge-env.postman_environment.json}"
REPORT_DIR="${REPORT_DIR:-workspace/e2e-reports}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

mkdir -p "$REPORT_DIR"

if ! command -v newman >/dev/null 2>&1; then
  echo "[ERROR] newman not installed. Install via 'npm install -g newman'." >&2
  exit 1
fi

echo "[INFO] Running Newman collection: $COLLECTION"
newman run "$COLLECTION" -e "$ENV_FILE" \
  --reporters cli,json \
  --reporter-json-export "$REPORT_DIR/newman-smoke-$TIMESTAMP.json"

echo "[INFO] Report saved to $REPORT_DIR/newman-smoke-$TIMESTAMP.json"
