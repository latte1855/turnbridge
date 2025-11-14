#!/usr/bin/env python3
"""
Compare webhook event names between docs/requirements/webhook_spec.md
and docs/integration/webhook-contract.md.
"""
from __future__ import annotations

import re
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
SPEC = ROOT / "docs" / "requirements" / "webhook_spec.md"
CONTRACT = ROOT / "docs" / "integration" / "webhook-contract.md"


def extract_events(text: str) -> set[str]:
    pattern = re.compile(r"\b[a-z]+(?:\.[a-z0-9\-]+)+\b")
    candidates = set(pattern.findall(text))
    return {evt for evt in candidates if not evt.startswith("docs") and "." in evt}


def extract_contract_table(text: str) -> set[str]:
    events: set[str] = set()
    for line in text.splitlines():
        line = line.strip()
        if line.startswith("|") and not line.startswith("| ---"):
            cells = [c.strip() for c in line.strip("|").split("|")]
            if cells and "." in cells[0] and cells[0].replace(".", "").replace("-", "").isalnum():
                events.add(cells[0])
    return events


def main() -> int:
    spec_text = SPEC.read_text(encoding="utf-8")
    contract_text = CONTRACT.read_text(encoding="utf-8")

    spec_events = extract_events(spec_text)
    contract_events = extract_contract_table(contract_text)

    missing_in_contract = spec_events - contract_events
    missing_in_spec = contract_events - spec_events

    ok = True
    if missing_in_contract:
        ok = False
        print("[ERROR] Events missing in webhook-contract.md:", ", ".join(sorted(missing_in_contract)))
    if missing_in_spec:
        ok = False
        print("[ERROR] Events missing in webhook_spec.md:", ", ".join(sorted(missing_in_spec)))

    if ok:
        print("[OK] Webhook event definitions are consistent.")
        return 0
    return 1


if __name__ == "__main__":
    sys.exit(main())
