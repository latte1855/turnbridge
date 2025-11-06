package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.TrackRangeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrackRangeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrackRange.class);
        TrackRange trackRange1 = getTrackRangeSample1();
        TrackRange trackRange2 = new TrackRange();
        assertThat(trackRange1).isNotEqualTo(trackRange2);

        trackRange2.setId(trackRange1.getId());
        assertThat(trackRange1).isEqualTo(trackRange2);

        trackRange2 = getTrackRangeSample2();
        assertThat(trackRange1).isNotEqualTo(trackRange2);
    }
}
