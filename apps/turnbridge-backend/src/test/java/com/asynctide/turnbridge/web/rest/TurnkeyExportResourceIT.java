package com.asynctide.turnbridge.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.asynctide.turnbridge.security.AuthoritiesConstants;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * TurnkeyExportResource 手動匯出流程整合測試。
 */
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(
    properties = {
        "turnbridge.turnkey.inbox-dir=target/test-turnkey-export-resource",
        "turnbridge.turnkey.b2s-storage-src-base=target/test-turnkey-b2s"
    }
)
class TurnkeyExportResourceIT {

    private static final Path TEST_INBOX = Path.of("target/test-turnkey-export-resource");
    private static final Path TEST_B2S = Path.of("target/test-turnkey-b2s");

    @Autowired
    private MockMvc restMockMvc;

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
    void init() throws IOException {
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileItemRepository.deleteAll();
        importFileRepository.deleteAll();
        importFileLogRepository.deleteAll();
        tenantRepository.deleteAll();
        deleteDirectory(TEST_INBOX);
        deleteDirectory(TEST_B2S);
        tenant = tenantRepository.save(new Tenant().name("ExportResource 租戶").code("TEN-EXPORT-REST").status("ACTIVE"));
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void triggerExport_shouldProcessInvoices_andReturnSummary() throws Exception {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoiceItemRepository.saveAll(createItems(invoice));
        persistRawItem(importFile, invoice, Map.of("InvoiceType", "07"));

        restMockMvc
            .perform(post("/api/turnkey/export").param("batchSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.batchSize").value(10))
            .andExpect(jsonPath("$.processed").value(1));

        Invoice reloaded = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(reloaded.getInvoiceStatus()).isEqualTo(InvoiceStatus.IN_PICKUP);

        try (var stream = Files.list(TEST_INBOX.resolve("TEN-EXPORT-REST"))) {
            List<Path> files = stream.toList();
            assertThat(files).hasSize(1);
            assertThat(Files.readString(files.get(0))).contains("<InvoiceNumber>" + invoice.getInvoiceNo() + "</InvoiceNumber>");
        }

        Path b2sFile = TEST_B2S.resolve("F0401/SRC").resolve("FGPAYLOAD_F0401_20241118_000001.xml");
        assertThat(Files.exists(b2sFile)).isTrue();

        List<ImportFileLog> logs = importFileLogRepository.findAll();
        assertThat(logs).anyMatch(log -> "XML_GENERATED".equals(log.getEventCode()));
        assertThat(logs).anyMatch(log -> "XML_DELIVERED_TO_TURNKEY".equals(log.getEventCode()));
    }

    private ImportFile persistImportFile() {
        ImportFile file = new ImportFile();
        file.setImportType(ImportType.INVOICE);
        file.setOriginalFilename("rest-export.csv");
        file.setSha256("2".repeat(64));
        file.setStatus(ImportStatus.NORMALIZED);
        file.setTotalCount(1);
        file.setSuccessCount(1);
        file.setErrorCount(0);
        file.setTenant(tenant);
        return importFileRepository.saveAndFlush(file);
    }

    private Invoice persistInvoice(ImportFile importFile) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("ZX99887766");
        invoice.setMessageFamily(MessageFamily.F0401);
        invoice.setSellerId("24445556");
        invoice.setSellerName("REST Seller");
        invoice.setBuyerId("12345678");
        invoice.setBuyerName("REST Buyer");
        invoice.setSalesAmount(new BigDecimal("2000"));
        invoice.setTaxAmount(new BigDecimal("100"));
        invoice.setTotalAmount(new BigDecimal("2100"));
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
        item.setDescription("REST 測試商品");
        item.setQuantity(BigDecimal.ONE);
        item.setUnitPrice(new BigDecimal("2000"));
        item.setAmount(new BigDecimal("2000"));
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
