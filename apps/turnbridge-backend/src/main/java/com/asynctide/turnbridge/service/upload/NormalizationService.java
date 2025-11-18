package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 將匯入檔轉換為內部 Invoice/InvoiceItem 的正規化服務。
 */
@Service
@Transactional
public class NormalizationService {

    private static final Logger log = LoggerFactory.getLogger(NormalizationService.class);
    private static final TypeReference<List<Map<String, Object>>> ITEM_LIST_TYPE = new TypeReference<>() {};
    private static final int MAX_DETAIL_LINES = 999;

    private final ImportFileRepository importFileRepository;
    private final ImportFileItemRepository importFileItemRepository;
    private final ImportFileItemErrorRepository importFileItemErrorRepository;
    private final ImportFileLogRepository importFileLogRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ObjectMapper objectMapper;

    public NormalizationService(
        ImportFileRepository importFileRepository,
        ImportFileItemRepository importFileItemRepository,
        ImportFileItemErrorRepository importFileItemErrorRepository,
        ImportFileLogRepository importFileLogRepository,
        InvoiceRepository invoiceRepository,
        InvoiceItemRepository invoiceItemRepository,
        ObjectMapper objectMapper
    ) {
        this.importFileRepository = importFileRepository;
        this.importFileItemRepository = importFileItemRepository;
        this.importFileItemErrorRepository = importFileItemErrorRepository;
        this.importFileLogRepository = importFileLogRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 解析上傳檔案並寫入 ImportFileItem/Invoice 等資料。
     */
    @Transactional(noRollbackFor = NormalizationException.class)
    public void normalize(ImportFile importFile, MultipartFile file, UploadMetadata metadata) {
        NormalizationStats stats = new NormalizationStats();
        Charset charset = resolveCharset(metadata.encoding());
        try {
            ensureLineLimit(file, charset);
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset));
                CSVParser parser = CSVFormat.DEFAULT
                    .builder()
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader)
            ) {
                List<RowBundle> currentGroup = new ArrayList<>();
                String currentKey = null;
                for (CSVRecord record : parser) {
                    RowContext rowContext = RowContext.from(record);
                    ImportFileItem item = createItem(importFile, rowContext);
                    stats.total++;
                    RowBundle bundle = new RowBundle(rowContext, item);
                    String key = groupKey(rowContext);
                    if (currentKey == null) {
                        currentKey = key;
                    }
                    if (!currentKey.equals(key)) {
                        processGroup(currentGroup, importFile, metadata, stats);
                        currentGroup = new ArrayList<>();
                        currentKey = key;
                    }
                    currentGroup.add(bundle);
                }

                if (!currentGroup.isEmpty()) {
                    processGroup(currentGroup, importFile, metadata, stats);
                }

                if (stats.total == 0) {
                    throw new NormalizationException("CSV 無資料", "EMPTY_FILE", "file", "UNKNOWN");
                }

                finalizeImport(importFile, stats);
            }
        } catch (NormalizationException e) {
            markFailed(importFile, stats, e.getMessage());
            throw e;
        } catch (IOException e) {
            markFailed(importFile, stats, "CSV 讀取失敗");
            throw new NormalizationException("CSV 讀取失敗", "IO_ERROR", "file", "UNKNOWN");
        }
    }

    private void ensureLineLimit(MultipartFile file, Charset charset) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset));
            CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader)
        ) {
            int count = 0;
            for (CSVRecord record : parser) {
                count++;
                if (count > MAX_DETAIL_LINES) {
                    throw new NormalizationException(
                        "單檔最多 999 筆明細，第 " + record.getRecordNumber() + " 行起請拆成下一檔",
                        "ITEM_LIMIT_EXCEEDED",
                        "lineIndex",
                        "UNKNOWN"
                    );
                }
            }
        } catch (NormalizationException e) {
            throw e;
        } catch (IOException e) {
            throw new NormalizationException("CSV 讀取失敗", "IO_ERROR", "file", "UNKNOWN");
        }
    }

    private void processGroup(List<RowBundle> bundles, ImportFile importFile, UploadMetadata metadata, NormalizationStats stats) {
        try {
            NormalizedInvoicePayload payload = toInvoicePayload(bundles, importFile, metadata);
            Invoice saved = invoiceRepository.save(payload.invoice());
            payload.items().forEach(it -> it.setInvoice(saved));
            if (!payload.items().isEmpty()) {
                invoiceItemRepository.saveAll(payload.items());
            }
            for (RowBundle bundle : bundles) {
                ImportFileItem item = bundle.item();
                item.setInvoice(saved);
                item.setStatus(ImportItemStatus.NORMALIZED);
                item.setNormalizedFamily(saved.getMessageFamily().name());
                item.setNormalizedJson(payload.normalizedJson());
                importFileItemRepository.save(item);
            }
            stats.success += bundles.size();
        } catch (NormalizationException ex) {
            for (RowBundle bundle : bundles) {
                ImportFileItem item = bundle.item();
                item.setStatus(ImportItemStatus.FAILED);
                item.setErrorCode(ex.getErrorCode());
                item.setErrorMessage(ex.getMessage());
                item.setNormalizedFamily(ex.getNormalizedFamily());
                importFileItemRepository.save(item);
                recordItemError(bundle, ex);
                logRowError(importFile, bundle.row(), ex);
            }
            stats.error += bundles.size();
        }
    }

    private ImportFileItem createItem(ImportFile importFile, RowContext rowContext) {
        ImportFileItem item = new ImportFileItem();
        item.setImportFile(importFile);
        item.setLineIndex(rowContext.lineNumber());
        String rawJson = toJson(rowContext.original());
        item.setRawData(rawJson);
        item.setRawHash(sha256(rawJson));
        item.setSourceFamily(firstNonBlank(rowContext, "legacyType", "LegacyType"));
        item.setStatus(ImportItemStatus.PENDING);
        return importFileItemRepository.save(item);
    }

    private void recordItemError(RowBundle bundle, NormalizationException ex) {
        ImportFileItemError error = new ImportFileItemError();
        error.setImportFileItem(bundle.item());
        error.setColumnIndex(bundle.row().columnIndexOf(ex.getField()));
        error.setFieldName(StringUtils.hasText(ex.getField()) ? ex.getField() : "UNKNOWN");
        error.setErrorCode(ex.getErrorCode());
        error.setMessage(ex.getMessage());
        error.setSeverity("ERROR");
        error.setOccurredAt(Instant.now());
        importFileItemErrorRepository.save(error);
    }

    private void logRowError(ImportFile importFile, RowContext row, NormalizationException ex) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("lineIndex", row.lineNumber());
        detail.put("invoiceNo", firstNonBlank(row, "InvoiceNo", "AllowanceNo"));
        detail.put("field", ex.getField());
        detail.put("errorCode", ex.getErrorCode());
        detail.put("rawData", row.original());
        saveLog(importFile, "NORMALIZE_ROW_ERROR", "ERROR", ex.getMessage(), detail);
    }

    private void finalizeImport(ImportFile importFile, NormalizationStats stats) {
        importFile.setTotalCount(stats.total);
        importFile.setSuccessCount(stats.success);
        importFile.setErrorCount(stats.error);
        importFile.setStatus(stats.error > 0 ? ImportStatus.FAILED : ImportStatus.NORMALIZED);
        importFileRepository.save(importFile);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("total", stats.total);
        detail.put("success", stats.success);
        detail.put("error", stats.error);
        saveLog(importFile, "NORMALIZE_SUMMARY", stats.error > 0 ? "WARN" : "INFO", summaryMessage(stats), detail);
    }

    private void markFailed(ImportFile importFile, NormalizationStats stats, String reason) {
        importFile.setStatus(ImportStatus.FAILED);
        importFile.setTotalCount(stats.total);
        importFile.setSuccessCount(stats.success);
        importFile.setErrorCount(stats.error == 0 ? 1 : stats.error);
        importFileRepository.save(importFile);
        saveLog(importFile, "NORMALIZE_FAILURE", "ERROR", reason, null);
    }

    private void saveLog(ImportFile importFile, String eventCode, String level, String message, Map<String, Object> detail) {
        ImportFileLog logEntity = new ImportFileLog();
        logEntity.setImportFile(importFile);
        logEntity.setEventCode(eventCode);
        logEntity.setLevel(level);
        logEntity.setMessage(message);
        if (detail != null) {
            logEntity.setDetail(toJson(detail));
        }
        logEntity.setOccurredAt(Instant.now());
        importFileLogRepository.save(logEntity);
    }

    private String summaryMessage(NormalizationStats stats) {
        return "正規化完成：success=" + stats.success + ",error=" + stats.error;
    }

    private Charset resolveCharset(String encoding) {
        if (!StringUtils.hasText(encoding)) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(encoding.trim());
    }

    private NormalizedInvoicePayload toInvoicePayload(
        List<RowBundle> bundles,
        ImportFile importFile,
        UploadMetadata metadata
    ) {
        RowContext row = bundles.get(0).row();
        String type = require(row, "type", "Type 欄位必填", "Type");
        MessageFamily family = resolveFamily(type);
        String invoiceNo = firstNonBlank(row, "InvoiceNo", "AllowanceNo");
        if (!StringUtils.hasText(invoiceNo)) {
            throw new NormalizationException("缺少發票/折讓號碼", "INVOICE_NO_MISSING", "InvoiceNo", family.name());
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(invoiceNo.trim());
        invoice.setMessageFamily(family);
        invoice.setImportFile(importFile);
        invoice.setTenant(importFile.getTenant());
        invoice.setInvoiceStatus(InvoiceStatus.NORMALIZED);
        invoice.setLegacyType(resolveLegacyType(row, importFile, metadata));
        String sellerId = firstNonBlank(row, "SellerId");
        if (!StringUtils.hasText(sellerId)) {
            sellerId = metadata.sellerId();
        }
        invoice.setSellerId(sellerId);
        invoice.setSellerName(firstNonBlank(row, "SellerName"));
        invoice.setBuyerId(firstNonBlank(row, "BuyerId"));
        invoice.setBuyerName(firstNonBlank(row, "BuyerName"));
        invoice.setSalesAmount(parseDecimal(row, family, "SalesAmount"));
        invoice.setTaxAmount(parseDecimal(row, family, "Tax", "TaxAmount"));
        invoice.setTotalAmount(parseDecimal(row, family, "Total", "TotalAmount"));
        invoice.setTaxType(firstNonBlank(row, "TaxType"));
        invoice.setIssuedAt(parseIssueInstant(row, family));

        verifyAmount(invoice.getSalesAmount(), invoice.getTaxAmount(), invoice.getTotalAmount(), family);

        List<InvoiceItem> items = buildItems(bundles, family);
        String normalizedSnapshot = toJson(buildNormalizedSnapshot(invoice, items));
        invoice.setNormalizedJson(normalizedSnapshot);
        return new NormalizedInvoicePayload(invoice, items, normalizedSnapshot);
    }

    private Map<String, Object> buildNormalizedSnapshot(Invoice invoice, List<InvoiceItem> items) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("invoiceNo", invoice.getInvoiceNo());
        snapshot.put("messageFamily", invoice.getMessageFamily());
        snapshot.put("buyerId", invoice.getBuyerId());
        snapshot.put("sellerId", invoice.getSellerId());
        snapshot.put("totalAmount", invoice.getTotalAmount());
        snapshot.put("taxAmount", invoice.getTaxAmount());
        snapshot.put("salesAmount", invoice.getSalesAmount());
        snapshot.put("itemCount", items.size());
        return snapshot;
    }

    private MessageFamily resolveFamily(String rawType) {
        try {
            return MessageFamily.valueOf(rawType.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new NormalizationException("不支援的訊息別: " + rawType, "ILLEGAL_MESSAGE_TYPE", "Type", rawType);
        }
    }

    private void verifyAmount(BigDecimal sales, BigDecimal tax, BigDecimal total, MessageFamily family) {
        if (sales == null || tax == null || total == null) {
            return;
        }
        if (sales.add(tax).compareTo(total) != 0) {
            throw new NormalizationException("金額與稅額不平衡", "AMOUNT_MISMATCH", "Total", family.name());
        }
    }

    private List<InvoiceItem> buildItems(List<RowBundle> bundles, MessageFamily family) {
        List<InvoiceItem> items = new ArrayList<>();
        int sequence = 1;
        for (RowBundle bundle : bundles) {
            String json = firstNonBlank(bundle.row(), "Items", "Details");
            if (StringUtils.hasText(json)) {
                items.addAll(readItemsFromJson(json, sequence));
                sequence = items.size() + 1;
            } else {
                items.add(toItemFromRow(bundle.row(), family, sequence++));
            }
        }
        return items;
    }

    private List<InvoiceItem> readItemsFromJson(String json, int startSequence) {
        List<InvoiceItem> items = new ArrayList<>();
        try {
            List<Map<String, Object>> parsed = objectMapper.readValue(json, ITEM_LIST_TYPE);
            int seq = startSequence;
            for (Map<String, Object> map : parsed) {
                InvoiceItem item = new InvoiceItem();
                item.setSequence(seq++);
                String description = readText(map, "description", "Description", "name", "Name", "itemName", "ItemName");
                if (!StringUtils.hasText(description)) {
                    description = "項目" + item.getSequence();
                }
                item.setDescription(description);
                item.setQuantity(readBigDecimal(map.get("qty"), map.get("quantity")));
                item.setUnitPrice(readBigDecimal(map.get("unitPrice"), map.get("price")));
                item.setAmount(readBigDecimal(map.get("amount"), null));
                items.add(item);
            }
        } catch (JsonProcessingException e) {
            throw new NormalizationException("Items JSON 無法解析", "ITEMS_JSON_INVALID", "Items", "UNKNOWN");
        }
        return items;
    }

    private InvoiceItem toItemFromRow(RowContext row, MessageFamily family, int sequence) {
        InvoiceItem item = new InvoiceItem();
        item.setSequence(sequence);
        item.setDescription(firstNonBlank(row, "ItemDescription", "Description", "ItemName", "ProductName", "Remark"));
        if (!StringUtils.hasText(item.getDescription())) {
            item.setDescription("項目" + sequence);
        }
        item.setQuantity(parseDecimal(row, family, "ItemQuantity", "Quantity"));
        item.setUnitPrice(parseDecimal(row, family, "UnitPrice"));
        item.setAmount(parseDecimal(row, family, "ItemAmount", "Amount"));
        return item;
    }

    private String readText(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                Object value = map.get(key);
                if (value != null) {
                    String text = String.valueOf(value).trim();
                    if (StringUtils.hasText(text)) {
                        return text;
                    }
                }
            }
        }
        return null;
    }

    private BigDecimal readBigDecimal(Object primary, Object secondary) {
        Object target = primary != null ? primary : secondary;
        if (target == null) {
            return null;
        }
        if (target instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        return new BigDecimal(String.valueOf(target).trim());
    }

    private String resolveLegacyType(RowContext row, ImportFile importFile, UploadMetadata metadata) {
        String value = firstNonBlank(row, "legacyType", "LegacyType");
        if (!StringUtils.hasText(value)) {
            value = importFile.getLegacyType();
        }
        if (!StringUtils.hasText(value)) {
            value = metadata.legacyType();
        }
        return value;
    }

    private BigDecimal parseDecimal(RowContext row, MessageFamily family, String... fields) {
        String value = firstNonBlank(row, fields);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new NormalizationException("數值格式錯誤: " + value, "NUMBER_FORMAT", fields[0], family.name());
        }
    }

    private Instant parseIssueInstant(RowContext row, MessageFamily family) {
        String text = firstNonBlank(row, "DateTime", "InvoiceDateTime", "IssueDateTime", "InvoiceDate");
        if (!StringUtils.hasText(text)) {
            return null;
        }
        String trimmed = text.trim();
        List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        for (DateTimeFormatter formatter : formatters) {
            try {
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    LocalDateTime ldt = LocalDateTime.parse(trimmed, formatter);
                    return ldt.toInstant(ZoneOffset.UTC);
                }
                return OffsetDateTime.parse(trimmed, formatter).toInstant();
            } catch (DateTimeParseException ignored) {}
        }
        try {
            LocalDate date = LocalDate.parse(trimmed);
            return date.atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            throw new NormalizationException("日期格式錯誤: " + trimmed, "DATETIME_INVALID", "DateTime", family.name());
        }
    }

    private String require(RowContext row, String key, String message, String field) {
        String value = row.get(key);
        if (!StringUtils.hasText(value)) {
            throw new NormalizationException(message, "FIELD_REQUIRED", field, "UNKNOWN");
        }
        return value;
    }

    private String firstNonBlank(RowContext row, String... keys) {
        for (String key : keys) {
            String value = row.get(key);
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new NormalizationException("無法轉成 JSON", "JSON_SERIALIZE", "json", "UNKNOWN");
        }
    }

    private String groupKey(RowContext row) {
        String invoiceNo = firstNonBlank(row, "InvoiceNo", "AllowanceNo");
        if (!StringUtils.hasText(invoiceNo)) {
            return "LINE-" + row.lineNumber();
        }
        String type = firstNonBlank(row, "type", "Type");
        if (!StringUtils.hasText(type)) {
            type = "UNKNOWN";
        }
        return type.trim().toUpperCase(Locale.ROOT) + "::" + invoiceNo.trim().toUpperCase(Locale.ROOT);
    }

    private String sha256(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormatHolder.HEX.formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static class RowContext {

        private final Map<String, String> original;
        private final Map<String, String> lowerCase;
        private final List<String> headers;
        private final int lineNumber;

        private RowContext(Map<String, String> original, Map<String, String> lowerCase, List<String> headers, int lineNumber) {
            this.original = original;
            this.lowerCase = lowerCase;
            this.headers = headers;
            this.lineNumber = lineNumber;
        }

        public static RowContext from(CSVRecord record) {
            Map<String, String> source = new LinkedHashMap<>(record.toMap());
            Map<String, String> lower = new HashMap<>();
            source.forEach((k, v) -> lower.put(k.toLowerCase(Locale.ROOT), v));
            return new RowContext(source, lower, new ArrayList<>(source.keySet()), (int) record.getRecordNumber());
        }

        public String get(String key) {
            if (key == null) {
                return null;
            }
            return lowerCase.get(key.toLowerCase(Locale.ROOT));
        }

        public Map<String, String> original() {
            return original;
        }

        public int columnIndexOf(String key) {
            if (!StringUtils.hasText(key)) {
                return 1;
            }
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                if (header != null && header.equalsIgnoreCase(key)) {
                    return i + 1;
                }
            }
            return 1;
        }

        public int lineNumber() {
            return lineNumber;
        }
    }

    private static record RowBundle(RowContext row, ImportFileItem item) {}

    private static record NormalizedInvoicePayload(Invoice invoice, List<InvoiceItem> items, String normalizedJson) {}

    private static class NormalizationStats {
        int total;
        int success;
        int error;
    }

    private static final class HexFormatHolder {
        private static final java.util.HexFormat HEX = java.util.HexFormat.of();

        private HexFormatHolder() {}
    }
}
