package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.StoredObjectAsserts.*;
import static com.asynctide.turnbridge.domain.StoredObjectTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoredObjectMapperTest {

    private StoredObjectMapper storedObjectMapper;

    @BeforeEach
    void setUp() {
        storedObjectMapper = new StoredObjectMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStoredObjectSample1();
        var actual = storedObjectMapper.toEntity(storedObjectMapper.toDto(expected));
        assertStoredObjectAllPropertiesEquals(expected, actual);
    }
}
