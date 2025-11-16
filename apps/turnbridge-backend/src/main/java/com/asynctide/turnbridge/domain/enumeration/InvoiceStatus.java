package com.asynctide.turnbridge.domain.enumeration;

/**
 * The InvoiceStatus enumeration.
 */
public enum InvoiceStatus {
    /**
     * 草稿
     */
    DRAFT("草稿"),
    /**
     * 已正規化
     */
    NORMALIZED("已正規化"),
    /**
     * 待產 XML
     */
    PENDING_XML("待產 XML"),
    /**
     * Turnkey 處理中
     */
    IN_PICKUP("Turnkey 處理中"),
    /**
     * ACK
     */
    ACKED("ACK"),
    /**
     * 錯誤
     */
    ERROR,
    /**
     * 作廢
     */
    VOIDED("作廢");

    private String value;

    InvoiceStatus() {}

    InvoiceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
