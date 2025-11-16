package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.WebhookEndpointAsserts.*;
import static com.asynctide.turnbridge.domain.WebhookEndpointTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebhookEndpointMapperTest {

    private WebhookEndpointMapper webhookEndpointMapper;

    @BeforeEach
    void setUp() {
        webhookEndpointMapper = new WebhookEndpointMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWebhookEndpointSample1();
        var actual = webhookEndpointMapper.toEntity(webhookEndpointMapper.toDto(expected));
        assertWebhookEndpointAllPropertiesEquals(expected, actual);
    }
}
