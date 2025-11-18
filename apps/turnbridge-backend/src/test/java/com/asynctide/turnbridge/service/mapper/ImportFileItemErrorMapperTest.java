package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.ImportFileItemErrorAsserts.*;
import static com.asynctide.turnbridge.domain.ImportFileItemErrorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImportFileItemErrorMapperTest {

    private ImportFileItemErrorMapper importFileItemErrorMapper;

    @BeforeEach
    void setUp() {
        importFileItemErrorMapper = new ImportFileItemErrorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getImportFileItemErrorSample1();
        var actual = importFileItemErrorMapper.toEntity(importFileItemErrorMapper.toDto(expected));
        assertImportFileItemErrorAllPropertiesEquals(expected, actual);
    }
}
