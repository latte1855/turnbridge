package com.asynctide.turnbridge.my.turnkey;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.TurnkeyMessage;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.TurnkeyMessageRepository;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.service.turnkey.TurnkeyProcessResultService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * TurnkeyProcessResultService 解析流程整合測試。
 */
@IntegrationTest
@Transactional
@TestPropertySource(properties = "turnbridge.turnkey.process-result-base=target/test-turnkey-process")
class TurnkeyProcessResultServiceIT {

    private static final Path PROCESS_BASE = Path.of("target/test-turnkey-process");

    @Autowired
    private TurnkeyProcessResultService processResultService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Autowired
    private ImportFileLogRepository importFileLogRepository;

    @Autowired
    private TurnkeyMessageRepository turnkeyMessageRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Tenant tenant;

    @BeforeEach
    void setup() throws IOException {
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileRepository.deleteAll();
        importFileLogRepository.deleteAll();
        turnkeyMessageRepository.deleteAll();
        tenantRepository.deleteAll();
        deleteDirectory(PROCESS_BASE);
        tenant = tenantRepository.save(new Tenant().name("ProcessResult 租戶").code("TEN-PR").status("ACTIVE"));
    }

    @Test
    void pollProcessResults_shouldUpdateInvoice_andPersistTurnkeyMessage() throws Exception {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoiceItemRepository.saveAll(createItems(invoice));
        Path payloadPath = createProcessResultFile(invoice.getInvoiceNo(), "0", null, null);

        processResultService.pollProcessResults();

        Invoice reloaded = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(reloaded.getInvoiceStatus()).isEqualTo(InvoiceStatus.ACKED);
        List<TurnkeyMessage> messages = turnkeyMessageRepository.findAll();
        assertThat(messages)
            .hasSize(1)
            .first()
            .satisfies(message -> {
                assertThat(message.getInvoice()).isNotNull();
                assertThat(message.getPayloadPath()).isEqualTo(payloadPath.toAbsolutePath().toString() + ".done");
            });
        List<ImportFileLog> logs = importFileLogRepository.findAll();
        assertThat(logs).anyMatch(log -> "PROCESS_RESULT".equals(log.getEventCode()));
        assertThat(Files.exists(payloadPath)).isFalse();
        assertThat(Files.exists(payloadPath.resolveSibling(payloadPath.getFileName() + ".done"))).isTrue();
    }

    @Test
    void pollProcessResults_whenError_shouldMapTbCodeAndSetStatus() throws Exception {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoiceItemRepository.saveAll(createItems(invoice));
        Path payloadPath = createProcessResultFile(invoice.getInvoiceNo(), "9", "5003", "資料不符");

        processResultService.pollProcessResults();

        Invoice reloaded = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(reloaded.getInvoiceStatus()).isEqualTo(InvoiceStatus.ERROR);
        assertThat(reloaded.getTbCode()).isNotBlank();
        assertThat(reloaded.getTbCategory()).isNotBlank();
        assertThat(reloaded.getTbSourceCode()).isEqualTo("5003");
        assertThat(reloaded.getTbSourceMessage()).contains("資料不符");

        List<TurnkeyMessage> messages = turnkeyMessageRepository.findAll();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getInvoice().getId()).isEqualTo(invoice.getId());
        assertThat(messages.get(0).getCode()).isEqualTo("5003");
        assertThat(Files.exists(payloadPath.resolveSibling(payloadPath.getFileName() + ".done"))).isTrue();

        ImportFileLog log = importFileLogRepository.findAll().stream().filter(entry -> "PROCESS_RESULT".equals(entry.getEventCode())).findFirst().orElseThrow();
        JsonNode detail = objectMapper.readTree(log.getDetail());
        assertThat(detail.get("tbCode").asText()).isNotBlank();
        assertThat(detail.get("turnkeyMessageId").asLong()).isPositive();
    }

    private ImportFile persistImportFile() {
        ImportFile file = new ImportFile();
        file.setImportType(ImportType.INVOICE);
        file.setOriginalFilename("process.csv");
        file.setSha256("3".repeat(64));
        file.setStatus(ImportStatus.NORMALIZED);
        file.setTotalCount(1);
        file.setSuccessCount(1);
        file.setErrorCount(0);
        file.setTenant(tenant);
        return importFileRepository.saveAndFlush(file);
    }

    private Invoice persistInvoice(ImportFile importFile) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("ZX11223344");
        invoice.setMessageFamily(MessageFamily.F0401);
        invoice.setSellerId("23669988");
        invoice.setSellerName("ProcessResult Seller");
        invoice.setBuyerId("12345678");
        invoice.setBuyerName("ProcessResult Buyer");
        invoice.setSalesAmount(new BigDecimal("1500"));
        invoice.setTaxAmount(new BigDecimal("75"));
        invoice.setTotalAmount(new BigDecimal("1575"));
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
        item.setDescription("ProcessResult 商品");
        item.setQuantity(BigDecimal.ONE);
        item.setUnitPrice(new BigDecimal("1500"));
        item.setAmount(new BigDecimal("1500"));
        return List.of(item);
    }

    private Path createProcessResultFile(String invoiceNo, String resultCode, String errorCode, String errorMessage) throws IOException {
        Path targetDir = PROCESS_BASE.resolve("F0401").resolve("ProcessResult");
        Files.createDirectories(targetDir);
        Path file = targetDir.resolve(invoiceNo + "-result.xml");
        String content =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <ProcessResult>
              <ResultCode>%s</ResultCode>
              <ResultMessage>%s</ResultMessage>
              <InvoiceNumber>%s</InvoiceNumber>
              <ErrorCode>%s</ErrorCode>
              <ErrorMessage>%s</ErrorMessage>
            </ProcessResult>
            """
                .formatted(resultCode, resultCode.equals("0") ? "OK" : "ERR", invoiceNo, errorCode != null ? errorCode : "", errorMessage != null ? errorMessage : "");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var stream = Files.walk(path)) {
            stream.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {}
            });
        }
    }
}
