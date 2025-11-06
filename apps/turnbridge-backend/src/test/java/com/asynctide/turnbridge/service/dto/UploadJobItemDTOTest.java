package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UploadJobItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadJobItemDTO.class);
        UploadJobItemDTO uploadJobItemDTO1 = new UploadJobItemDTO();
        uploadJobItemDTO1.setId(1L);
        UploadJobItemDTO uploadJobItemDTO2 = new UploadJobItemDTO();
        assertThat(uploadJobItemDTO1).isNotEqualTo(uploadJobItemDTO2);
        uploadJobItemDTO2.setId(uploadJobItemDTO1.getId());
        assertThat(uploadJobItemDTO1).isEqualTo(uploadJobItemDTO2);
        uploadJobItemDTO2.setId(2L);
        assertThat(uploadJobItemDTO1).isNotEqualTo(uploadJobItemDTO2);
        uploadJobItemDTO1.setId(null);
        assertThat(uploadJobItemDTO1).isNotEqualTo(uploadJobItemDTO2);
    }
}
