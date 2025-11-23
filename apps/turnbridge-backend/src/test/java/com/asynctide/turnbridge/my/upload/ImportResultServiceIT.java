package com.asynctide.turnbridge.my.upload;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.service.upload.ImportResultService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * ImportResultService 整合測試：確認 CSV/ZIP 生成行為。
 */
@IntegrationTest
@Transactional
class ImportResultServiceIT {

    @Autowired
    private ImportResultService importResultService;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Autowired
    private ImportFileItemRepository importFileItemRepository;

    @Autowired
    private ImportFileItemErrorRepository importFileItemErrorRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private ImportFile importFile;
    private Tenant tenant;

    @BeforeEach
    void setupData() {
        importFileItemErrorRepository.deleteAll();
        importFileItemRepository.deleteAll();
        importFileRepository.deleteAll();
        invoiceRepository.deleteAll();
        tenantRepository.deleteAll();

        tenant = tenantRepository.save(new Tenant().name("匯出租戶").code("TEN-RESULT").status("ACTIVE"));

        importFile = new ImportFile();
        importFile.setImportType(ImportType.INVOICE);
        importFile.setOriginalFilename("sample.csv");
        importFile.setSha256("f".repeat(64));
        importFile.setTotalCount(2);
        importFile.setSuccessCount(1);
        importFile.setErrorCount(1);
        importFile.setStatus(ImportStatus.FAILED);
        importFile.setTenant(tenant);
        importFileRepository.save(importFile);

        ImportFileItem success = new ImportFileItem();
        success.setImportFile(importFile);
        success.setLineIndex(1);
        success.setRawData("{\"InvoiceNo\":\"AB0001\",\"Amount\":100}");
        success.setRawHash("a".repeat(64));
        success.setSourceFamily("C0401");
        success.setStatus(ImportItemStatus.NORMALIZED);
        importFileItemRepository.save(success);

        Invoice failedInvoice = new Invoice();
        failedInvoice.setInvoiceNo("ZX0002");
        failedInvoice.setMessageFamily(MessageFamily.F0401);
        failedInvoice.setInvoiceStatus(InvoiceStatus.ERROR);
        failedInvoice.setImportFile(importFile);
        failedInvoice.setTenant(tenant);
        failedInvoice.setTbCode("TB-5003");
        failedInvoice.setTbCategory("PLATFORM.DATA_AMOUNT_MISMATCH");
        failedInvoice.setTbCanAutoRetry(false);
        failedInvoice.setTbRecommendedAction("FIX_DATA");
        failedInvoice.setTbSourceCode("E200");
        failedInvoice.setTbSourceMessage("稅額不符");
        failedInvoice.setTbResultCode("9");
        failedInvoice = invoiceRepository.save(failedInvoice);

        ImportFileItem failed = new ImportFileItem();
        failed.setImportFile(importFile);
        failed.setLineIndex(2);
        failed.setRawData("{\"InvoiceNo\":\"AB0002\",\"Amount\":200}");
        failed.setRawHash("b".repeat(64));
        failed.setSourceFamily("C0401");
        failed.setStatus(ImportItemStatus.FAILED);
        failed.setErrorCode("AMOUNT_MISMATCH");
        failed.setErrorMessage("含稅金額與明細加總不一致");
        failed.setInvoice(failedInvoice);
        failed = importFileItemRepository.save(failed);

        ImportFileItemError error = new ImportFileItemError();
        error.setImportFileItem(failed);
        error.setColumnIndex(5);
        error.setFieldName("Amount");
        error.setErrorCode("AMOUNT_MISMATCH");
        error.setMessage("含稅金額與明細加總不一致");
        error.setSeverity("ERROR");
        importFileItemErrorRepository.save(error);
    }

    /** 驗證產出的 CSV 會包含原始欄位、狀態與欄位錯誤摘要。 */
    @Test
    @DisplayName("shouldGenerateCsvWithDynamicColumnsAndErrors(): 驗證產出的 CSV 會包含原始欄位、狀態與欄位錯誤摘要")
    void shouldGenerateCsvWithDynamicColumnsAndErrors() {
        ImportResultService.ResultFile result = importResultService.generateCsv(importFile.getId());
        String csv = new String(result.content(), StandardCharsets.UTF_8);
        assertThat(result.filename()).isEqualTo("sample_result.csv");
        assertThat(csv)
            .contains("status,errorCode,errorMessage,fieldErrors,tbCode,tbCategory,tbCanAutoRetry,tbRecommendedAction,tbResultCode,tbSourceCode,tbSourceMessage");
        assertThat(csv).contains("AB0001,100,NORMALIZED,,,,,,,,,");
        assertThat(csv)
            .contains(
                "AB0002,200,FAILED,AMOUNT_MISMATCH,含稅金額與明細加總不一致,Amount(5):AMOUNT_MISMATCH-含稅金額與明細加總不一致,TB-5003,PLATFORM.DATA_AMOUNT_MISMATCH,false,FIX_DATA,9,E200,稅額不符"
            );
    }

    /** 驗證可一次下載多個批次並打包為 ZIP。 */
    @Test
    @DisplayName("shouldGenerateZipForMultipleImports(): 驗證可一次下載多個批次並打包為 ZIP")
    void shouldGenerateZipForMultipleImports() throws Exception {
        ImportFile another = new ImportFile();
        another.setImportType(ImportType.INVOICE);
        another.setOriginalFilename("another.csv");
        another.setSha256("c".repeat(64));
        another.setTotalCount(0);
        another.setSuccessCount(0);
        another.setErrorCount(0);
        another.setStatus(ImportStatus.NORMALIZED);
        another.setTenant(tenant);
        another = importFileRepository.save(another);

        ImportResultService.ResultFile zip = importResultService.generateZip(List.of(importFile.getId(), another.getId()));
        assertThat(zip.filename()).isEqualTo("import-results.zip");
        try (ZipInputStream zis = new ZipInputStream(new java.io.ByteArrayInputStream(zip.content()))) {
            ZipEntry entry = zis.getNextEntry();
            List<String> names = new java.util.ArrayList<>();
            while (entry != null) {
                names.add(entry.getName());
                entry = zis.getNextEntry();
            }
            assertThat(names).containsExactlyInAnyOrder("sample_result.csv", "another_result.csv");
        }
    }
}
