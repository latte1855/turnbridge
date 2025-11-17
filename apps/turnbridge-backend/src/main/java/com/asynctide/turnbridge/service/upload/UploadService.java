package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
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

    public UploadService(
        ImportFileRepository importFileRepository,
        ImportFileLogRepository importFileLogRepository,
        NormalizationService normalizationService
    ) {
        this.importFileRepository = importFileRepository;
        this.importFileLogRepository = importFileLogRepository;
        this.normalizationService = normalizationService;
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
        importFile.setOriginalFilename(file.getOriginalFilename());
        importFile.setSha256(actualHash);
        importFile.setTotalCount(0);
        importFile.setSuccessCount(0);
        importFile.setErrorCount(0);
        importFile.setStatus(ImportStatus.RECEIVED);
        if (metadata.hasLegacyType()) {
            importFile.setLegacyType(metadata.legacyType());
        }
        importFile = importFileRepository.save(importFile);
        saveUploadLog(importFile, metadata);
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
}
