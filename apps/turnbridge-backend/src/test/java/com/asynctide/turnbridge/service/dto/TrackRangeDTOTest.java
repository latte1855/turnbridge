package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrackRangeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrackRangeDTO.class);
        TrackRangeDTO trackRangeDTO1 = new TrackRangeDTO();
        trackRangeDTO1.setId(1L);
        TrackRangeDTO trackRangeDTO2 = new TrackRangeDTO();
        assertThat(trackRangeDTO1).isNotEqualTo(trackRangeDTO2);
        trackRangeDTO2.setId(trackRangeDTO1.getId());
        assertThat(trackRangeDTO1).isEqualTo(trackRangeDTO2);
        trackRangeDTO2.setId(2L);
        assertThat(trackRangeDTO1).isNotEqualTo(trackRangeDTO2);
        trackRangeDTO1.setId(null);
        assertThat(trackRangeDTO1).isNotEqualTo(trackRangeDTO2);
    }
}
