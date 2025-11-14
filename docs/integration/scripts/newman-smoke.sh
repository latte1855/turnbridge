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

TMP_ENV=$(mktemp)
trap 'rm -f "$TMP_ENV"' EXIT
cp "$ENV_FILE" "$TMP_ENV"

python3 - "$TMP_ENV" <<'PY'
import json, os, sys
path = sys.argv[1]
overrides = {
    "base_url": os.environ.get("NEWMAN_BASE_URL"),
    "token": os.environ.get("NEWMAN_TOKEN") or os.environ.get("TOKEN"),
    "import_id": os.environ.get("NEWMAN_IMPORT_ID"),
    "invoice_file": os.environ.get("NEWMAN_INVOICE_FILE"),
    "invoice_md5": os.environ.get("NEWMAN_INVOICE_MD5"),
}
with open(path, encoding="utf-8") as f:
    data = json.load(f)
for entry in data.get("values", []):
    key = entry.get("key")
    if key in overrides and overrides[key]:
        entry["value"] = overrides[key]
with open(path, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)
PY

echo "[INFO] Running Newman collection: $COLLECTION"
newman run "$COLLECTION" -e "$TMP_ENV" \
  --reporters cli,json \
  --reporter-json-export "$REPORT_DIR/newman-smoke-$TIMESTAMP.json"

echo "[INFO] Report saved to $REPORT_DIR/newman-smoke-$TIMESTAMP.json"
