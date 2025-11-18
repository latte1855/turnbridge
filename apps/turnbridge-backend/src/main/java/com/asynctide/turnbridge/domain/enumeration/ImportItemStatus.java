package com.asynctide.turnbridge.domain.enumeration;

/**
 * 匯入明細狀態（ImportFileItem.status）
 */
public enum ImportItemStatus {
    /**
     * 待處理
     */
    PENDING("待處理"),
    /**
     * 已正規化
     */
    NORMALIZED("已正規化"),
    /**
     * 檢核失敗
     */
    FAILED("檢核失敗");

    private final String value;

    ImportItemStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
