#!/usr/bin/env bash
set -euo pipefail

PLAYBOOK="deploy/turnkey/ansible/turnkey.yml"
SOURCE="docs/integration/turnkey-flow.yaml"

if [[ ! -f "$PLAYBOOK" ]]; then
  echo "[ERROR] $PLAYBOOK not found" >&2
  exit 1
fi

if ! grep -Fq "$SOURCE" "$PLAYBOOK"; then
  echo "[ERROR] $PLAYBOOK must include vars_files entry for $SOURCE" >&2
  exit 1
fi

echo "[OK] $PLAYBOOK references $SOURCE"
