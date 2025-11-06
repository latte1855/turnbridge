package com.asynctide.turnbridge.domain.enumeration;

/**
 * 字軌區間狀態（TrackRange.status）
 */
public enum TrackRangeStatus {
    /**
     * 可用
     */
    ACTIVE("可用"),
    /**
     * 用盡
     */
    EXHAUSTED("用盡"),
    /**
     * 結束（含期末收斂）
     */
    CLOSED("結束");

    private final String value;

    TrackRangeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
