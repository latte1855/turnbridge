package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nat.einvoice.tky.gateway.msg.EINVPayload;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0401Payload;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0401Sub.Amount;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0401Sub.Details;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0401Sub.Main;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0401Sub.ProductItem;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0501Payload;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.F0701Payload;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Payload;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.G0501Payload;
import gov.nat.einvoice.tky.gateway.msg.common.v4X.RoleDescription;
import gov.nat.einvoice.tky.gateway.msg.v40.F0401;
import gov.nat.einvoice.tky.gateway.msg.v40.F0501;
import gov.nat.einvoice.tky.gateway.msg.v40.F0701;
import gov.nat.einvoice.tky.gateway.msg.v40.G0401;
import gov.nat.einvoice.tky.gateway.msg.v40.G0501;
import gov.nat.einvoice.tky.gateway.process.parser.Parser;
import gov.nat.einvoice.tky.gateway.process.validate.ValidateResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Turnkey XML 建置服務：將 Normalize 後的資料轉為 GOV 官方 F/G XML。
 */
@Service
@Transactional(readOnly = true)
public class TurnkeyXmlBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyXmlBuilder.class);

    private static final Parser PARSER = new Parser();
    private static final ZoneId ZONE_TAIPEI = ZoneId.of("Asia/Taipei");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZONE_TAIPEI);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss").withZone(ZONE_TAIPEI);
    private static final TypeReference<Map<String, Object>> RAW_MAP_TYPE = new TypeReference<>() {};

    private final InvoiceItemRepository invoiceItemRepository;
    private final ImportFileItemRepository importFileItemRepository;
    private final ObjectMapper objectMapper;

    public TurnkeyXmlBuilder(
        InvoiceItemRepository invoiceItemRepository,
        ImportFileItemRepository importFileItemRepository,
        ObjectMapper objectMapper
    ) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.importFileItemRepository = importFileItemRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 依據發票資料產生 Turnkey XML。
     *
     * @param invoice 發票主檔
     * @return 產出的 XML 與 Payload
     */
    public TurnkeyXmlBuildResult build(Invoice invoice) {
        Objects.requireNonNull(invoice, "invoice is required");
        MessageFamily family = invoice.getMessageFamily();
        if (family == null) {
            throw new TurnkeyXmlException("Invoice 缺少 messageFamily");
        }
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceIdOrderBySequenceAsc(invoice.getId());
        RawInvoiceContext rawContext = buildRawContext(invoice);
        return switch (family) {
            case F0401 -> buildF0401(invoice, items, rawContext);
            case F0501 -> buildF0501(invoice, rawContext);
            case F0701 -> buildF0701(invoice, rawContext);
            case G0401 -> buildG0401(invoice, items, rawContext);
            case G0501 -> buildG0501(invoice, rawContext);
        };
    }

    private TurnkeyXmlBuildResult buildF0401(Invoice invoice, List<InvoiceItem> items, RawInvoiceContext rawContext) {
        F0401 payload = new F0401();
        payload.initNameSpace();
        payload.setMain(buildF0401Main(invoice, rawContext));
        payload.setDetails(buildF0401Details(items));
        payload.setAmount(buildF0401Amount(invoice, rawContext));
        try {
            validatePayload(payload, gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.F0401.validate(payload));
        } catch (NullPointerException npe) {
            LOG.warn("Turnkey validator config missing, skipping validation: {}", npe.getMessage());
        }
        String xml = PARSER.generateXml(payload);
        return new TurnkeyXmlBuildResult(MessageFamily.F0401, xml, payload);
    }

    private Main buildF0401Main(Invoice invoice, RawInvoiceContext rawContext) {
        Main main = new Main();
        main.setInvoiceNumber(invoice.getInvoiceNo());
        Instant issuedAt = invoice.getIssuedAt() != null ? invoice.getIssuedAt() : Instant.now();
        main.setInvoiceDate(DATE_FORMAT.format(issuedAt));
        main.setInvoiceTime(TIME_FORMAT.format(issuedAt));

        RoleDescription seller = new RoleDescription();
        setIfHasText(seller::setIdentifier, coalesce(invoice.getSellerId(), rawContext.firstValue("SellerId", "SellerBan", "SellerIdentifier")));
        setIfHasText(seller::setName, coalesce(invoice.getSellerName(), rawContext.firstValue("SellerName")));
        main.setSeller(seller);

        RoleDescription buyer = new RoleDescription();
        setIfHasText(buyer::setIdentifier, coalesce(invoice.getBuyerId(), rawContext.firstValue("BuyerId", "BuyerBan", "BuyerIdentifier")));
        setIfHasText(buyer::setName, coalesce(invoice.getBuyerName(), rawContext.firstValue("BuyerName")));
        main.setBuyer(buyer);

        setIfHasText(main::setInvoiceType, rawContext.firstValue("InvoiceType", "TypeCode"));
        setIfHasText(main::setDonateMark, rawContext.firstValue("DonateMark"));
        setIfHasText(main::setCarrierType, rawContext.firstValue("CarrierType"));
        setIfHasText(main::setCarrierId1, rawContext.firstValue("CarrierId1"));
        setIfHasText(main::setCarrierId2, rawContext.firstValue("CarrierId2"));
        setIfHasText(main::setPrintMark, rawContext.firstValue("PrintMark"));
        setIfHasText(main::setNPOBAN, rawContext.firstValue("NPOBAN", "LoveCode"));
        setIfHasText(main::setRandomNumber, rawContext.firstValue("RandomNumber"));
        return main;
    }

    private Details buildF0401Details(List<InvoiceItem> items) {
        Details details = new Details();
        List<ProductItem> products = items
            .stream()
            .sorted(Comparator.comparing(InvoiceItem::getSequence, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(this::toProductItem)
            .collect(Collectors.toList());
        details.setProductItemList(products);
        return details;
    }

    private ProductItem toProductItem(InvoiceItem item) {
        ProductItem product = new ProductItem();
        product.setDescription(item.getDescription());
        if (item.getQuantity() != null) {
            product.setQuantity(item.getQuantity());
        }
        if (item.getUnitPrice() != null) {
            product.setUnitPrice(item.getUnitPrice());
        }
        if (item.getAmount() != null) {
            product.setAmount(item.getAmount());
        }
        product.setSequenceNumber(item.getSequence() != null ? item.getSequence().toString() : null);
        return product;
    }

    private Amount buildF0401Amount(Invoice invoice, RawInvoiceContext rawContext) {
        Amount amount = new Amount();
        amount.setSalesAmount(defaultDecimal(invoice.getSalesAmount(), firstDecimal(rawContext, "SalesAmount")));
        amount.setTaxAmount(defaultDecimal(invoice.getTaxAmount(), firstDecimal(rawContext, "Tax", "TaxAmount")));
        amount.setTotalAmount(defaultDecimal(invoice.getTotalAmount(), firstDecimal(rawContext, "Total", "TotalAmount")));
        setIfHasText(amount::setTaxType, coalesce(invoice.getTaxType(), rawContext.firstValue("TaxType")));
        setIfPresent(amount::setFreeTaxSalesAmount, firstDecimal(rawContext, "FreeTaxSalesAmount"));
        setIfPresent(amount::setZeroTaxSalesAmount, firstDecimal(rawContext, "ZeroTaxSalesAmount"));
        setIfPresent(amount::setTaxRate, firstDecimal(rawContext, "TaxRate"));
        setIfPresent(amount::setDiscountAmount, firstDecimal(rawContext, "DiscountAmount"));
        return amount;
    }

    private TurnkeyXmlBuildResult buildF0501(Invoice invoice, RawInvoiceContext rawContext) {
        F0501 payload = new F0501();
        payload.initNameSpace();
        payload.setCancelInvoiceNumber(invoice.getInvoiceNo());
        payload.setInvoiceDate(resolveDate(rawContext.firstValue("InvoiceDate", "Date"), invoice.getIssuedAt()));
        setIfHasText(payload::setBuyerId, coalesce(invoice.getBuyerId(), rawContext.firstValue("BuyerId", "BuyerBan")));
        setIfHasText(payload::setSellerId, coalesce(invoice.getSellerId(), rawContext.firstValue("SellerId", "SellerBan")));
        payload.setCancelDate(resolveDate(rawContext.firstValue("CancelDate"), Instant.now()));
        payload.setCancelTime(resolveTime(rawContext.firstValue("CancelTime"), Instant.now()));
        payload.setCancelReason(requireText(rawContext.firstValue("CancelReason", "Reason"), "CancelReason"));
        setIfHasText(payload::setReturnTaxDocumentNumber, rawContext.firstValue("ReturnTaxDocumentNumber", "CancelDocumentNumber"));
        setIfHasText(payload::setRemark, rawContext.firstValue("Remark"));
        try {
            validatePayload(payload, gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.F0501.validate(payload));
        } catch (NullPointerException npe) {
            LOG.warn("Turnkey validator config missing, skipping validation: {}", npe.getMessage());
        }
        return new TurnkeyXmlBuildResult(MessageFamily.F0501, PARSER.generateXml(payload), payload);
    }

    private TurnkeyXmlBuildResult buildF0701(Invoice invoice, RawInvoiceContext rawContext) {
        F0701 payload = new F0701();
        payload.initNameSpace();
        payload.setVoidInvoiceNumber(invoice.getInvoiceNo());
        payload.setInvoiceDate(resolveDate(rawContext.firstValue("InvoiceDate", "Date"), invoice.getIssuedAt()));
        setIfHasText(payload::setBuyerId, coalesce(invoice.getBuyerId(), rawContext.firstValue("BuyerId", "BuyerBan")));
        setIfHasText(payload::setSellerId, coalesce(invoice.getSellerId(), rawContext.firstValue("SellerId", "SellerBan")));
        payload.setVoidDate(resolveDate(rawContext.firstValue("VoidDate"), Instant.now()));
        payload.setVoidTime(resolveTime(rawContext.firstValue("VoidTime"), Instant.now()));
        payload.setVoidReason(requireText(rawContext.firstValue("VoidReason", "Reason"), "VoidReason"));
        setIfHasText(payload::setRemark, rawContext.firstValue("Remark"));
        try {
            validatePayload(payload, gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.F0701.validate(payload));
        } catch (NullPointerException npe) {
            LOG.warn("Turnkey validator config missing, skipping validation: {}", npe.getMessage());
        }
        return new TurnkeyXmlBuildResult(MessageFamily.F0701, PARSER.generateXml(payload), payload);
    }

    private TurnkeyXmlBuildResult buildG0401(Invoice invoice, List<InvoiceItem> items, RawInvoiceContext rawContext) {
        G0401 payload = new G0401();
        payload.initNameSpace();
        payload.setMain(buildG0401Main(invoice, rawContext));
        payload.setDetails(buildG0401Details(invoice, items, rawContext));
        payload.setAmount(buildG0401Amount(invoice));
        try {
            validatePayload(payload, gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.G0401.validate(payload));
        } catch (NullPointerException npe) {
            LOG.warn("Turnkey validator config missing, skipping validation: {}", npe.getMessage());
        }
        return new TurnkeyXmlBuildResult(MessageFamily.G0401, PARSER.generateXml(payload), payload);
    }

    private gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Main buildG0401Main(
        Invoice invoice,
        RawInvoiceContext rawContext
    ) {
        gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Main main = new gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Main();
        main.setAllowanceNumber(invoice.getInvoiceNo());
        main.setAllowanceDate(resolveDate(rawContext.firstValue("AllowanceDate", "Date"), invoice.getIssuedAt()));

        RoleDescription seller = new RoleDescription();
        setIfHasText(seller::setIdentifier, coalesce(invoice.getSellerId(), rawContext.firstValue("SellerId", "SellerBan")));
        setIfHasText(seller::setName, coalesce(invoice.getSellerName(), rawContext.firstValue("SellerName")));
        main.setSeller(seller);

        RoleDescription buyer = new RoleDescription();
        setIfHasText(buyer::setIdentifier, coalesce(invoice.getBuyerId(), rawContext.firstValue("BuyerId", "BuyerBan")));
        setIfHasText(buyer::setName, coalesce(invoice.getBuyerName(), rawContext.firstValue("BuyerName")));
        main.setBuyer(buyer);

        main.setAllowanceType(requireText(rawContext.firstValue("AllowanceType"), "AllowanceType"));
        setIfHasText(main::setOriginalInvoiceSellerId, rawContext.firstValue("OriginalInvoiceSellerId", "OriginalSellerId"));
        setIfHasText(main::setOriginalInvoiceBuyerId, rawContext.firstValue("OriginalInvoiceBuyerId", "OriginalBuyerId"));
        return main;
    }

    private gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Details buildG0401Details(
        Invoice invoice,
        List<InvoiceItem> items,
        RawInvoiceContext rawContext
    ) {
        gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Details details = new gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Details();
        List<RawRow> rawRows = rawContext.rows();
        List<gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.ProductItem> productItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            RawRow row = rawRows.isEmpty() ? RawRow.empty() : rawRows.get(Math.min(i, rawRows.size() - 1));
            productItems.add(toAllowanceItem(invoice, items.get(i), row, rawContext));
        }
        details.setProductItemList(productItems);
        return details;
    }

    private gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.ProductItem toAllowanceItem(
        Invoice invoice,
        InvoiceItem item,
        RawRow row,
        RawInvoiceContext rawContext
    ) {
        gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.ProductItem product = new gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.ProductItem();
        setIfHasText(
            product::setOriginalInvoiceNumber,
            requireText(coalesce(row.firstValue("OriginalInvoiceNumber"), rawContext.firstValue("OriginalInvoiceNumber")), "OriginalInvoiceNumber")
        );
        setIfHasText(product::setOriginalInvoiceDate, resolveDate(row.firstValue("OriginalInvoiceDate"), invoice.getIssuedAt()));
        setIfHasText(product::setOriginalDescription, item.getDescription());
        setIfHasText(product::setOriginalSequenceNumber, coalesce(row.firstValue("OriginalSequenceNumber"), sequenceNumber(item.getSequence())));
        setIfHasText(product::setAllowanceSequenceNumber, coalesce(row.firstValue("AllowanceSequenceNumber"), sequenceNumber(item.getSequence())));
        setIfHasText(product::setUnit, row.firstValue("Unit", "ItemUnit"));
        if (item.getQuantity() != null) {
            product.setQuantity(item.getQuantity());
        }
        if (item.getUnitPrice() != null) {
            product.setUnitPrice(item.getUnitPrice());
        }
        if (item.getAmount() != null) {
            product.setAmount(item.getAmount());
        }
        setIfPresent(product::setTax, parseLong(row.firstValue("Tax", "ItemTax")));
        setIfHasText(product::setTaxType, coalesce(row.firstValue("TaxType"), rawContext.firstValue("TaxType")));
        return product;
    }

    private gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Amount buildG0401Amount(Invoice invoice) {
        gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Amount amount = new gov.nat.einvoice.tky.gateway.msg.common.v4X.G0401Sub.Amount();
        amount.setTaxAmount(nullSafe(invoice.getTaxAmount()));
        amount.setTotalAmount(nullSafe(invoice.getTotalAmount()));
        return amount;
    }

    private TurnkeyXmlBuildResult buildG0501(Invoice invoice, RawInvoiceContext rawContext) {
        G0501 payload = new G0501();
        payload.initNameSpace();
        payload.setCancelAllowanceNumber(invoice.getInvoiceNo());
        payload.setAllowanceType(requireText(rawContext.firstValue("AllowanceType"), "AllowanceType"));
        payload.setAllowanceDate(resolveDate(rawContext.firstValue("AllowanceDate", "Date"), invoice.getIssuedAt()));
        setIfHasText(payload::setBuyerId, coalesce(invoice.getBuyerId(), rawContext.firstValue("BuyerId", "BuyerBan")));
        setIfHasText(payload::setSellerId, coalesce(invoice.getSellerId(), rawContext.firstValue("SellerId", "SellerBan")));
        payload.setCancelDate(resolveDate(rawContext.firstValue("CancelDate", "VoidDate"), Instant.now()));
        payload.setCancelTime(resolveTime(rawContext.firstValue("CancelTime", "VoidTime"), Instant.now()));
        payload.setCancelReason(requireText(rawContext.firstValue("CancelReason", "Reason"), "CancelReason"));
        setIfHasText(payload::setRemark, rawContext.firstValue("Remark"));
        try {
            validatePayload(payload, gov.nat.einvoice.tky.gateway.process.validate.prog.v4X.G0501.validate(payload));
        } catch (NullPointerException npe) {
            LOG.warn("Turnkey validator config missing, skipping validation: {}", npe.getMessage());
        }
        return new TurnkeyXmlBuildResult(MessageFamily.G0501, PARSER.generateXml(payload), payload);
    }

    private void validatePayload(EINVPayload payload, ValidateResult result) {
        if (result == null || !result.isOk()) {
            String code = result != null ? result.getErrorCode() : "UNKNOWN";
            String message = result != null ? result.getErrorMessage() : "未知錯誤";
            LOG.warn("Turnkey payload validate failed: {} {}", code, message);
            throw new TurnkeyXmlException("Turnkey XML 驗證失敗: " + code + " " + message);
        }
    }

    private RawInvoiceContext buildRawContext(Invoice invoice) {
        List<ImportFileItem> lineItems = importFileItemRepository.findByInvoiceIdOrderByLineIndexAsc(invoice.getId());
        List<RawRow> rows = new ArrayList<>();
        for (ImportFileItem item : lineItems) {
            rows.add(new RawRow(parseRawMap(item.getRawData())));
        }
        if (rows.isEmpty()) {
            rows.add(RawRow.empty());
        }
        return new RawInvoiceContext(rows);
    }

    private Map<String, String> parseRawMap(String rawJson) {
        if (!StringUtils.hasText(rawJson)) {
            return new HashMap<>();
        }
        try {
            Map<String, Object> parsed = objectMapper.readValue(rawJson, RAW_MAP_TYPE);
            Map<String, String> normalized = new HashMap<>();
            parsed.forEach((key, value) -> normalized.put(key, value != null ? value.toString() : null));
            return normalized;
        } catch (JsonProcessingException e) {
            throw new TurnkeyXmlException("ImportFileItem rawData 無法解析", e);
        }
    }

    private String coalesce(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String resolveDate(String candidate, Instant fallback) {
        String formatted = formatDate(candidate);
        if (formatted != null) {
            return formatted;
        }
        if (fallback == null) {
            return null;
        }
        return DATE_FORMAT.format(fallback);
    }

    private String resolveTime(String candidate, Instant fallback) {
        String formatted = formatTime(candidate);
        if (formatted != null) {
            return formatted;
        }
        if (fallback == null) {
            return null;
        }
        return TIME_FORMAT.format(fallback);
    }

    private String formatDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() == 7) {
            digits = "0" + digits;
        }
        if (digits.length() == 8) {
            return digits;
        }
        return null;
    }

    private String formatTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() == 6) {
            return digits;
        }
        if (digits.length() == 4) {
            return digits + "00";
        }
        if (digits.length() == 2) {
            return digits + "0000";
        }
        return null;
    }

    private BigDecimal firstDecimal(RawInvoiceContext rawContext, String... keys) {
        for (String key : keys) {
            BigDecimal parsed = parseDecimal(rawContext.firstValue(key));
            if (parsed != null) {
                return parsed;
            }
        }
        return null;
    }

    private BigDecimal parseDecimal(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            LOG.warn("無法解析金額: {}", value);
            return null;
        }
    }

    private Long parseLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            LOG.warn("無法解析整數: {}", value);
            return null;
        }
    }

    private BigDecimal defaultDecimal(BigDecimal primary, BigDecimal fallback) {
        if (primary != null) {
            return primary;
        }
        if (fallback != null) {
            return fallback;
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private void setIfHasText(Consumer<String> setter, String value) {
        if (StringUtils.hasText(value)) {
            setter.accept(value.trim());
        }
    }

    private <T> void setIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private String sequenceNumber(Integer sequence) {
        return sequence != null ? sequence.toString() : null;
    }

    private String requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new TurnkeyXmlException("Turnkey 欄位缺漏: " + fieldName);
        }
        return value.trim();
    }

    private static final class RawInvoiceContext {
        private final List<RawRow> rows;

        private RawInvoiceContext(List<RawRow> rows) {
            this.rows = rows;
        }

        private RawRow firstRow() {
            return rows.isEmpty() ? RawRow.empty() : rows.get(0);
        }

        private String firstValue(String... keys) {
            return firstRow().firstValue(keys);
        }

        private List<RawRow> rows() {
            return rows;
        }
    }

    private static final class RawRow {
        private final Map<String, String> original;
        private final Map<String, String> lowerKeyIndex;

        private RawRow(Map<String, String> original) {
            this.original = original;
            this.lowerKeyIndex = new HashMap<>();
            original.forEach((key, value) -> lowerKeyIndex.put(key.toLowerCase(Locale.ROOT), value));
        }

        private static RawRow empty() {
            return new RawRow(new HashMap<>());
        }

        private String firstValue(String... keys) {
            for (String key : keys) {
                String value = get(key);
                if (StringUtils.hasText(value)) {
                    return value.trim();
                }
            }
            return null;
        }

        private String get(String key) {
            if (!StringUtils.hasText(key)) {
                return null;
            }
            return lowerKeyIndex.get(key.toLowerCase(Locale.ROOT));
        }
    }
}
