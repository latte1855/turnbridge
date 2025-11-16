package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;
import static com.asynctide.turnbridge.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFile.class);
        ImportFile importFile1 = getImportFileSample1();
        ImportFile importFile2 = new ImportFile();
        assertThat(importFile1).isNotEqualTo(importFile2);

        importFile2.setId(importFile1.getId());
        assertThat(importFile1).isEqualTo(importFile2);

        importFile2 = getImportFileSample2();
        assertThat(importFile1).isNotEqualTo(importFile2);
    }

    @Test
    void tenantTest() {
        ImportFile importFile = getImportFileRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        importFile.setTenant(tenantBack);
        assertThat(importFile.getTenant()).isEqualTo(tenantBack);

        importFile.tenant(null);
        assertThat(importFile.getTenant()).isNull();
    }
}
