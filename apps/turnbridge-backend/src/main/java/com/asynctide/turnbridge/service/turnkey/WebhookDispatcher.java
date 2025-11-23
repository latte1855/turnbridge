package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import com.asynctide.turnbridge.domain.enumeration.WebhookStatus;
import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import com.asynctide.turnbridge.repository.WebhookEndpointRepository;
import com.asynctide.turnbridge.service.turnkey.TurnkeyErrorMapper.MappedError;
import com.asynctide.turnbridge.service.turnkey.TurnkeyProcessResultService.ProcessResultPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Webhook 投遞器：處理一般事件與 DLQ 通知。
 */
@Service
public class WebhookDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDispatcher.class);
    private static final String EVENT_INVOICE_STATUS = "invoice.status.updated";
    private static final String EVENT_DELIVERY_FAILED = "webhook.delivery.failed";
    private static final int[] RETRY_DELAYS_MINUTES = { 0, 1, 5, 15 };

    private final WebhookEndpointRepository webhookEndpointRepository;
    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public WebhookDispatcher(
        WebhookEndpointRepository webhookEndpointRepository,
        WebhookDeliveryLogRepository webhookDeliveryLogRepository,
        ObjectMapper objectMapper,
        RestTemplateBuilder restTemplateBuilder
    ) {
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
            .requestFactory(() -> {
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
                factory.setReadTimeout((int) Duration.ofSeconds(10).toMillis());
                return factory;
            })
            .build();
    }

    public void dispatchInvoiceStatusUpdated(Invoice invoice, ProcessResultPayload payload, Long messageId, MappedError mapped) {
        if (invoice == null || invoice.getTenant() == null) {
            return;
        }
        List<WebhookEndpoint> endpoints = webhookEndpointRepository.findByTenantIdAndStatus(invoice.getTenant().getId(), WebhookStatus.ACTIVE);
        if (endpoints.isEmpty()) {
            return;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("invoice_no", invoice.getInvoiceNo());
        data.put("status", invoice.getInvoiceStatus().name());
        data.put("normalized_message_type", invoice.getMessageFamily() != null ? invoice.getMessageFamily().name() : null);
        if (invoice.getImportFile() != null) {
            data.put("import_id", invoice.getImportFile().getId());
        }
        data.put("mof_code", payload.errorCode());
        data.put("tb_code", mapped.tbCode());
        data.put("tb_category", mapped.tbCategory());
        data.put("can_auto_retry", mapped.canAutoRetry());
        data.put("recommended_action", mapped.recommendedAction());
        data.put("source_layer", "PLATFORM");
        data.put("source_code", mapped.sourceCode());
        data.put("source_message", payload.errorMessage() != null ? payload.errorMessage() : payload.resultMessage());
        data.put("result_code", payload.resultCode());
        data.put("message", payload.errorMessage());
        data.put("turnkey_message_id", messageId);
        data.put("legacy_type", invoice.getLegacyType());
        sendEventToEndpoints(endpoints, EVENT_INVOICE_STATUS, data, null);
    }

    private void sendEventToEndpoints(
        List<WebhookEndpoint> endpoints,
        String event,
        Map<String, Object> data,
        WebhookEndpoint excludedEndpoint
    ) {
        for (WebhookEndpoint endpoint : endpoints) {
            if (!isSubscribed(endpoint, event)) {
                continue;
            }
            if (excludedEndpoint != null && endpoint.getId() != null && endpoint.getId().equals(excludedEndpoint.getId())) {
                continue;
            }
            sendEvent(endpoint, event, data);
        }
    }

    private boolean isSubscribed(WebhookEndpoint endpoint, String event) {
        return Arrays
            .stream(endpoint.getEvents().split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .anyMatch(e -> e.equalsIgnoreCase(event));
    }

    private void sendEvent(WebhookEndpoint endpoint, String event, Map<String, Object> data) {
        String deliveryId = UUID.randomUUID().toString();
        WebhookDeliveryLog log = initializeLog(endpoint, event, data, deliveryId);
        DeliveryAttempt attempt = sendPayload(endpoint, event, deliveryId, log.getPayload());
        boolean movedToDlq = handleAttemptResult(log, attempt);
        webhookDeliveryLogRepository.save(log);
        if (movedToDlq && !EVENT_DELIVERY_FAILED.equals(event)) {
            notifyDeliveryFailed(log);
        }
    }

    private WebhookDeliveryLog initializeLog(WebhookEndpoint endpoint, String event, Map<String, Object> eventData, String deliveryId) {
        Instant now = Instant.now();
        String tenantCode = endpoint.getTenant() != null ? endpoint.getTenant().getCode() : "UNKNOWN";
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("delivery_id", deliveryId);
        body.put("event", event);
        body.put("timestamp", now.toString());
        body.put("tenant_id", tenantCode);
        body.put("data", new LinkedHashMap<>(eventData));
        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new IllegalStateException("Webhook payload 序列化失敗", e);
        }

        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.setWebhookEndpoint(endpoint);
        log.setDeliveryId(deliveryId);
        log.setEvent(event);
        log.setPayload(json);
        log.setAttempts(0);
        log.setDeliveredAt(null);
        return log;
    }

    private DeliveryAttempt sendPayload(WebhookEndpoint endpoint, String event, String deliveryId, String payload) {
        Instant now = Instant.now();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Turnbridge-Event", event);
        headers.add("X-Turnbridge-Delivery-Id", deliveryId);
        headers.add("X-Turnbridge-Timestamp", String.valueOf(now.getEpochSecond()));
        if (StringUtils.hasText(endpoint.getSecret())) {
            headers.add("X-Turnbridge-Signature", "sha256=" + computeSignature(endpoint.getSecret(), payload));
        }

        DeliveryResult result = DeliveryResult.SUCCESS;
        Integer status = null;
        String lastError = null;
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URI.create(endpoint.getTargetUrl()), new HttpEntity<>(payload, headers), String.class);
            status = response.getStatusCode().value();
            if (!response.getStatusCode().is2xxSuccessful()) {
                result = DeliveryResult.RETRY;
                lastError = "HTTP " + status;
            }
        } catch (HttpStatusCodeException ex) {
            result = DeliveryResult.RETRY;
            status = ex.getStatusCode().value();
            lastError = ex.getResponseBodyAsString();
            LOG.warn("Webhook 投遞失敗：endpoint={}, status={}", endpoint.getId(), status);
        } catch (Exception ex) {
            result = DeliveryResult.RETRY;
            lastError = ex.getMessage();
            LOG.warn("Webhook 投遞例外：endpoint={}", endpoint.getId(), ex);
        }
        return new DeliveryAttempt(result, status, lastError, now);
    }

    private boolean handleAttemptResult(WebhookDeliveryLog log, DeliveryAttempt attempt) {
        log.setHttpStatus(attempt.httpStatus());
        log.setLastError(attempt.lastError());
        int attempts = log.getAttempts() == null ? 0 : log.getAttempts();
        attempts++;
        log.setAttempts(attempts);
        log.setLockedAt(null);
        if (attempt.result() == DeliveryResult.SUCCESS) {
            log.setStatus(DeliveryResult.SUCCESS);
            log.setDeliveredAt(attempt.attemptTime());
            log.setNextAttemptAt(null);
            return false;
        }
        if (attempts >= RETRY_DELAYS_MINUTES.length) {
            log.setStatus(DeliveryResult.FAILED);
            log.setDlqReason(log.getLastError());
            log.setDeliveredAt(attempt.attemptTime());
            log.setNextAttemptAt(null);
            return true;
        }
        int nextAttemptIndex = attempts;
        long delayMinutes = RETRY_DELAYS_MINUTES[nextAttemptIndex];
        log.setStatus(DeliveryResult.RETRY);
        log.setNextAttemptAt(attempt.attemptTime().plus(delayMinutes, ChronoUnit.MINUTES));
        return false;
    }

    public void retry(WebhookDeliveryLog log) {
        DeliveryAttempt attempt = sendPayload(log.getWebhookEndpoint(), log.getEvent(), log.getDeliveryId(), log.getPayload());
        boolean movedToDlq = handleAttemptResult(log, attempt);
        webhookDeliveryLogRepository.save(log);
        if (movedToDlq) {
            notifyDeliveryFailed(log);
        }
    }

    private void notifyDeliveryFailed(WebhookDeliveryLog failedLog) {
        if (failedLog.getWebhookEndpoint() == null || failedLog.getWebhookEndpoint().getTenant() == null) {
            return;
        }
        if (EVENT_DELIVERY_FAILED.equals(failedLog.getEvent())) {
            return;
        }
        Long tenantId = failedLog.getWebhookEndpoint().getTenant().getId();
        if (tenantId == null) {
            return;
        }
        List<WebhookEndpoint> endpoints = webhookEndpointRepository.findByTenantIdAndStatus(tenantId, WebhookStatus.ACTIVE);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("failed_delivery_id", failedLog.getDeliveryId());
        data.put("webhook_endpoint_id", failedLog.getWebhookEndpoint().getId());
        data.put("event_failed", failedLog.getEvent());
        data.put("attempts", failedLog.getAttempts());
        data.put("last_error", failedLog.getLastError());
        data.put("dlq_reason", failedLog.getDlqReason());
        data.put("status", failedLog.getStatus().name());
        sendEventToEndpoints(endpoints, EVENT_DELIVERY_FAILED, data, failedLog.getWebhookEndpoint());
    }

    private String computeSignature(String secret, String json) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] result = mac.doFinal(json.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            LOG.warn("Webhook 簽章失敗，改用空簽章", e);
            return "";
        }
    }

    private record DeliveryAttempt(DeliveryResult result, Integer httpStatus, String lastError, Instant attemptTime) {}
}
