package com.asynctide.turnbridge.storage;

import java.time.Instant;

/**
 * 已儲存物件的引用資訊（非 JPA 實體）。
 * <p>呼叫 StorageProvider.store(...) 後回傳，提供持久化 StoredObject 所需欄位。</p>
 */
public record StoredObjectRef(
        String bucket,
        String objectKey,
        String mediaType,
        long contentLength,
        String sha256,
        String storageClass,
        String encryption,
        Instant createdAt
) {}
