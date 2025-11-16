package com.asynctide.turnbridge.domain.enumeration;

/**
 * The WebhookStatus enumeration.
 */
public enum WebhookStatus {
    /**
     * 啟用
     */
    ACTIVE("啟用"),
    /**
     * 停用
     */
    DISABLED("停用");

    private final String value;

    WebhookStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
