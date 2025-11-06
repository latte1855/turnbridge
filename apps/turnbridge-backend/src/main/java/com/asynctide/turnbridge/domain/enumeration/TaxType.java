package com.asynctide.turnbridge.domain.enumeration;

/**
 * 稅別（UploadJobItem.taxType，依 MIG4.1 常見分類抽象化）
 */
public enum TaxType {
    /**
     * 應稅（TX）
     */
    TAXABLE("應稅"),
    /**
     * 零稅率
     */
    ZERO("零稅率"),
    /**
     * 免稅
     */
    EXEMPT("免稅");

    private final String value;

    TaxType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
