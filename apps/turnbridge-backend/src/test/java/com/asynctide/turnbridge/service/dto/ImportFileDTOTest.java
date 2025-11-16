package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileDTO.class);
        ImportFileDTO importFileDTO1 = new ImportFileDTO();
        importFileDTO1.setId(1L);
        ImportFileDTO importFileDTO2 = new ImportFileDTO();
        assertThat(importFileDTO1).isNotEqualTo(importFileDTO2);
        importFileDTO2.setId(importFileDTO1.getId());
        assertThat(importFileDTO1).isEqualTo(importFileDTO2);
        importFileDTO2.setId(2L);
        assertThat(importFileDTO1).isNotEqualTo(importFileDTO2);
        importFileDTO1.setId(null);
        assertThat(importFileDTO1).isNotEqualTo(importFileDTO2);
    }
}
