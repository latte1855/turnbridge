package com.asynctide.turnbridge.domain.enumeration;

/**
 * The DeliveryResult enumeration.
 */
public enum DeliveryResult {
    /**
     * 成功
     */
    SUCCESS("成功"),
    /**
     * 失敗
     */
    FAILED("失敗"),
    /**
     * 重試中
     */
    RETRY("重試中");

    private final String value;

    DeliveryResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
