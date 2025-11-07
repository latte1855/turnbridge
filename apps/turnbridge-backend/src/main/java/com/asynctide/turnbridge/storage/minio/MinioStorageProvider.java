package com.asynctide.turnbridge.storage.minio;

import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.storage.StoredObjectRef;
import io.minio.*;
import io.minio.errors.MinioException;

import java.io.InputStream;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * 基於 MinIO 的儲存實作（正式/測試環境）。
 * <p>須於 application.yml 設定 endpoint、accessKey、secretKey。</p>
 */
public class MinioStorageProvider implements StorageProvider {

    private final MinioClient client;

    public MinioStorageProvider(MinioClient client) {
        this.client = client;
    }

    @Override
    public StoredObjectRef store(InputStream in, long contentLength, String mediaType, String bucket, String objectKey, Map<String, String> metadata) {
        try {
            client.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .contentType(mediaType)
                    .stream(in, contentLength, -1)
                    .build()
            );
            // 簡化：sha256 不從 ETag 反推，交由上層另計算或留空
            return new StoredObjectRef(bucket, objectKey, mediaType, Math.max(contentLength, 0), null, "STANDARD", "aws:kms", Instant.now());
        } catch (Exception e) {
            throw new RuntimeException("MinIO store failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<InputStream> open(String bucket, String objectKey) {
        try {
            GetObjectResponse res = client.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
            return Optional.of(res);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        try {
            client.statObject(StatObjectArgs.builder().bucket(bucket).object(objectKey).build());
            return true;
        } catch (MinioException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
