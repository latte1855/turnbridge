package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileItemErrorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileItemErrorDTO.class);
        ImportFileItemErrorDTO importFileItemErrorDTO1 = new ImportFileItemErrorDTO();
        importFileItemErrorDTO1.setId(1L);
        ImportFileItemErrorDTO importFileItemErrorDTO2 = new ImportFileItemErrorDTO();
        assertThat(importFileItemErrorDTO1).isNotEqualTo(importFileItemErrorDTO2);
        importFileItemErrorDTO2.setId(importFileItemErrorDTO1.getId());
        assertThat(importFileItemErrorDTO1).isEqualTo(importFileItemErrorDTO2);
        importFileItemErrorDTO2.setId(2L);
        assertThat(importFileItemErrorDTO1).isNotEqualTo(importFileItemErrorDTO2);
        importFileItemErrorDTO1.setId(null);
        assertThat(importFileItemErrorDTO1).isNotEqualTo(importFileItemErrorDTO2);
    }
}
