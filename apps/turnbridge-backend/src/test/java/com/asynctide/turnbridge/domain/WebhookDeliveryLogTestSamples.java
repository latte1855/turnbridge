package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WebhookDeliveryLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static WebhookDeliveryLog getWebhookDeliveryLogSample1() {
        return new WebhookDeliveryLog().id(1L).deliveryId("deliveryId1").event("event1").httpStatus(1).attempts(1).lastError("lastError1");
    }

    public static WebhookDeliveryLog getWebhookDeliveryLogSample2() {
        return new WebhookDeliveryLog().id(2L).deliveryId("deliveryId2").event("event2").httpStatus(2).attempts(2).lastError("lastError2");
    }

    public static WebhookDeliveryLog getWebhookDeliveryLogRandomSampleGenerator() {
        return new WebhookDeliveryLog()
            .id(longCount.incrementAndGet())
            .deliveryId(UUID.randomUUID().toString())
            .event(UUID.randomUUID().toString())
            .httpStatus(intCount.incrementAndGet())
            .attempts(intCount.incrementAndGet())
            .lastError(UUID.randomUUID().toString());
    }
}
