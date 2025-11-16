package com.asynctide.turnbridge.domain.enumeration;

/**
 * The ManualActionType enumeration.
 */
public enum ManualActionType {
    /**
     * 重送 XML
     */
    RESEND_XML("重送 XML"),
    /**
     * 補派號段
     */
    ASSIGN_NO("補派號段");

    private final String value;

    ManualActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
