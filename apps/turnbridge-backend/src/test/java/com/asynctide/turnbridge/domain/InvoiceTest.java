package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;
import static com.asynctide.turnbridge.domain.InvoiceTestSamples.*;
import static com.asynctide.turnbridge.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Invoice.class);
        Invoice invoice1 = getInvoiceSample1();
        Invoice invoice2 = new Invoice();
        assertThat(invoice1).isNotEqualTo(invoice2);

        invoice2.setId(invoice1.getId());
        assertThat(invoice1).isEqualTo(invoice2);

        invoice2 = getInvoiceSample2();
        assertThat(invoice1).isNotEqualTo(invoice2);
    }

    @Test
    void importFileTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        ImportFile importFileBack = getImportFileRandomSampleGenerator();

        invoice.setImportFile(importFileBack);
        assertThat(invoice.getImportFile()).isEqualTo(importFileBack);

        invoice.importFile(null);
        assertThat(invoice.getImportFile()).isNull();
    }

    @Test
    void tenantTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        invoice.setTenant(tenantBack);
        assertThat(invoice.getTenant()).isEqualTo(tenantBack);

        invoice.tenant(null);
        assertThat(invoice.getTenant()).isNull();
    }
}
