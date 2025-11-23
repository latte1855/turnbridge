package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import com.asynctide.turnbridge.service.dto.WebhookDashboardDTOs.TbSummaryDTO;
import com.asynctide.turnbridge.service.dto.WebhookDashboardDTOs.WebhookDlqDTO;
import com.asynctide.turnbridge.service.turnkey.WebhookDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookDashboardService {

    private final InvoiceRepository invoiceRepository;
    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final ObjectMapper objectMapper;

    private final WebhookDispatcher webhookDispatcher;

    public WebhookDashboardService(
        InvoiceRepository invoiceRepository,
        WebhookDeliveryLogRepository webhookDeliveryLogRepository,
        ObjectMapper objectMapper,
        WebhookDispatcher webhookDispatcher
    ) {
        this.invoiceRepository = invoiceRepository;
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.objectMapper = objectMapper;
        this.webhookDispatcher = webhookDispatcher;
    }

    @Transactional(readOnly = true)
    public List<TbSummaryDTO> getTbSummary() {
        return invoiceRepository.findTbErrorSummary().stream()
            .map(row -> {
                TbSummaryDTO dto = new TbSummaryDTO();
                dto.tbCode = (String) row[0];
                dto.tbCategory = (String) row[1];
                dto.count = ((Number) row[2]).longValue();
                dto.sampleInvoice = (String) row[3];
                dto.sampleImportId = row[4] != null ? ((Number) row[4]).longValue() : null;
                dto.lastUpdated = row[5] instanceof Instant ? (Instant) row[5] : null;
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<WebhookDlqDTO> getDlq(Pageable pageable) {
        return webhookDeliveryLogRepository
            .findByStatus(DeliveryResult.RETRY, pageable)
            .map(this::mapToDlqDto);
    }

    private WebhookDlqDTO mapToDlqDto(WebhookDeliveryLog log) {
        WebhookDlqDTO dto = new WebhookDlqDTO();
        dto.id = log.getId();
        dto.deliveryId = log.getDeliveryId();
        dto.event = log.getEvent();
        dto.status = log.getStatus().name();
        dto.attempts = log.getAttempts();
        dto.httpStatus = log.getHttpStatus() != null ? log.getHttpStatus().toString() : null;
        dto.lastError = log.getLastError();
        dto.dlqReason = log.getDlqReason();
        dto.nextAttemptAt = log.getNextAttemptAt();
        dto.webhookEndpointName = log.getWebhookEndpoint() != null ? log.getWebhookEndpoint().getName() : null;
        if (log.getPayload() != null) {
            try {
                JsonNode root = objectMapper.readTree(log.getPayload());
                JsonNode data = root.path("data");
                dto.tbCode = data.path("tb_code").asText(null);
                dto.tbCategory = data.path("tb_category").asText(null);
                dto.invoiceNo = data.path("invoice_no").asText(null);
                dto.importId = data.has("import_id") && !data.get("import_id").isNull() ? data.get("import_id").asLong() : null;
                dto.platformMessage = data.path("source_message").asText(null);
            } catch (Exception ignore) {
                // ignore parsing errors
            }
        }
        return dto;
    }

    @Transactional
    public void resendDelivery(Long id) {
        webhookDeliveryLogRepository.findOneWithEagerRelationships(id).ifPresent(log -> {
            webhookDispatcher.retry(log);
        });
    }
}
