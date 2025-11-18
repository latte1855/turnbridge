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
import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.service.upload.NormalizationException;
import com.asynctide.turnbridge.service.upload.NormalizationService;
import com.asynctide.turnbridge.service.upload.UploadMetadata;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

/**
 * NormalizeService 的整合測試：檢驗成功、錯誤記錄與 ProblemDetail。
 */
@IntegrationTest
@Transactional
class NormalizationServiceIT {

    private static final String SELLER_ID = "24556677";

    @Autowired
    private NormalizationService normalizationService;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ImportFileLogRepository importFileLogRepository;

    @Autowired
    private ImportFileItemRepository importFileItemRepository;

    @Autowired
    private ImportFileItemErrorRepository importFileItemErrorRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant tenant;

    @BeforeEach
    void cleanRepositories() {
        importFileItemErrorRepository.deleteAll();
        importFileItemRepository.deleteAll();
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileLogRepository.deleteAll();
        importFileRepository.deleteAll();
        tenantRepository.deleteAll();
        tenant = tenantRepository.save(new Tenant().name("測試租戶").code("TEN-NORM").status("ACTIVE"));
    }

    @Test
    void normalize_shouldPersistInvoiceAndItems() {
        ImportFile importFile = persistImportFile();
        MultipartFile file = csv(
            """
                Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,rawLine,legacyType
                F0401,AB12345678,24556677,15888888,1000,50,1050,TX,2025-11-01T10:00:00+08:00,RAW-001,C0401
                """
        );
        UploadMetadata metadata = new UploadMetadata(SELLER_ID, "UTF-8", "default", "sha-value", "C0401", "case-success");

        normalizationService.normalize(importFile, file, metadata);

        ImportFile refreshed = importFileRepository.findById(importFile.getId()).orElseThrow();
        assertThat(refreshed.getStatus()).isEqualTo(ImportStatus.NORMALIZED);
        assertThat(refreshed.getSuccessCount()).isEqualTo(1);
        assertThat(refreshed.getErrorCount()).isZero();

        Invoice invoice = invoicesByImport(importFile).stream().findFirst().orElseThrow();
        assertThat(invoice.getInvoiceNo()).isEqualTo("AB12345678");
        assertThat(invoice.getInvoiceStatus()).isEqualTo(InvoiceStatus.NORMALIZED);
        assertThat(invoice.getSalesAmount()).isNotNull();
        assertThat(invoice.getSalesAmount()).isEqualByComparingTo("1000");
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo("1050");

        List<InvoiceItem> items = itemsByInvoice(invoice);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getDescription()).isEqualTo("項目1");

