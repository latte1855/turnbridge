package com.asynctide.turnbridge.my.turnkey;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.service.turnkey.TurnkeyXmlBuildResult;
import com.asynctide.turnbridge.service.turnkey.TurnkeyXmlBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Turnkey XML Builder 整合測試。
 */
@IntegrationTest
@Transactional
class TurnkeyXmlBuilderIT {

    private static final String SELLER_ID = "24556677";

    @Autowired
    private TurnkeyXmlBuilder turnkeyXmlBuilder;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Autowired
    private ImportFileItemRepository importFileItemRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Tenant tenant;

    @BeforeEach
    void init() {
        importFileItemRepository.deleteAll();
        invoiceItemRepository.deleteAll();
        invoiceRepository.deleteAll();
        importFileRepository.deleteAll();
        tenantRepository.deleteAll();
        tenant = tenantRepository.save(new Tenant().name("Turnkey 租戶").code("TEN-XML").status("ACTIVE"));
    }

    @Test
    void buildF0401_shouldGenerateXmlWithBasicFields() {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoiceItemRepository.saveAll(createItems(invoice));
        persistRawItem(importFile, invoice, 1, Map.of("InvoiceType", "07", "RandomNumber", "1234"));

        TurnkeyXmlBuildResult result = turnkeyXmlBuilder.build(invoice);

        assertThat(result.messageFamily()).isEqualTo(MessageFamily.F0401);
        assertThat(result.xml()).contains("<InvoiceNumber>" + invoice.getInvoiceNo() + "</InvoiceNumber>");
        assertThat(result.xml()).contains(invoice.getSellerName());
    }

    @Test
    void buildF0501_shouldGenerateCancelXml() {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoice.setMessageFamily(MessageFamily.F0501);
        invoice = invoiceRepository.saveAndFlush(invoice);
        persistRawItem(
            importFile,
            invoice,
            1,
            Map.of(
                "InvoiceDate",
                "2024-10-31",
                "BuyerId",
                "15888888",
                "SellerId",
                SELLER_ID,
                "CancelDate",
                "2024-11-05",
                "CancelTime",
                "093000",
                "CancelReason",
                "開立錯誤"
            )
        );

        TurnkeyXmlBuildResult result = turnkeyXmlBuilder.build(invoice);

        assertThat(result.messageFamily()).isEqualTo(MessageFamily.F0501);
        assertThat(result.xml()).contains("<CancelReason>開立錯誤</CancelReason>");
        assertThat(result.xml()).contains("<CancelInvoiceNumber>" + invoice.getInvoiceNo() + "</CancelInvoiceNumber>");
    }

    @Test
    void buildF0701_shouldGenerateVoidXml() {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoice.setMessageFamily(MessageFamily.F0701);
        invoice = invoiceRepository.saveAndFlush(invoice);
        persistRawItem(
            importFile,
            invoice,
            1,
            Map.of(
                "InvoiceDate",
                "2024-10-31",
                "BuyerId",
                "15888888",
                "SellerId",
                SELLER_ID,
                "VoidDate",
                "2024-11-06",
                "VoidTime",
                "101500",
                "VoidReason",
                "發票註銷"
            )
        );

        TurnkeyXmlBuildResult result = turnkeyXmlBuilder.build(invoice);

        assertThat(result.messageFamily()).isEqualTo(MessageFamily.F0701);
        assertThat(result.xml()).contains("<VoidReason>發票註銷</VoidReason>");
        assertThat(result.xml()).contains("<VoidInvoiceNumber>" + invoice.getInvoiceNo() + "</VoidInvoiceNumber>");
    }

    @Test
    void buildG0401_shouldGenerateAllowanceXml() {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoice.setMessageFamily(MessageFamily.G0401);
        invoice.setSalesAmount(new BigDecimal("200"));
        invoice.setTaxAmount(new BigDecimal("10"));
        invoice.setTotalAmount(new BigDecimal("210"));
        invoice = invoiceRepository.saveAndFlush(invoice);
        invoiceItemRepository.saveAll(createItems(invoice));
        persistRawItem(
            importFile,
            invoice,
            1,
            Map.of(
                "AllowanceType",
                "1",
                "AllowanceDate",
                "20241105",
                "OriginalInvoiceNumber",
                "AB98765432",
                "OriginalInvoiceDate",
                "20241030",
                "TaxType",
                "1"
            )
        );

        TurnkeyXmlBuildResult result = turnkeyXmlBuilder.build(invoice);

        assertThat(result.messageFamily()).isEqualTo(MessageFamily.G0401);
        assertThat(result.xml()).contains("<AllowanceNumber>" + invoice.getInvoiceNo() + "</AllowanceNumber>");
        assertThat(result.xml()).contains("<OriginalInvoiceNumber>AB98765432</OriginalInvoiceNumber>");
    }

