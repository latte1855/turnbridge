package com.asynctide.turnbridge.domain.enumeration;

/**
 * The ApprovalStatus enumeration.
 */
public enum ApprovalStatus {
    /**
     * 待審
     */
    PENDING("待審"),
    /**
     * 已核准
     */
    APPROVED("已核准"),
    /**
     * 已拒絕
     */
    REJECTED("已拒絕");

    private final String value;

    ApprovalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
