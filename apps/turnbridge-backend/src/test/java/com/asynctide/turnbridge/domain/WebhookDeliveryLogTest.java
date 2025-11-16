package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.WebhookDeliveryLogTestSamples.*;
import static com.asynctide.turnbridge.domain.WebhookEndpointTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookDeliveryLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookDeliveryLog.class);
        WebhookDeliveryLog webhookDeliveryLog1 = getWebhookDeliveryLogSample1();
        WebhookDeliveryLog webhookDeliveryLog2 = new WebhookDeliveryLog();
        assertThat(webhookDeliveryLog1).isNotEqualTo(webhookDeliveryLog2);

        webhookDeliveryLog2.setId(webhookDeliveryLog1.getId());
        assertThat(webhookDeliveryLog1).isEqualTo(webhookDeliveryLog2);

        webhookDeliveryLog2 = getWebhookDeliveryLogSample2();
        assertThat(webhookDeliveryLog1).isNotEqualTo(webhookDeliveryLog2);
    }

    @Test
    void webhookEndpointTest() {
        WebhookDeliveryLog webhookDeliveryLog = getWebhookDeliveryLogRandomSampleGenerator();
        WebhookEndpoint webhookEndpointBack = getWebhookEndpointRandomSampleGenerator();

        webhookDeliveryLog.setWebhookEndpoint(webhookEndpointBack);
        assertThat(webhookDeliveryLog.getWebhookEndpoint()).isEqualTo(webhookEndpointBack);

        webhookDeliveryLog.webhookEndpoint(null);
        assertThat(webhookDeliveryLog.getWebhookEndpoint()).isNull();
    }
}
