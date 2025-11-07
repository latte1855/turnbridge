package com.asynctide.turnbridge.storage.local;

import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.storage.StoredObjectRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * 基於本機檔案系統的儲存實作（開發/測試用）。
 * <p>
 * 以 {@code baseDir/bucket/objectKey} 為實際存放位置；不處理目錄清理與版本化。
 */
public class LocalFsStorageProvider implements StorageProvider {
    private static final Logger log = LoggerFactory.getLogger(LocalFsStorageProvider.class);

    private final Path baseDir;

    /**
     * @param baseDir 根目錄（例如 /tmp/turnbridge/storage）
     */
    public LocalFsStorageProvider(Path baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public StoredObjectRef store(InputStream in, long contentLength, String mediaType,
                                 String bucket, String objectKey, Map<String, String> metadata) {
        try {
            Path target = baseDir.resolve(bucket).resolve(objectKey).normalize();
            Files.createDirectories(target.getParent());

            // 邊寫邊計算 SHA-256
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            long written = 0;
            try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                    sha256.update(buf, 0, n);
                    written += n;
                }
            }
            String hex = bytesToHex(sha256.digest());
            log.info("LocalFs stored: {}/{} ({} bytes, sha256={})", bucket, objectKey, written, hex);

            return new StoredObjectRef(bucket, objectKey, mediaType, written, hex, "STANDARD", null, Instant.now());
        } catch (Exception e) {
            throw new UncheckedIOException("LocalFs store failed: " + e.getMessage(), new IOException(e));
        }
    }

    @Override
    public Optional<InputStream> open(String bucket, String objectKey) {
        try {
            Path target = baseDir.resolve(bucket).resolve(objectKey).normalize();
            if (Files.exists(target)) {
                return Optional.of(Files.newInputStream(target, StandardOpenOption.READ));
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        Path target = baseDir.resolve(bucket).resolve(objectKey).normalize();
        return Files.exists(target);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b: bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
