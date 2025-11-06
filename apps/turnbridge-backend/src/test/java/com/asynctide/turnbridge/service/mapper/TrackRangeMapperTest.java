package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.TrackRangeAsserts.*;
import static com.asynctide.turnbridge.domain.TrackRangeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackRangeMapperTest {

    private TrackRangeMapper trackRangeMapper;

    @BeforeEach
    void setUp() {
        trackRangeMapper = new TrackRangeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrackRangeSample1();
        var actual = trackRangeMapper.toEntity(trackRangeMapper.toDto(expected));
        assertTrackRangeAllPropertiesEquals(expected, actual);
    }
}
