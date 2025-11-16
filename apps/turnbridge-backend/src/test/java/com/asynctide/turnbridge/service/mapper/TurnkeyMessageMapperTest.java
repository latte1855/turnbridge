package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.TurnkeyMessageAsserts.*;
import static com.asynctide.turnbridge.domain.TurnkeyMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TurnkeyMessageMapperTest {

    private TurnkeyMessageMapper turnkeyMessageMapper;

    @BeforeEach
    void setUp() {
        turnkeyMessageMapper = new TurnkeyMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTurnkeyMessageSample1();
        var actual = turnkeyMessageMapper.toEntity(turnkeyMessageMapper.toDto(expected));
        assertTurnkeyMessageAllPropertiesEquals(expected, actual);
    }
}
