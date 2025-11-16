package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WebhookDeliveryLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebhookDeliveryLogDTO.class);
        WebhookDeliveryLogDTO webhookDeliveryLogDTO1 = new WebhookDeliveryLogDTO();
        webhookDeliveryLogDTO1.setId(1L);
        WebhookDeliveryLogDTO webhookDeliveryLogDTO2 = new WebhookDeliveryLogDTO();
        assertThat(webhookDeliveryLogDTO1).isNotEqualTo(webhookDeliveryLogDTO2);
        webhookDeliveryLogDTO2.setId(webhookDeliveryLogDTO1.getId());
        assertThat(webhookDeliveryLogDTO1).isEqualTo(webhookDeliveryLogDTO2);
        webhookDeliveryLogDTO2.setId(2L);
        assertThat(webhookDeliveryLogDTO1).isNotEqualTo(webhookDeliveryLogDTO2);
        webhookDeliveryLogDTO1.setId(null);
        assertThat(webhookDeliveryLogDTO1).isNotEqualTo(webhookDeliveryLogDTO2);
    }
}
