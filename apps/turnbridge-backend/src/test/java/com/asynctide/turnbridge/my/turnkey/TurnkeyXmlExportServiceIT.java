package com.asynctide.turnbridge.my.turnkey;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.service.turnkey.TurnkeyXmlExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * TurnkeyXmlExportService 匯出流程整合測試。
 */
@IntegrationTest
@Transactional
@TestPropertySource(properties = "turnbridge.turnkey.inbox-dir=target/test-turnkey-inbox")
class TurnkeyXmlExportServiceIT {

    private static final Path TEST_INBOX = Path.of("target/test-turnkey-inbox");

    @Autowired
    private TurnkeyXmlExportService exportService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Autowired
    private ImportFileItemRepository importFileItemRepository;

    @Autowired
    private ImportFileLogRepository importFileLogRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Tenant tenant;

    @BeforeEach
    void setup() throws IOException {
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileItemRepository.deleteAll();
        importFileRepository.deleteAll();
        tenantRepository.deleteAll();
        importFileLogRepository.deleteAll();
        tenant = tenantRepository.save(new Tenant().name("XML Export 租戶").code("TEN-EXPORT").status("ACTIVE"));
        deleteDirectory(TEST_INBOX);
    }

    @Test
    void exportPendingInvoices_shouldGenerateXmlAndUpdateStatus() throws Exception {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoiceItemRepository.saveAll(createItems(invoice));
        persistRawItem(importFile, invoice, Map.of("InvoiceType", "07", "RandomNumber", "5566"));

        int processed = exportService.exportPendingInvoices(5);

        assertThat(processed).isEqualTo(1);
        Invoice reloaded = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(reloaded.getInvoiceStatus()).isEqualTo(InvoiceStatus.IN_PICKUP);

        Path tenantDir = TEST_INBOX.resolve("TEN-EXPORT");
        assertThat(Files.exists(tenantDir)).isTrue();
        List<Path> xmlFiles;
        try (var stream = Files.list(tenantDir)) {
            xmlFiles = stream.toList();
        }
        assertThat(xmlFiles).hasSize(1);
        String xml = Files.readString(xmlFiles.get(0));
        assertThat(xml).contains("<InvoiceNumber>" + invoice.getInvoiceNo() + "</InvoiceNumber>");

        List<ImportFileLog> logs = importFileLogRepository.findAll();
        assertThat(logs)
            .anyMatch(log -> "XML_GENERATED".equals(log.getEventCode()) && log.getImportFile().getId().equals(importFile.getId()));
    }

    private ImportFile persistImportFile() {
        ImportFile file = new ImportFile();
        file.setImportType(ImportType.INVOICE);
        file.setOriginalFilename("turnkey.csv");
        file.setSha256("1".repeat(64));
        file.setStatus(ImportStatus.NORMALIZED);
        file.setTotalCount(1);
        file.setSuccessCount(1);
        file.setErrorCount(0);
        file.setTenant(tenant);
        return importFileRepository.saveAndFlush(file);
    }

    private Invoice persistInvoice(ImportFile importFile) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("ZX55668877");
        invoice.setMessageFamily(MessageFamily.F0401);
        invoice.setSellerId("24556677");
        invoice.setSellerName("XML Export Seller");
        invoice.setBuyerId("12345678");
        invoice.setBuyerName("XML Export Buyer");
        invoice.setSalesAmount(new BigDecimal("1000"));
        invoice.setTaxAmount(new BigDecimal("50"));
        invoice.setTotalAmount(new BigDecimal("1050"));
        invoice.setTaxType("1");
        invoice.setInvoiceStatus(InvoiceStatus.NORMALIZED);
        invoice.setIssuedAt(Instant.parse("2024-11-18T05:00:00Z"));
        invoice.setImportFile(importFile);
        invoice.setTenant(tenant);
        return invoiceRepository.saveAndFlush(invoice);
    }

    private List<InvoiceItem> createItems(Invoice invoice) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setSequence(1);
        item.setDescription("測試商品");
        item.setQuantity(BigDecimal.ONE);
        item.setUnitPrice(new BigDecimal("1000"));
        item.setAmount(new BigDecimal("1000"));
        return List.of(item);
    }

    private void persistRawItem(ImportFile importFile, Invoice invoice, Map<String, Object> raw) throws JsonProcessingException {
        ImportFileItem item = new ImportFileItem();
        item.setImportFile(importFile);
        item.setInvoice(invoice);
        item.setLineIndex(1);
        item.setRawData(objectMapper.writeValueAsString(raw));
        item.setStatus(ImportItemStatus.NORMALIZED);
        importFileItemRepository.saveAndFlush(item);
    }

    private void deleteDirectory(Path target) throws IOException {
        if (!Files.exists(target)) {
            return;
        }
        try (var stream = Files.walk(target)) {
            stream.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {}
            });
        }
    }
}
