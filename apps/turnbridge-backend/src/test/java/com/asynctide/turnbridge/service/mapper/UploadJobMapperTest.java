package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.UploadJobAsserts.*;
import static com.asynctide.turnbridge.domain.UploadJobTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadJobMapperTest {

    private UploadJobMapper uploadJobMapper;

    @BeforeEach
    void setUp() {
        uploadJobMapper = new UploadJobMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUploadJobSample1();
        var actual = uploadJobMapper.toEntity(uploadJobMapper.toDto(expected));
        assertUploadJobAllPropertiesEquals(expected, actual);
    }
}
