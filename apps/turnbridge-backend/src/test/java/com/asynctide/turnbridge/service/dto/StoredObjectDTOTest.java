package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoredObjectDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoredObjectDTO.class);
        StoredObjectDTO storedObjectDTO1 = new StoredObjectDTO();
        storedObjectDTO1.setId(1L);
        StoredObjectDTO storedObjectDTO2 = new StoredObjectDTO();
        assertThat(storedObjectDTO1).isNotEqualTo(storedObjectDTO2);
        storedObjectDTO2.setId(storedObjectDTO1.getId());
        assertThat(storedObjectDTO1).isEqualTo(storedObjectDTO2);
        storedObjectDTO2.setId(2L);
        assertThat(storedObjectDTO1).isNotEqualTo(storedObjectDTO2);
        storedObjectDTO1.setId(null);
        assertThat(storedObjectDTO1).isNotEqualTo(storedObjectDTO2);
    }
}
