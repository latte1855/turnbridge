package com.asynctide.turnbridge.domain.enumeration;

/**
 * The ImportStatus enumeration.
 */
public enum ImportStatus {
    /**
     * 已接收
     */
    RECEIVED("已接收"),
    /**
     * 上傳中
     */
    UPLOADING("上傳中"),
    /**
     * 上傳完成
     */
    UPLOADED("上傳完成"),
    /**
     * 已正規化
     */
    NORMALIZED("已正規化"),
    /**
     * 失敗
     */
    FAILED("失敗");

    private final String value;

    ImportStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
