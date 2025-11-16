package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.TenantTestSamples.*;
import static com.asynctide.turnbridge.domain.WebhookEndpointTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookEndpointTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookEndpoint.class);
        WebhookEndpoint webhookEndpoint1 = getWebhookEndpointSample1();
        WebhookEndpoint webhookEndpoint2 = new WebhookEndpoint();
        assertThat(webhookEndpoint1).isNotEqualTo(webhookEndpoint2);

        webhookEndpoint2.setId(webhookEndpoint1.getId());
        assertThat(webhookEndpoint1).isEqualTo(webhookEndpoint2);

        webhookEndpoint2 = getWebhookEndpointSample2();
        assertThat(webhookEndpoint1).isNotEqualTo(webhookEndpoint2);
    }

    @Test
    void tenantTest() {
        WebhookEndpoint webhookEndpoint = getWebhookEndpointRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        webhookEndpoint.setTenant(tenantBack);
        assertThat(webhookEndpoint.getTenant()).isEqualTo(tenantBack);

        webhookEndpoint.tenant(null);
        assertThat(webhookEndpoint.getTenant()).isNull();
    }
}
