package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileLogDTO.class);
        ImportFileLogDTO importFileLogDTO1 = new ImportFileLogDTO();
        importFileLogDTO1.setId(1L);
        ImportFileLogDTO importFileLogDTO2 = new ImportFileLogDTO();
        assertThat(importFileLogDTO1).isNotEqualTo(importFileLogDTO2);
        importFileLogDTO2.setId(importFileLogDTO1.getId());
        assertThat(importFileLogDTO1).isEqualTo(importFileLogDTO2);
        importFileLogDTO2.setId(2L);
        assertThat(importFileLogDTO1).isNotEqualTo(importFileLogDTO2);
        importFileLogDTO1.setId(null);
        assertThat(importFileLogDTO1).isNotEqualTo(importFileLogDTO2);
    }
}
