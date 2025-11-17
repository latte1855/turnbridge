package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.ImportFileItemAsserts.*;
import static com.asynctide.turnbridge.domain.ImportFileItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImportFileItemMapperTest {

    private ImportFileItemMapper importFileItemMapper;

    @BeforeEach
    void setUp() {
        importFileItemMapper = new ImportFileItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getImportFileItemSample1();
        var actual = importFileItemMapper.toEntity(importFileItemMapper.toDto(expected));
        assertImportFileItemAllPropertiesEquals(expected, actual);
    }
}
