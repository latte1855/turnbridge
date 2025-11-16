package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TurnkeyMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TurnkeyMessageDTO.class);
        TurnkeyMessageDTO turnkeyMessageDTO1 = new TurnkeyMessageDTO();
        turnkeyMessageDTO1.setId(1L);
        TurnkeyMessageDTO turnkeyMessageDTO2 = new TurnkeyMessageDTO();
        assertThat(turnkeyMessageDTO1).isNotEqualTo(turnkeyMessageDTO2);
        turnkeyMessageDTO2.setId(turnkeyMessageDTO1.getId());
        assertThat(turnkeyMessageDTO1).isEqualTo(turnkeyMessageDTO2);
        turnkeyMessageDTO2.setId(2L);
        assertThat(turnkeyMessageDTO1).isNotEqualTo(turnkeyMessageDTO2);
        turnkeyMessageDTO1.setId(null);
        assertThat(turnkeyMessageDTO1).isNotEqualTo(turnkeyMessageDTO2);
    }
}