    @Test
    void buildG0501_shouldGenerateAllowanceCancelXml() {
        ImportFile importFile = persistImportFile();
        Invoice invoice = persistInvoice(importFile);
        invoice.setMessageFamily(MessageFamily.G0501);
        invoice = invoiceRepository.saveAndFlush(invoice);
        persistRawItem(
            importFile,
            invoice,
            1,
            Map.of(
                "AllowanceType",
                "1",
                "AllowanceDate",
                "20241105",
                "BuyerId",
                "55667788",
                "SellerId",
                SELLER_ID,
                "CancelDate",
                "20241107",
                "CancelTime",
                "150000",
                "CancelReason",
                "折讓作廢"
            )
        );

        TurnkeyXmlBuildResult result = turnkeyXmlBuilder.build(invoice);

        assertThat(result.messageFamily()).isEqualTo(MessageFamily.G0501);
        assertThat(result.xml()).contains("<CancelAllowanceNumber>" + invoice.getInvoiceNo() + "</CancelAllowanceNumber>");
        assertThat(result.xml()).contains("<CancelReason>折讓作廢</CancelReason>");
    }

    private ImportFile persistImportFile() {
        ImportFile file = new ImportFile();
        file.setImportType(ImportType.INVOICE);
        file.setOriginalFilename("turnkey.csv");
        file.setSha256("0".repeat(64));
        file.setStatus(ImportStatus.NORMALIZED);
        file.setTotalCount(1);
        file.setSuccessCount(1);
        file.setErrorCount(0);
        file.setTenant(tenant);
        return importFileRepository.saveAndFlush(file);
    }

    private Invoice persistInvoice(ImportFile importFile) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("ZX12345678");
        invoice.setMessageFamily(MessageFamily.F0401);
        invoice.setSellerId(SELLER_ID);
        invoice.setSellerName("Turnbridge Demo Seller");
        invoice.setBuyerId("15888888");
        invoice.setBuyerName("Demo Buyer");
        invoice.setSalesAmount(new BigDecimal("1000"));
        invoice.setTaxAmount(new BigDecimal("50"));
        invoice.setTotalAmount(new BigDecimal("1050"));
        invoice.setTaxType("1");
        invoice.setInvoiceStatus(InvoiceStatus.NORMALIZED);
        invoice.setIssuedAt(Instant.parse("2024-11-01T10:00:00Z"));
        invoice.setImportFile(importFile);
        invoice.setTenant(tenant);
        return invoiceRepository.saveAndFlush(invoice);
    }

    private List<InvoiceItem> createItems(Invoice invoice) {
        InvoiceItem item1 = new InvoiceItem();
        item1.setInvoice(invoice);
        item1.setSequence(1);
        item1.setDescription("商品A");
        item1.setQuantity(new BigDecimal("2"));
        item1.setUnitPrice(new BigDecimal("500"));
        item1.setAmount(new BigDecimal("1000"));

        InvoiceItem item2 = new InvoiceItem();
        item2.setInvoice(invoice);
        item2.setSequence(2);
        item2.setDescription("商品B");
        item2.setQuantity(BigDecimal.ONE);
        item2.setUnitPrice(new BigDecimal("50"));
        item2.setAmount(new BigDecimal("50"));
        return List.of(item1, item2);
    }

    private void persistRawItem(ImportFile importFile, Invoice invoice, int lineIndex, Map<String, Object> raw) {
        ImportFileItem item = new ImportFileItem();
        item.setImportFile(importFile);
        item.setInvoice(invoice);
        item.setLineIndex(lineIndex);
        item.setRawData(writeJson(raw));
        item.setStatus(ImportItemStatus.NORMALIZED);
        importFileItemRepository.saveAndFlush(item);
    }

    private String writeJson(Map<String, Object> raw) {
        try {
            return objectMapper.writeValueAsString(raw);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("raw data 序列化失敗", e);
        }
    }
}
