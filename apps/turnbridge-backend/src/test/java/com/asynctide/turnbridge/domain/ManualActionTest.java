package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;
import static com.asynctide.turnbridge.domain.InvoiceTestSamples.*;
import static com.asynctide.turnbridge.domain.ManualActionTestSamples.*;
import static com.asynctide.turnbridge.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ManualActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ManualAction.class);
        ManualAction manualAction1 = getManualActionSample1();
        ManualAction manualAction2 = new ManualAction();
        assertThat(manualAction1).isNotEqualTo(manualAction2);

        manualAction2.setId(manualAction1.getId());
        assertThat(manualAction1).isEqualTo(manualAction2);

        manualAction2 = getManualActionSample2();
        assertThat(manualAction1).isNotEqualTo(manualAction2);
    }

    @Test
    void tenantTest() {
        ManualAction manualAction = getManualActionRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        manualAction.setTenant(tenantBack);
        assertThat(manualAction.getTenant()).isEqualTo(tenantBack);

        manualAction.tenant(null);
        assertThat(manualAction.getTenant()).isNull();
    }

    @Test
    void invoiceTest() {
        ManualAction manualAction = getManualActionRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        manualAction.setInvoice(invoiceBack);
        assertThat(manualAction.getInvoice()).isEqualTo(invoiceBack);

        manualAction.invoice(null);
        assertThat(manualAction.getInvoice()).isNull();
    }

    @Test
    void importFileTest() {
        ManualAction manualAction = getManualActionRandomSampleGenerator();
        ImportFile importFileBack = getImportFileRandomSampleGenerator();

        manualAction.setImportFile(importFileBack);
        assertThat(manualAction.getImportFile()).isEqualTo(importFileBack);

        manualAction.importFile(null);
        assertThat(manualAction.getImportFile()).isNull();
    }
}
