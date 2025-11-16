package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.WebhookDeliveryLogAsserts.*;
import static com.asynctide.turnbridge.domain.WebhookDeliveryLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebhookDeliveryLogMapperTest {

    private WebhookDeliveryLogMapper webhookDeliveryLogMapper;

    @BeforeEach
    void setUp() {
        webhookDeliveryLogMapper = new WebhookDeliveryLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWebhookDeliveryLogSample1();
        var actual = webhookDeliveryLogMapper.toEntity(webhookDeliveryLogMapper.toDto(expected));
        assertWebhookDeliveryLogAllPropertiesEquals(expected, actual);
    }
}
