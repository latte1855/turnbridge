package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.UploadJobItemTestSamples.*;
import static com.asynctide.turnbridge.domain.UploadJobTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UploadJobItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadJobItem.class);
        UploadJobItem uploadJobItem1 = getUploadJobItemSample1();
        UploadJobItem uploadJobItem2 = new UploadJobItem();
        assertThat(uploadJobItem1).isNotEqualTo(uploadJobItem2);

        uploadJobItem2.setId(uploadJobItem1.getId());
        assertThat(uploadJobItem1).isEqualTo(uploadJobItem2);

        uploadJobItem2 = getUploadJobItemSample2();
        assertThat(uploadJobItem1).isNotEqualTo(uploadJobItem2);
    }

    @Test
    void jobTest() {
        UploadJobItem uploadJobItem = getUploadJobItemRandomSampleGenerator();
        UploadJob uploadJobBack = getUploadJobRandomSampleGenerator();

        uploadJobItem.setJob(uploadJobBack);
        assertThat(uploadJobItem.getJob()).isEqualTo(uploadJobBack);

        uploadJobItem.job(null);
        assertThat(uploadJobItem.getJob()).isNull();
    }
}
