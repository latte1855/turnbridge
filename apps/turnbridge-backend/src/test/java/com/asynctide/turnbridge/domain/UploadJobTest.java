package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.StoredObjectTestSamples.*;
import static com.asynctide.turnbridge.domain.UploadJobItemTestSamples.*;
import static com.asynctide.turnbridge.domain.UploadJobTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UploadJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadJob.class);
        UploadJob uploadJob1 = getUploadJobSample1();
        UploadJob uploadJob2 = new UploadJob();
        assertThat(uploadJob1).isNotEqualTo(uploadJob2);

        uploadJob2.setId(uploadJob1.getId());
        assertThat(uploadJob1).isEqualTo(uploadJob2);

        uploadJob2 = getUploadJobSample2();
        assertThat(uploadJob1).isNotEqualTo(uploadJob2);
    }

    @Test
    void itemsTest() {
        UploadJob uploadJob = getUploadJobRandomSampleGenerator();
        UploadJobItem uploadJobItemBack = getUploadJobItemRandomSampleGenerator();

        uploadJob.addItems(uploadJobItemBack);
        assertThat(uploadJob.getItems()).containsOnly(uploadJobItemBack);
        assertThat(uploadJobItemBack.getJob()).isEqualTo(uploadJob);

        uploadJob.removeItems(uploadJobItemBack);
        assertThat(uploadJob.getItems()).doesNotContain(uploadJobItemBack);
        assertThat(uploadJobItemBack.getJob()).isNull();

        uploadJob.items(new HashSet<>(Set.of(uploadJobItemBack)));
        assertThat(uploadJob.getItems()).containsOnly(uploadJobItemBack);
        assertThat(uploadJobItemBack.getJob()).isEqualTo(uploadJob);

        uploadJob.setItems(new HashSet<>());
        assertThat(uploadJob.getItems()).doesNotContain(uploadJobItemBack);
        assertThat(uploadJobItemBack.getJob()).isNull();
    }

    @Test
    void originalFileTest() {
        UploadJob uploadJob = getUploadJobRandomSampleGenerator();
        StoredObject storedObjectBack = getStoredObjectRandomSampleGenerator();

        uploadJob.setOriginalFile(storedObjectBack);
        assertThat(uploadJob.getOriginalFile()).isEqualTo(storedObjectBack);

        uploadJob.originalFile(null);
        assertThat(uploadJob.getOriginalFile()).isNull();
    }

    @Test
    void resultFileTest() {
        UploadJob uploadJob = getUploadJobRandomSampleGenerator();
        StoredObject storedObjectBack = getStoredObjectRandomSampleGenerator();

        uploadJob.setResultFile(storedObjectBack);
        assertThat(uploadJob.getResultFile()).isEqualTo(storedObjectBack);

        uploadJob.resultFile(null);
        assertThat(uploadJob.getResultFile()).isNull();
    }
}
