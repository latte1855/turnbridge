package com.asynctide.turnbridge.my.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.service.upload.ImportResultService;
import com.asynctide.turnbridge.service.upload.NormalizationException;
import com.asynctide.turnbridge.service.upload.UploadMetadata;
import com.asynctide.turnbridge.service.upload.UploadResponse;
import com.asynctide.turnbridge.service.upload.UploadService;
import com.asynctide.turnbridge.tenant.TenantContext;
import com.asynctide.turnbridge.tenant.TenantContextHolder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired
    private ImportResultService importResultService;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant tenant;

    @BeforeEach
    void cleanUp() {
        importFileItemErrorRepository.deleteAll();
        importFileItemRepository.deleteAll();
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileLogRepository.deleteAll();
        importFileRepository.deleteAll();
        tenantRepository.deleteAll();
        tenant = tenantRepository.save(new Tenant().name("整合租戶").code("TEN-IT").status("ACTIVE"));
        TenantContextHolder.set(new TenantContext(tenant.getId(), tenant.getCode(), false, List.of()));
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
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
        assertThat(logs).hasSize(3);
        assertThat(logs).extracting(ImportFileLog::getImportFile).extracting(ImportFile::getId).containsOnly(saved.getId());
        assertThat(logs)
            .extracting(ImportFileLog::getEventCode)
            .containsExactlyInAnyOrder("UPLOAD_RECEIVED", "NORMALIZE_SUMMARY", "NORMALIZE_ROW_ERROR");
        ImportFileLog rowError = logs
            .stream()
            .filter(log -> "NORMALIZE_ROW_ERROR".equals(log.getEventCode()))
            .findFirst()
            .orElseThrow();
        assertThat(rowError.getDetail()).contains("AMOUNT_MISMATCH").contains("\"lineIndex\":1");
        ImportFileLog summary = logs.stream().filter(log -> "NORMALIZE_SUMMARY".equals(log.getEventCode())).findFirst().orElseThrow();
        assertThat(summary.getLevel()).isEqualTo("WARN");
        assertThat(invoiceRepository.findAll()).isEmpty();

        ImportResultService.ResultFile csvFile = importResultService.generateCsv(saved.getId());
        String csv = new String(csvFile.content(), StandardCharsets.UTF_8);
        assertThat(csv).contains("AB99990001");
        assertThat(csv).contains("FAILED");
        assertThat(csv).contains("AMOUNT_MISMATCH");
    }

    @Test
    void handleUpload_whenExceedLineLimit_shouldThrowItemLimitException() {
        MultipartFile file = csv(buildLargeCsv(1000));
        UploadMetadata metadata = new UploadMetadata(
            SELLER_ID,
            "UTF-8",
            "default",
            sha256(file),
            "C0401",
            "upload-it-limit"
        );

        NormalizationException ex = catchThrowableOfType(
            NormalizationException.class,
            () -> uploadService.handleUpload(ImportType.INVOICE, file, metadata)
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getProblemDetailWithCause().getProperties().get("errorCode")).isEqualTo("ITEM_LIMIT_EXCEEDED");

        List<ImportFile> imports = importFileRepository.findAll();
        assertThat(imports).hasSize(1);
        ImportFile saved = imports.get(0);
        assertThat(saved.getStatus()).isEqualTo(ImportStatus.FAILED);
        assertThat(importFileLogRepository.findAll()).extracting(ImportFileLog::getEventCode).contains("UPLOAD_RECEIVED", "NORMALIZE_FAILURE");
    }

    @Test
    void handleUpload_whenFilenameMissing_shouldAssignFallback() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "",
            "text/csv",
            """
                Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,rawLine,legacyType
                F0401,AB12345000,24556677,15888888,500,25,525,TX,2025-11-05T12:00:00+08:00,RAW-LINE,C0401
                """.getBytes(StandardCharsets.UTF_8)
        );
        UploadMetadata metadata = new UploadMetadata(
            SELLER_ID,
            "UTF-8",
            "default",
            sha256(file),
            "C0401",
            "upload-it-fallback"
        );

        UploadResponse response = uploadService.handleUpload(ImportType.INVOICE, file, metadata);

        ImportFile saved = importFileRepository.findById(response.importId()).orElseThrow();
        assertThat(saved.getOriginalFilename()).isNotBlank();
        assertThat(saved.getOriginalFilename()).endsWith(".csv");
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

    private String buildLargeCsv(int rowCount) {
        StringBuilder builder = new StringBuilder();
        builder.append("Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,rawLine,legacyType\n");
        for (int i = 0; i < rowCount; i++) {
            builder
                .append("F0401,LINE")
                .append(String.format("%05d", i))
                .append(",24556677,15888888,100,5,105,TX,2025-11-05T10:00:00+08:00,RAW-")
                .append(i)
                .append(",C0401\n");
        }
        return builder.toString();
    }
}
