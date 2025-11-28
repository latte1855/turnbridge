package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.web.rest.errors.BadRequestAlertException;
import com.asynctide.turnbridge.tenant.TenantContextHolder;
import com.asynctide.turnbridge.upload.UploadProperties;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 處理上傳與建立 ImportFile 的服務。
 */
@Service
public class UploadService {

    private static final Logger log = LoggerFactory.getLogger(UploadService.class);

    private final ImportFileRepository importFileRepository;
    private final ImportFileLogRepository importFileLogRepository;
    private final NormalizationService normalizationService;
    private final TenantRepository tenantRepository;
    private final UploadProperties uploadProperties;

    public UploadService(
        ImportFileRepository importFileRepository,
        ImportFileLogRepository importFileLogRepository,
        NormalizationService normalizationService,
        TenantRepository tenantRepository,
        UploadProperties uploadProperties
    ) {
        this.importFileRepository = importFileRepository;
        this.importFileLogRepository = importFileLogRepository;
        this.normalizationService = normalizationService;
        this.tenantRepository = tenantRepository;
        this.uploadProperties = uploadProperties;
    }

    @Transactional(noRollbackFor = NormalizationException.class)
    public UploadResponse handleUpload(ImportType type, MultipartFile file, UploadMetadata metadata) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestAlertException("上傳檔案不可為空", "importFile", "filemissing");
        }
        if (!StringUtils.hasText(metadata.sha256())) {
            throw new BadRequestAlertException("必須提供 SHA-256", "importFile", "sha256required");
        }

        String actualHash = calculateSha256(file);
        if (!actualHash.equalsIgnoreCase(metadata.sha256().trim())) {
            throw new BadRequestAlertException("SHA-256 不符", "importFile", "sha256mismatch");
        }

        ImportFile importFile = new ImportFile();
        importFile.setImportType(type);
        importFile.setOriginalFilename(resolveOriginalFilename(file, type));
        importFile.setSha256(actualHash);
        importFile.setTotalCount(0);
        importFile.setSuccessCount(0);
        importFile.setErrorCount(0);
        importFile.setStatus(ImportStatus.RECEIVED);
        if (metadata.hasLegacyType()) {
            importFile.setLegacyType(metadata.legacyType());
        }
        importFile.setTenant(resolveTenant());
        importFile = importFileRepository.save(importFile);
        saveUploadLog(importFile, metadata);
        backupOriginalFile(importFile, file);
        log.info("Accepted upload [{}] size={} bytes", importFile.getId(), file.getSize());
        normalizationService.normalize(importFile, file, metadata);
        ImportFile latest = importFileRepository.findById(importFile.getId()).orElseThrow();
        return new UploadResponse(latest.getId(), latest.getStatus(), latest.getStatus().name());
    }

    private String calculateSha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] data = file.getBytes();
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException("Unable to compute SHA-256", e);
        }
    }

    private void saveUploadLog(ImportFile importFile, UploadMetadata metadata) {
        ImportFileLog logEntity = new ImportFileLog();
        logEntity.setImportFile(importFile);
        logEntity.setEventCode("UPLOAD_RECEIVED");
        logEntity.setLevel("INFO");
        logEntity.setMessage("批次檔案 " + importFile.getOriginalFilename() + " 已驗證 SHA-256");
        logEntity.setOccurredAt(java.time.Instant.now());
        importFileLogRepository.save(logEntity);
    }

    private com.asynctide.turnbridge.domain.Tenant resolveTenant() {
        return TenantContextHolder
            .get()
            .filter(TenantContext -> TenantContext.tenantId() != null)
            .map(ctx -> tenantRepository.getReferenceById(ctx.tenantId()))
            .orElseThrow(() -> new BadRequestAlertException("缺少租戶資訊", "importFile", "tenantmissing"));
    }

    private String resolveOriginalFilename(MultipartFile file, ImportType type) {
        String originalName = file.getOriginalFilename();
        if (StringUtils.hasText(originalName)) {
            return originalName;
        }
        String prefix = type != null ? type.name().toLowerCase(Locale.ROOT) : "import";
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        return prefix + "_" + timestamp + ".csv";
    }

    private void backupOriginalFile(ImportFile importFile, MultipartFile source) {
        if (!StringUtils.hasText(uploadProperties.getBackupDir())) {
            return;
        }
        try {
            Path targetDir = Path.of(uploadProperties.getBackupDir());
            String tenantCode = importFile.getTenant() != null && StringUtils.hasText(importFile.getTenant().getCode())
                ? importFile.getTenant().getCode()
                : "default";
            targetDir = targetDir.resolve(tenantCode);
            targetDir = targetDir.resolve(DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now()));
            Files.createDirectories(targetDir);
            String originalName = importFile.getOriginalFilename();
            if (!StringUtils.hasText(originalName)) {
                originalName = "upload.csv";
            }
            String sanitizedName = originalName.replaceAll("[^A-Za-z0-9._-]", "_");
            Path backupFile = targetDir.resolve(String.format("import-%06d-%s", importFile.getId(), sanitizedName));
            Files.copy(source.getInputStream(), backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.warn("無法備份上傳檔案 importId={}", importFile.getId(), ex);
        }
    }
}
