package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.ManualActionAsserts.*;
import static com.asynctide.turnbridge.domain.ManualActionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ManualActionMapperTest {

    private ManualActionMapper manualActionMapper;

    @BeforeEach
    void setUp() {
        manualActionMapper = new ManualActionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getManualActionSample1();
        var actual = manualActionMapper.toEntity(manualActionMapper.toDto(expected));
        assertManualActionAllPropertiesEquals(expected, actual);
    }
}
