package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookEndpointDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookEndpointDTO.class);
        WebhookEndpointDTO webhookEndpointDTO1 = new WebhookEndpointDTO();
        webhookEndpointDTO1.setId(1L);
        WebhookEndpointDTO webhookEndpointDTO2 = new WebhookEndpointDTO();
        assertThat(webhookEndpointDTO1).isNotEqualTo(webhookEndpointDTO2);
        webhookEndpointDTO2.setId(webhookEndpointDTO1.getId());
        assertThat(webhookEndpointDTO1).isEqualTo(webhookEndpointDTO2);
        webhookEndpointDTO2.setId(2L);
        assertThat(webhookEndpointDTO1).isNotEqualTo(webhookEndpointDTO2);
        webhookEndpointDTO1.setId(null);
        assertThat(webhookEndpointDTO1).isNotEqualTo(webhookEndpointDTO2);
    }
}
