package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WebhookEndpointTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static WebhookEndpoint getWebhookEndpointSample1() {
        return new WebhookEndpoint().id(1L).name("name1").targetUrl("targetUrl1").secret("secret1").events("events1");
    }

    public static WebhookEndpoint getWebhookEndpointSample2() {
        return new WebhookEndpoint().id(2L).name("name2").targetUrl("targetUrl2").secret("secret2").events("events2");
    }

    public static WebhookEndpoint getWebhookEndpointRandomSampleGenerator() {
        return new WebhookEndpoint()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .targetUrl(UUID.randomUUID().toString())
            .secret(UUID.randomUUID().toString())
            .events(UUID.randomUUID().toString());
    }
}
