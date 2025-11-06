package com.asynctide.turnbridge.domain.enumeration;

/**
 * 明細處理狀態（UploadJobItem.status）
 */
public enum JobItemStatus {
    /**
     * 佇列中
     */
    QUEUED("佇列中"),
    /**
     * 成功
     */
    OK("成功"),
    /**
     * 失敗
     */
    ERROR("失敗");

    private final String value;

    JobItemStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
