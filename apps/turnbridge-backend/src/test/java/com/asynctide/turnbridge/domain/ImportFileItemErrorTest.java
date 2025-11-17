package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileItemErrorTestSamples.*;
import static com.asynctide.turnbridge.domain.ImportFileItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileItemErrorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileItemError.class);
        ImportFileItemError importFileItemError1 = getImportFileItemErrorSample1();
        ImportFileItemError importFileItemError2 = new ImportFileItemError();
        assertThat(importFileItemError1).isNotEqualTo(importFileItemError2);

        importFileItemError2.setId(importFileItemError1.getId());
        assertThat(importFileItemError1).isEqualTo(importFileItemError2);

        importFileItemError2 = getImportFileItemErrorSample2();
        assertThat(importFileItemError1).isNotEqualTo(importFileItemError2);
    }

    @Test
    void importFileItemTest() {
        ImportFileItemError importFileItemError = getImportFileItemErrorRandomSampleGenerator();
        ImportFileItem importFileItemBack = getImportFileItemRandomSampleGenerator();

        importFileItemError.setImportFileItem(importFileItemBack);
        assertThat(importFileItemError.getImportFileItem()).isEqualTo(importFileItemBack);

        importFileItemError.importFileItem(null);
        assertThat(importFileItemError.getImportFileItem()).isNull();
    }
}
