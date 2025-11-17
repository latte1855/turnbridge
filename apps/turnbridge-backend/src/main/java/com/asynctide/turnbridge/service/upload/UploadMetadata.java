package com.asynctide.turnbridge.service.upload;

import org.springframework.util.StringUtils;

/**
 * 上傳附帶的中繼資料（來源、編碼、校驗資訊）。
 */
public record UploadMetadata(
    /** 賣方統編或客戶識別碼 */
    String sellerId,
    /** 檔案編碼（預設 UTF-8） */
    String encoding,
    /** Profile 名稱（Legacy/Canonical…） */
    String profile,
    /** 上傳檔案的 SHA-256（hex） */
    String sha256,
    /** 原訊息別（可選） */
    String legacyType,
    /** Idempotency-Key（用於重複上傳防護） */
    String idempotencyKey
) {

    public boolean hasSeller() {
        return StringUtils.hasText(sellerId);
    }

    public boolean hasLegacyType() {
        return StringUtils.hasText(legacyType);
    }
}
