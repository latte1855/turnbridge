package com.asynctide.turnbridge.domain.enumeration;

/**
 * 儲存物件用途（StoredObject.purpose）
 */
public enum StoragePurpose {
    /**
     * 原始上傳 CSV/ZIP
     */
    UPLOAD_ORIGINAL("原始上傳 CSV/ZIP"),
    /**
     * 回饋 CSV
     */
    RESULT_CSV("回饋 CSV"),
    /**
     * 產出的 MIG 4.1 XML（壓縮/封裝後）
     */
    MIG_XML("產出的 MIG XML"),
    /**
     * 財政部回饋 XML（Process/Summary）
     */
    FEEDBACK_XML("財政部回饋 XML"),
    /**
     * 其他
     */
    OTHER("其他");

    private final String value;

    StoragePurpose(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
