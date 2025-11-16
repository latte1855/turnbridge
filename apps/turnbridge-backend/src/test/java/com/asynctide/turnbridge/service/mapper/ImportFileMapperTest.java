package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.ImportFileAsserts.*;
import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImportFileMapperTest {

    private ImportFileMapper importFileMapper;

    @BeforeEach
    void setUp() {
        importFileMapper = new ImportFileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getImportFileSample1();
        var actual = importFileMapper.toEntity(importFileMapper.toDto(expected));
        assertImportFileAllPropertiesEquals(expected, actual);
    }
}
