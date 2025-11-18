package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileItemDTO.class);
        ImportFileItemDTO importFileItemDTO1 = new ImportFileItemDTO();
        importFileItemDTO1.setId(1L);
        ImportFileItemDTO importFileItemDTO2 = new ImportFileItemDTO();
        assertThat(importFileItemDTO1).isNotEqualTo(importFileItemDTO2);
        importFileItemDTO2.setId(importFileItemDTO1.getId());
        assertThat(importFileItemDTO1).isEqualTo(importFileItemDTO2);
        importFileItemDTO2.setId(2L);
        assertThat(importFileItemDTO1).isNotEqualTo(importFileItemDTO2);
        importFileItemDTO1.setId(null);
        assertThat(importFileItemDTO1).isNotEqualTo(importFileItemDTO2);
    }
}
