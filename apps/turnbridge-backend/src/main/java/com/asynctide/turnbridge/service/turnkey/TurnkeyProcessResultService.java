package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.TurnkeyMessage;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.TurnkeyMessageRepository;
import com.asynctide.turnbridge.service.turnkey.TurnkeyErrorMapper.MappedError;
import com.asynctide.turnbridge.turnkey.TurnkeyProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

/**
 * 解析 Turnkey ProcessResult XML，更新發票狀態與錯誤碼。
 */
@Service
@Transactional
public class TurnkeyProcessResultService {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyProcessResultService.class);
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    static {
        try {
            DOCUMENT_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DOCUMENT_BUILDER_FACTORY.setFeature("http://xml.org/sax/features/external-general-entities", false);
            DOCUMENT_BUILDER_FACTORY.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception ignored) {}
    }

    private final TurnkeyProperties turnkeyProperties;
    private final InvoiceRepository invoiceRepository;
    private final TurnkeyMessageRepository turnkeyMessageRepository;
    private final ImportFileLogRepository importFileLogRepository;
    private final TurnkeyErrorMapper errorMapper;
    private final WebhookDispatcher webhookDispatcher;
    private final ObjectMapper objectMapper;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final AtomicLong lastSuccessGauge;

    public TurnkeyProcessResultService(
        TurnkeyProperties turnkeyProperties,
        InvoiceRepository invoiceRepository,
        TurnkeyMessageRepository turnkeyMessageRepository,
        ImportFileLogRepository importFileLogRepository,
        TurnkeyErrorMapper errorMapper,
        WebhookDispatcher webhookDispatcher,
        ObjectMapper objectMapper,
        MeterRegistry meterRegistry
    ) {
        this.turnkeyProperties = turnkeyProperties;
        this.invoiceRepository = invoiceRepository;
        this.turnkeyMessageRepository = turnkeyMessageRepository;
        this.importFileLogRepository = importFileLogRepository;
        this.errorMapper = errorMapper;
        this.webhookDispatcher = webhookDispatcher;
        this.objectMapper = objectMapper;
        this.successCounter = Counter
            .builder("turnkey_process_result_total")
            .description("Number of ProcessResult payloads processed successfully")
            .tag("result", "SUCCESS")
            .register(meterRegistry);
        this.failureCounter = Counter
            .builder("turnkey_process_result_total")
            .description("Number of ProcessResult payloads processed unsuccessfully")
            .tag("result", "ERROR")
            .register(meterRegistry);
        this.lastSuccessGauge = meterRegistry.gauge("turnkey_process_result_last_success_epoch", new AtomicLong(0));
    }

    @Scheduled(cron = "${turnbridge.turnkey.process-result-cron:15 */5 * * * *}")
    public void pollProcessResults() {
        String base = turnkeyProperties.getProcessResultBase();
        if (!StringUtils.hasText(base)) {
            return;
        }
        for (MessageFamily family : MessageFamily.values()) {
            Path familyDir = Path.of(base, family.name(), "ProcessResult");
            if (!Files.exists(familyDir)) {
                continue;
            }
            scanFamilyDirectory(family, familyDir);
        }
    }

    private void scanFamilyDirectory(MessageFamily family, Path familyDir) {
        try {
            Files.walkFileTree(familyDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!Files.isRegularFile(file)) {
                        return FileVisitResult.CONTINUE;
                    }
                    String path = file.toAbsolutePath().toString();
                    if (turnkeyMessageRepository.existsByPayloadPath(path)) {
                        return FileVisitResult.CONTINUE;
                    }
                    processFile(family, file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            LOG.error("掃描 ProcessResult 目錄失敗：dir={}", familyDir, ex);
        }
    }

    private void processFile(MessageFamily family, Path file) {
        try {
            ProcessResultPayload payload = parseProcessResult(file);
            if (payload == null) {
                return;
            }
            TurnkeyMessage message = new TurnkeyMessage();
            message.setMessageId(file.getFileName().toString());
            message.setMessageFamily(family);
            message.setType(payload.resultCode);
            message.setCode(payload.errorCode);
            message.setMessage(payload.errorMessage != null ? payload.errorMessage : payload.resultMessage);
            message.setPayloadPath(file.toAbsolutePath().toString());
            message.setReceivedAt(Instant.now());

            Optional<Invoice> invoiceOpt = invoiceRepository.findByInvoiceNo(payload.invoiceNumber());
            invoiceOpt.ifPresent(message::setInvoice);
            TurnkeyMessage saved = turnkeyMessageRepository.save(message);

            MappedError mappedError = errorMapper.map(payload.resultCode(), payload.errorCode());
            invoiceOpt.ifPresent(invoice -> applyResultToInvoice(invoice, payload, mappedError));
            invoiceOpt.ifPresent(invoice -> webhookDispatcher.dispatchInvoiceStatusUpdated(invoice, payload, saved.getId(), mappedError));
            logImportFile(invoiceOpt.orElse(null), payload, mappedError, file, saved.getId());
            recordMetrics("0".equals(payload.resultCode()));

            Path processedPath = file.resolveSibling(file.getFileName() + ".done");
            Files.move(file, processedPath, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("ProcessResult 已處理：{}", processedPath);
        } catch (Exception ex) {
            LOG.error("處理 ProcessResult 失敗：file={}", file, ex);
            recordMetrics(false);
        }
    }

    private void applyResultToInvoice(Invoice invoice, ProcessResultPayload payload, MappedError mapped) {
        boolean success = "0".equals(payload.resultCode());
        invoice.setInvoiceStatus(success ? InvoiceStatus.ACKED : InvoiceStatus.ERROR);
        invoice.setTbCode(mapped.tbCode());
        invoice.setTbCategory(mapped.tbCategory());
        invoice.setTbCanAutoRetry(mapped.canAutoRetry());
        invoice.setTbRecommendedAction(mapped.recommendedAction());
        invoice.setTbSourceCode(mapped.sourceCode());
        invoice.setTbSourceMessage(
            StringUtils.hasText(payload.errorMessage()) ? payload.errorMessage() : payload.resultMessage()
        );
        invoice.setTbResultCode(payload.resultCode());
        invoiceRepository.save(invoice);
    }

    private void logImportFile(Invoice invoice, ProcessResultPayload payload, MappedError mapped, Path file, Long messageId) {
        if (invoice == null || invoice.getImportFile() == null) {
            return;
        }
        ImportFile importFile = invoice.getImportFile();
        ImportFileLog log = new ImportFileLog();
        log.setImportFile(importFile);
        log.setEventCode("PROCESS_RESULT");
        log.setLevel("INFO");
        log.setMessage("ProcessResult: code=" + payload.resultCode() + ", error=" + payload.errorCode());
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("file", file.toAbsolutePath().toString());
        detail.put("invoiceNo", payload.invoiceNumber());
        detail.put("errorCode", payload.errorCode());
        detail.put("errorMessage", payload.errorMessage());
        detail.put("tbCode", mapped.tbCode());
        detail.put("turnkeyMessageId", messageId);
        log.setDetail(toJson(detail));
        log.setOccurredAt(Instant.now());
        importFileLogRepository.save(log);
    }

    private ProcessResultPayload parseProcessResult(Path file) {
        try {
            Document document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(file.toFile());
            document.getDocumentElement().normalize();
            String resultCode = textContent(document, "ResultCode");
            String resultMessage = textContent(document, "ResultMessage");
            String invoiceNumber = textContent(document, "InvoiceNumber");
            String errorCode = textContent(document, "ErrorCode");
            String errorMessage = textContent(document, "ErrorMessage");
            return new ProcessResultPayload(resultCode, resultMessage, invoiceNumber, errorCode, errorMessage);
        } catch (Exception ex) {
            LOG.error("解析 ProcessResult XML 失敗：file={}", file, ex);
            return null;
        }
    }

    private String textContent(Document document, String tag) {
        if (document.getElementsByTagName(tag).getLength() == 0) {
            return null;
        }
        return document.getElementsByTagName(tag).item(0).getTextContent();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"json\"}";
        }
    }

    private void recordMetrics(boolean success) {
        if (success) {
            successCounter.increment();
            if (lastSuccessGauge != null) {
                lastSuccessGauge.set(Instant.now().getEpochSecond());
            }
        } else {
            failureCounter.increment();
        }
    }

    public static record ProcessResultPayload(
        String resultCode,
        String resultMessage,
        String invoiceNumber,
        String errorCode,
        String errorMessage
    ) {}
}
