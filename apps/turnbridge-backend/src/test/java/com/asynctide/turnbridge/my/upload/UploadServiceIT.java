package com.asynctide.turnbridge.my.upload;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.service.upload.UploadMetadata;
import com.asynctide.turnbridge.service.upload.UploadResponse;
import com.asynctide.turnbridge.service.upload.UploadService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * UploadService 行為驗證：確保 Normalize 成功與失敗都能留下 ImportFile 記錄。
 */
@IntegrationTest
@Transactional
class UploadServiceIT {

    private static final String SELLER_ID = "24556677";

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Autowired
    private ImportFileLogRepository importFileLogRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ImportFileItemRepository importFileItemRepository;

    @Autowired
    private ImportFileItemErrorRepository importFileItemErrorRepository;

    @BeforeEach
    void cleanUp() {
        importFileItemErrorRepository.deleteAll();
        importFileItemRepository.deleteAll();
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileLogRepository.deleteAll();
        importFileRepository.deleteAll();
    }

    @Test
    void handleUpload_shouldReturnImportIdAndPersistNormalizedData() {
        MultipartFile file = csv(
            """
                Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,rawLine,legacyType
                F0401,AB55667788,24556677,15888888,500,25,525,TX,2025-11-03T12:00:00+08:00,RAW-LINE,C0401
                """
        );
        UploadMetadata metadata = new UploadMetadata(
            SELLER_ID,
            "UTF-8",
            "default",
            sha256(file),
            "C0401",
            "upload-it-success"
        );

        UploadResponse response = uploadService.handleUpload(ImportType.INVOICE, file, metadata);

        assertThat(response.importId()).isNotNull();
        ImportFile importFile = importFileRepository.findById(response.importId()).orElseThrow();
        assertThat(importFile.getStatus()).isEqualTo(ImportStatus.NORMALIZED);
        assertThat(importFile.getSuccessCount()).isEqualTo(1);
        assertThat(importFile.getErrorCount()).isZero();
        assertThat(invoiceRepository.findAll()).hasSize(1);
        List<InvoiceItem> items = invoiceItemRepository.findAll();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getDescription()).isEqualTo("項目1");
        List<ImportFileItem> importItems = importFileItemRepository.findAll();
        assertThat(importItems).hasSize(1);
        assertThat(importItems.get(0).getRawData()).isNotBlank();
        assertThat(importFileItemErrorRepository.findAll()).isEmpty();
        List<ImportFileLog> logs = importFileLogRepository.findAll();
        assertThat(logs).hasSize(2);
        assertThat(logs).extracting(ImportFileLog::getEventCode).containsExactlyInAnyOrder("UPLOAD_RECEIVED", "NORMALIZE_SUMMARY");
    }

    @Test
    void handleUpload_whenNormalizeFails_shouldKeepImportFileAndLog() {
        MultipartFile file = csv(
            """
                Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,rawLine,legacyType
                F0401,AB99990001,24556677,15888888,100,5,130,TX,2025-11-04T10:00:00+08:00,RAW-ERR,C0401
                """
        );
        UploadMetadata metadata = new UploadMetadata(
            SELLER_ID,
            "UTF-8",
            "default",
            sha256(file),
            "C0401",
            "upload-it-fail"
        );

        UploadResponse response = uploadService.handleUpload(ImportType.INVOICE, file, metadata);
        assertThat(response.status()).isEqualTo(ImportStatus.FAILED);

        List<ImportFile> imports = importFileRepository.findAll();
        assertThat(imports).hasSize(1);
        ImportFile saved = imports.get(0);
        assertThat(saved.getStatus()).isEqualTo(ImportStatus.FAILED);
        assertThat(saved.getErrorCount()).isEqualTo(1);

        List<ImportFileItem> importItems = importFileItemRepository.findAll();
        assertThat(importItems).hasSize(1);
        List<ImportFileItemError> errors = importFileItemErrorRepository.findAll();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getImportFileItem().getId()).isEqualTo(importItems.get(0).getId());
        List<ImportFileLog> logs = importFileLogRepository.findAll();
        assertThat(logs).hasSize(2);
        assertThat(logs).extracting(ImportFileLog::getImportFile).extracting(ImportFile::getId).containsOnly(saved.getId());
        assertThat(logs).extracting(ImportFileLog::getEventCode).containsExactlyInAnyOrder("UPLOAD_RECEIVED", "NORMALIZE_SUMMARY");
        assertThat(invoiceRepository.findAll()).isEmpty();
    }

    private MultipartFile csv(String content) {
        return new MockMultipartFile("file", "input.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));
    }

    private String sha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | java.io.IOException e) {
            throw new IllegalStateException("Unable to hash test csv", e);
        }
    }
}
