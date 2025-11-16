package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.ImportFileLogAsserts.*;
import static com.asynctide.turnbridge.domain.ImportFileLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImportFileLogMapperTest {

    private ImportFileLogMapper importFileLogMapper;

    @BeforeEach
    void setUp() {
        importFileLogMapper = new ImportFileLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getImportFileLogSample1();
        var actual = importFileLogMapper.toEntity(importFileLogMapper.toDto(expected));
        assertImportFileLogAllPropertiesEquals(expected, actual);
    }
}