        List<ImportFileItem> importItems = itemsByImportFile(importFile);
        assertThat(importItems).hasSize(1);
        assertThat(importItems.get(0).getStatus()).isEqualTo(ImportItemStatus.NORMALIZED);
        assertThat(importItems.get(0).getNormalizedJson()).isNotBlank();
        assertThat(importFileItemErrorRepository.findAll()).isEmpty();
        List<ImportFileLog> logs = logsByImport(importFile);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getEventCode()).isEqualTo("NORMALIZE_SUMMARY");
        assertThat(logs.get(0).getLevel()).isEqualTo("INFO");
    }

    @Test
    void normalize_shouldAggregateMultipleRowsForSameInvoice() {
        ImportFile importFile = persistImportFile();
        MultipartFile file = csv(
            """
                Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,ItemDescription,ItemQuantity,ItemAmount,legacyType
                F0401,AB77777777,24556677,15888888,1000,50,1050,TX,2025-11-05T10:00:00+08:00,貨品A,2,500,C0401
                F0401,AB77777777,24556677,15888888,1000,50,1050,TX,2025-11-05T10:00:00+08:00,貨品B,1,500,C0401
                """
        );
        UploadMetadata metadata = new UploadMetadata(SELLER_ID, "UTF-8", "default", "sha-value", "C0401", "case-multi");

        normalizationService.normalize(importFile, file, metadata);

        ImportFile refreshed = importFileRepository.findById(importFile.getId()).orElseThrow();
        assertThat(refreshed.getSuccessCount()).isEqualTo(2);
        assertThat(refreshed.getErrorCount()).isZero();

        List<Invoice> invoices = invoicesByImport(importFile);
        assertThat(invoices).hasSize(1);
        List<InvoiceItem> invoiceItems = itemsByInvoice(invoices.get(0));
        assertThat(invoiceItems).hasSize(2);
        assertThat(invoiceItems).extracting(InvoiceItem::getDescription).containsExactly("貨品A", "貨品B");

        List<ImportFileItem> importItems = itemsByImportFile(importFile);
        assertThat(importItems).hasSize(2);
        assertThat(importItems).allMatch(item -> item.getStatus() == ImportItemStatus.NORMALIZED);
        assertThat(importFileItemErrorRepository.findAll()).isEmpty();
    }

    @Test
    void normalize_withAmountMismatch_shouldLogErrorAndFailBatch() {
        ImportFile importFile = persistImportFile();
        MultipartFile file = csv(
            """
                Type,InvoiceNo,SellerId,BuyerId,SalesAmount,Tax,Total,TaxType,DateTime,rawLine,legacyType
                F0401,AB90000001,24556677,15888888,100,5,120,TX,2025-11-02T10:00:00+08:00,RAW-ERR,C0401
                """
        );
        UploadMetadata metadata = new UploadMetadata(SELLER_ID, "UTF-8", "default", "sha-value", "C0401", "case-fail");

        normalizationService.normalize(importFile, file, metadata);

        ImportFile refreshed = importFileRepository.findById(importFile.getId()).orElseThrow();
        assertThat(refreshed.getStatus()).isEqualTo(ImportStatus.FAILED);
        assertThat(refreshed.getTotalCount()).isEqualTo(1);
        assertThat(refreshed.getSuccessCount()).isZero();
        assertThat(refreshed.getErrorCount()).isEqualTo(1);

        List<ImportFileItem> importItems = itemsByImportFile(importFile);
        assertThat(importItems).hasSize(1);
        assertThat(importItems.get(0).getStatus()).isEqualTo(ImportItemStatus.FAILED);
        List<ImportFileItemError> errors = errorsByImportFile(importFile);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getErrorCode()).isEqualTo("AMOUNT_MISMATCH");
        assertThat(invoicesByImport(importFile)).isEmpty();

        List<ImportFileLog> logs = logsByImport(importFile);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getEventCode()).isEqualTo("NORMALIZE_SUMMARY");
        assertThat(logs.get(0).getLevel()).isEqualTo("WARN");
    }

    @Test
    void normalize_withEmptyCsvShouldRaiseProblemDetail() {
        ImportFile importFile = persistImportFile();
        MultipartFile file = csv("Type,InvoiceNo\n");
        UploadMetadata metadata = new UploadMetadata(SELLER_ID, "UTF-8", "default", "sha-value", "C0401", "case-empty");

        NormalizationException ex = catchThrowableOfType(
        	NormalizationException.class,
            () -> normalizationService.normalize(importFile, file, metadata)
            
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).contains("CSV 無資料");

        ProblemDetailWithCause detail = ex.getProblemDetailWithCause();
        assertThat(detail.getType().toString()).contains("normalization-failure");
        assertThat(detail.getProperties().get("errorCode")).isEqualTo("EMPTY_FILE");
        assertThat(detail.getProperties().get("field")).isEqualTo("file");

        ImportFile refreshed = importFileRepository.findById(importFile.getId()).orElseThrow();
        assertThat(refreshed.getStatus()).isEqualTo(ImportStatus.FAILED);
        assertThat(itemsByImportFile(importFile)).isEmpty();
        List<ImportFileLog> logs = logsByImport(importFile);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getEventCode()).isEqualTo("NORMALIZE_FAILURE");
    }

    private ImportFile persistImportFile() {
        ImportFile importFile = new ImportFile();
        importFile.setImportType(ImportType.INVOICE);
        importFile.setOriginalFilename("test.csv");
        importFile.setSha256("0".repeat(64));
        importFile.setTotalCount(0);
        importFile.setSuccessCount(0);
        importFile.setErrorCount(0);
        importFile.setStatus(ImportStatus.RECEIVED);
        importFile.setLegacyType("C0401");
        importFile.setTenant(tenant);
        return importFileRepository.saveAndFlush(importFile);
    }

    private MultipartFile csv(String content) {
        return new MockMultipartFile("file", "input.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));
    }

    private List<Invoice> invoicesByImport(ImportFile importFile) {
        return invoiceRepository
            .findAll()
            .stream()
            .filter(inv -> inv.getImportFile() != null && inv.getImportFile().getId().equals(importFile.getId()))
            .collect(Collectors.toList());
    }

    private List<InvoiceItem> itemsByInvoice(Invoice invoice) {
        return invoiceItemRepository
            .findAll()
            .stream()
            .filter(item -> item.getInvoice() != null && item.getInvoice().getId().equals(invoice.getId()))
            .collect(Collectors.toList());
    }

    private List<ImportFileLog> logsByImport(ImportFile importFile) {
        return importFileLogRepository
            .findAll()
            .stream()
            .filter(log -> log.getImportFile() != null && log.getImportFile().getId().equals(importFile.getId()))
            .collect(Collectors.toList());
    }

    private List<ImportFileItem> itemsByImportFile(ImportFile importFile) {
        return importFileItemRepository
            .findAll()
            .stream()
            .filter(item -> item.getImportFile() != null && item.getImportFile().getId().equals(importFile.getId()))
            .collect(Collectors.toList());
    }

    private List<ImportFileItemError> errorsByImportFile(ImportFile importFile) {
        List<Long> itemIds = itemsByImportFile(importFile).stream().map(ImportFileItem::getId).collect(Collectors.toList());
        return importFileItemErrorRepository
            .findAll()
            .stream()
            .filter(err -> err.getImportFileItem() != null && itemIds.contains(err.getImportFileItem().getId()))
            .collect(Collectors.toList());
    }
}
