package com.asynctide.turnbridge.domain.enumeration;

/**
 * Turnbridge Backend — Domain JDL v1.1
 * - 依據 `turnbridge-srs-v1.0.md`、`docs/AGENTS_MAPPING_v1.md`、`dev-roadmap.md`
 * - 全實體預計繼承 AbstractAuditingEntity（建立/修改資訊）
 */
public enum ImportType {
    /**
     * 發票/折讓
     */
    INVOICE("發票/折讓"),
    /**
     * 配號檔
     */
    E0501("配號檔");

    private final String value;

    ImportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
