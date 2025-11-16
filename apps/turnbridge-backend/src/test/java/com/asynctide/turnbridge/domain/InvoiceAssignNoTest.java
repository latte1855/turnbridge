package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.InvoiceAssignNoTestSamples.*;
import static com.asynctide.turnbridge.domain.TenantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceAssignNoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceAssignNo.class);
        InvoiceAssignNo invoiceAssignNo1 = getInvoiceAssignNoSample1();
        InvoiceAssignNo invoiceAssignNo2 = new InvoiceAssignNo();
        assertThat(invoiceAssignNo1).isNotEqualTo(invoiceAssignNo2);

        invoiceAssignNo2.setId(invoiceAssignNo1.getId());
        assertThat(invoiceAssignNo1).isEqualTo(invoiceAssignNo2);

        invoiceAssignNo2 = getInvoiceAssignNoSample2();
        assertThat(invoiceAssignNo1).isNotEqualTo(invoiceAssignNo2);
    }

    @Test
    void tenantTest() {
        InvoiceAssignNo invoiceAssignNo = getInvoiceAssignNoRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        invoiceAssignNo.setTenant(tenantBack);
        assertThat(invoiceAssignNo.getTenant()).isEqualTo(tenantBack);

        invoiceAssignNo.tenant(null);
        assertThat(invoiceAssignNo.getTenant()).isNull();
    }
}
