package com.asynctide.turnbridge.domain.enumeration;

/**
 * 批次狀態（UploadJob.status）
 */
public enum UploadJobStatus {
    /**
     * 已接收
     */
    RECEIVED("已接收"),
    /**
     * 解析中
     */
    PARSING("解析中"),
    /**
     * 驗證中
     */
    VALIDATING("驗證中"),
    /**
     * 打包中（MIG/XML）
     */
    PACKING("打包中（MIG/XML）"),
    /**
     * 已送 Turnkey
     */
    SENT("已送 Turnkey"),
    /**
     * 回饋已備妥
     */
    RESULT_READY("回饋已備妥"),
    /**
     * 失敗（需人工或重送）
     */
    FAILED("失敗（需人工或重送）");

    private final String value;

    UploadJobStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
