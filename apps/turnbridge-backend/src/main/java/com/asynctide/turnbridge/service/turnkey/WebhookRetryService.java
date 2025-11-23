package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Webhook 重送排程，每分鐘掃描待重送的投遞紀錄。
 */
@Component
public class WebhookRetryService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookRetryService.class);
    private static final Duration LOCK_TIMEOUT = Duration.ofMinutes(1);

    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final WebhookDispatcher webhookDispatcher;

    public WebhookRetryService(WebhookDeliveryLogRepository webhookDeliveryLogRepository, WebhookDispatcher webhookDispatcher) {
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    @Scheduled(cron = "${webhook.retry-cron:0 */1 * * * *}")
    public void retryPendingDeliveries() {
        Instant now = Instant.now();
        List<WebhookDeliveryLog> pending = webhookDeliveryLogRepository.findByStatusAndNextAttemptAtLessThanEqual(
            DeliveryResult.RETRY,
            now
        );
        for (WebhookDeliveryLog log : pending) {
            if (isLocked(log, now)) {
                continue;
            }
            lock(log, now);
            try {
                webhookDispatcher.retry(log);
            } catch (Exception ex) {
                LOG.error("Webhook 重送發生例外：deliveryId={}", log.getDeliveryId(), ex);
                log.setLockedAt(null);
                if (log.getAttempts() == null) {
                    log.setAttempts(0);
                }
                log.setAttempts(log.getAttempts() + 1);
                log.setLastError(ex.getMessage());
                webhookDeliveryLogRepository.save(log);
            }
        }
    }

    private boolean isLocked(WebhookDeliveryLog log, Instant now) {
        return log.getLockedAt() != null && log.getLockedAt().plus(LOCK_TIMEOUT).isAfter(now);
    }

    private void lock(WebhookDeliveryLog log, Instant now) {
        log.setLockedAt(now);
        webhookDeliveryLogRepository.save(log);
    }
}
