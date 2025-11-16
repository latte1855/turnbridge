package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileLogTestSamples.*;
import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileLog.class);
        ImportFileLog importFileLog1 = getImportFileLogSample1();
        ImportFileLog importFileLog2 = new ImportFileLog();
        assertThat(importFileLog1).isNotEqualTo(importFileLog2);

        importFileLog2.setId(importFileLog1.getId());
        assertThat(importFileLog1).isEqualTo(importFileLog2);

        importFileLog2 = getImportFileLogSample2();
        assertThat(importFileLog1).isNotEqualTo(importFileLog2);
    }

    @Test
    void importFileTest() {
        ImportFileLog importFileLog = getImportFileLogRandomSampleGenerator();
        ImportFile importFileBack = getImportFileRandomSampleGenerator();

        importFileLog.setImportFile(importFileBack);
        assertThat(importFileLog.getImportFile()).isEqualTo(importFileBack);

        importFileLog.importFile(null);
        assertThat(importFileLog.getImportFile()).isNull();
    }
}
