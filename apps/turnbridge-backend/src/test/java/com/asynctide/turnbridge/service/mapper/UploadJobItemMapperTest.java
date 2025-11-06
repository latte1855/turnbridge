package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.UploadJobItemAsserts.*;
import static com.asynctide.turnbridge.domain.UploadJobItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadJobItemMapperTest {

    private UploadJobItemMapper uploadJobItemMapper;

    @BeforeEach
    void setUp() {
        uploadJobItemMapper = new UploadJobItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUploadJobItemSample1();
        var actual = uploadJobItemMapper.toEntity(uploadJobItemMapper.toDto(expected));
        assertUploadJobItemAllPropertiesEquals(expected, actual);
    }
}
