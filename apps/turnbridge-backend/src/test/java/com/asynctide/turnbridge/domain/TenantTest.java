package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;
import static com.asynctide.turnbridge.domain.InvoiceTestSamples.*;
import static com.asynctide.turnbridge.domain.TenantTestSamples.*;
import static com.asynctide.turnbridge.domain.WebhookEndpointTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TenantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tenant.class);
        Tenant tenant1 = getTenantSample1();
        Tenant tenant2 = new Tenant();
        assertThat(tenant1).isNotEqualTo(tenant2);

        tenant2.setId(tenant1.getId());
        assertThat(tenant1).isEqualTo(tenant2);

        tenant2 = getTenantSample2();
        assertThat(tenant1).isNotEqualTo(tenant2);
    }

    @Test
    void importFilesTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        ImportFile importFileBack = getImportFileRandomSampleGenerator();

        tenant.addImportFiles(importFileBack);
        assertThat(tenant.getImportFiles()).containsOnly(importFileBack);
        assertThat(importFileBack.getTenant()).isEqualTo(tenant);

        tenant.removeImportFiles(importFileBack);
        assertThat(tenant.getImportFiles()).doesNotContain(importFileBack);
        assertThat(importFileBack.getTenant()).isNull();

        tenant.importFiles(new HashSet<>(Set.of(importFileBack)));
        assertThat(tenant.getImportFiles()).containsOnly(importFileBack);
        assertThat(importFileBack.getTenant()).isEqualTo(tenant);

        tenant.setImportFiles(new HashSet<>());
        assertThat(tenant.getImportFiles()).doesNotContain(importFileBack);
        assertThat(importFileBack.getTenant()).isNull();
    }

    @Test
    void invoicesTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        tenant.addInvoices(invoiceBack);
        assertThat(tenant.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getTenant()).isEqualTo(tenant);

        tenant.removeInvoices(invoiceBack);
        assertThat(tenant.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getTenant()).isNull();

        tenant.invoices(new HashSet<>(Set.of(invoiceBack)));
        assertThat(tenant.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getTenant()).isEqualTo(tenant);

        tenant.setInvoices(new HashSet<>());
        assertThat(tenant.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getTenant()).isNull();
    }

    @Test
    void webhookEndpointsTest() {
        Tenant tenant = getTenantRandomSampleGenerator();
        WebhookEndpoint webhookEndpointBack = getWebhookEndpointRandomSampleGenerator();

        tenant.addWebhookEndpoints(webhookEndpointBack);
        assertThat(tenant.getWebhookEndpoints()).containsOnly(webhookEndpointBack);
        assertThat(webhookEndpointBack.getTenant()).isEqualTo(tenant);

        tenant.removeWebhookEndpoints(webhookEndpointBack);
        assertThat(tenant.getWebhookEndpoints()).doesNotContain(webhookEndpointBack);
        assertThat(webhookEndpointBack.getTenant()).isNull();

        tenant.webhookEndpoints(new HashSet<>(Set.of(webhookEndpointBack)));
        assertThat(tenant.getWebhookEndpoints()).containsOnly(webhookEndpointBack);
        assertThat(webhookEndpointBack.getTenant()).isEqualTo(tenant);

        tenant.setWebhookEndpoints(new HashSet<>());
        assertThat(tenant.getWebhookEndpoints()).doesNotContain(webhookEndpointBack);
        assertThat(webhookEndpointBack.getTenant()).isNull();
    }
}
