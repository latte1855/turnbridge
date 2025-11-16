package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ManualActionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ManualActionDTO.class);
        ManualActionDTO manualActionDTO1 = new ManualActionDTO();
        manualActionDTO1.setId(1L);
        ManualActionDTO manualActionDTO2 = new ManualActionDTO();
        assertThat(manualActionDTO1).isNotEqualTo(manualActionDTO2);
        manualActionDTO2.setId(manualActionDTO1.getId());
        assertThat(manualActionDTO1).isEqualTo(manualActionDTO2);
        manualActionDTO2.setId(2L);
        assertThat(manualActionDTO1).isNotEqualTo(manualActionDTO2);
        manualActionDTO1.setId(null);
        assertThat(manualActionDTO1).isNotEqualTo(manualActionDTO2);
    }
}
