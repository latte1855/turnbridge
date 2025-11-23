package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.turnkey.TurnkeyProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 將內部發票匯出為 Turnkey XML 並寫入 INBOX 目錄。
 */
@Service
@Transactional
public class TurnkeyXmlExportService {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyXmlExportService.class);
    private static final ZoneId ZONE_TAIPEI = ZoneId.of("Asia/Taipei");
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZONE_TAIPEI);
    private static final List<String> EXPORT_EVENT_CODES = List.of(
        "XML_GENERATED",
        "XML_DELIVERED_TO_TURNKEY",
        "XML_DELIVERY_FAILURE",
        "XML_GENERATE_FAILURE"
    );

    private final InvoiceRepository invoiceRepository;
    private final ImportFileLogRepository importFileLogRepository;
    private final TurnkeyXmlBuilder turnkeyXmlBuilder;
    private final TurnkeyProperties turnkeyProperties;
    private final ObjectMapper objectMapper;

    public TurnkeyXmlExportService(
        InvoiceRepository invoiceRepository,
        ImportFileLogRepository importFileLogRepository,
        TurnkeyXmlBuilder turnkeyXmlBuilder,
        TurnkeyProperties turnkeyProperties,
        ObjectMapper objectMapper
    ) {
        this.invoiceRepository = invoiceRepository;
        this.importFileLogRepository = importFileLogRepository;
        this.turnkeyXmlBuilder = turnkeyXmlBuilder;
        this.turnkeyProperties = turnkeyProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * 針對指定批次大小匯出待處理的發票。
     *
     * @param batchSize 單次處理筆數
     * @return 實際匯出的發票數
     */
    public int exportPendingInvoices(int batchSize) {
        int size = batchSize > 0 ? batchSize : 100;
        List<Invoice> candidates = invoiceRepository
            .findByInvoiceStatus(InvoiceStatus.NORMALIZED, PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "id")))
            .getContent();
        if (candidates.isEmpty()) {
            return 0;
        }
        int processed = 0;
        for (Invoice invoice : candidates) {
            try {
                invoice.setInvoiceStatus(InvoiceStatus.PENDING_XML);
                TurnkeyXmlBuildResult result = turnkeyXmlBuilder.build(invoice);
                Path file = writeXmlFile(invoice, result.xml());
                invoice.setInvoiceStatus(InvoiceStatus.IN_PICKUP);
                logEvent(invoice, "XML_GENERATED", "INFO", "XML 產生完成", Map.of("file", file.toString()));
                deliverToTurnkey(invoice, file);
                processed++;
            } catch (Exception ex) {
                invoice.setInvoiceStatus(InvoiceStatus.ERROR);
                logEvent(invoice, "XML_GENERATE_FAILURE", "ERROR", ex.getMessage(), Map.of("reason", ex.getClass().getSimpleName()));
                LOG.error("Invoice {} 匯出 XML 失敗：{}", invoice.getInvoiceNo(), ex.getMessage(), ex);
            }
        }
        return processed;
    }

    private Path writeXmlFile(Invoice invoice, String xmlContent) throws IOException {
        Path targetDir = resolveTargetDirectory(invoice.getTenant());
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(generateFilename(invoice));
        if (Files.exists(targetFile)) {
            String withSuffix = targetFile.getFileName().toString().replace(".xml", "-" + System.currentTimeMillis() + ".xml");
            targetFile = targetDir.resolve(withSuffix);
        }
        Files.writeString(targetFile, xmlContent, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return targetFile.toAbsolutePath();
    }

    private Path resolveTargetDirectory(Tenant tenant) {
        Path base = Path.of(turnkeyProperties.getInboxDir());
        if (turnkeyProperties.isTenantSubDirectory()) {
            String tenantCode = tenant != null && StringUtils.hasText(tenant.getCode()) ? tenant.getCode() : "default";
            base = base.resolve(tenantCode);
        }
        return base;
    }

    private String generateFilename(Invoice invoice) {
        Instant issuedAt = invoice.getIssuedAt() != null ? invoice.getIssuedAt() : Instant.now();
        String prefix = StringUtils.hasText(turnkeyProperties.getFilenamePrefix()) ? turnkeyProperties.getFilenamePrefix() : "FGPAYLOAD";
        String invoiceId = invoice.getId() != null ? String.format("%06d", invoice.getId()) : "000000";
        return prefix + "_" + invoice.getMessageFamily() + "_" + FILE_DATE.format(issuedAt) + "_" + invoiceId + ".xml";
    }

    private void logEvent(Invoice invoice, String eventCode, String level, String message, Map<String, Object> detail) {
        ImportFile importFile = invoice.getImportFile();
        if (importFile == null) {
            return;
        }
        ImportFileLog log = new ImportFileLog();
        log.setImportFile(importFile);
        log.setEventCode(eventCode);
        log.setLevel(level);
        log.setMessage(message);
        log.setDetail(writeJson(detail));
        log.setOccurredAt(Instant.now());
        importFileLogRepository.save(log);
    }

    private String writeJson(Map<String, Object> detail) {
        if (detail == null || detail.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(new LinkedHashMap<>(detail));
        } catch (JsonProcessingException e) {
            LOG.warn("無法序列化 XML 匯出明細", e);
            return null;
        }
    }

    private void deliverToTurnkey(Invoice invoice, Path exportedFile) {
        String base = turnkeyProperties.getB2sStorageSrcBase();
        if (!StringUtils.hasText(base)) {
            return;
        }
        Path destDir = Path.of(base, invoice.getMessageFamily().name(), "SRC");
        try {
            Files.createDirectories(destDir);
            Path destFile = destDir.resolve(exportedFile.getFileName());
            Files.copy(exportedFile, destFile, StandardCopyOption.REPLACE_EXISTING);
            logEvent(
                invoice,
                "XML_DELIVERED_TO_TURNKEY",
                "INFO",
                "已搬移至 Turnkey B2SSTORAGE",
                Map.of("localFile", exportedFile.toString(), "turnkeyFile", destFile.toString())
            );
        } catch (IOException ex) {
            logEvent(
                invoice,
                "XML_DELIVERY_FAILURE",
                "ERROR",
                "搬移至 Turnkey 目錄失敗",
                Map.of("reason", ex.getMessage(), "targetDir", destDir.toString())
            );
            LOG.error("搬移 XML 到 Turnkey 目錄失敗：invoice={}, dir={}", invoice.getInvoiceNo(), destDir, ex);
        }
    }

    public List<ImportFileLog> findRecentExportLogs(Pageable pageable) {
        return importFileLogRepository.findByEventCodeInOrderByOccurredAtDesc(EXPORT_EVENT_CODES, pageable).getContent();
    }
}
