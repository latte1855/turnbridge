package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.ImportFileItemTestSamples.*;
import static com.asynctide.turnbridge.domain.ImportFileTestSamples.*;
import static com.asynctide.turnbridge.domain.InvoiceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportFileItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportFileItem.class);
        ImportFileItem importFileItem1 = getImportFileItemSample1();
        ImportFileItem importFileItem2 = new ImportFileItem();
        assertThat(importFileItem1).isNotEqualTo(importFileItem2);

        importFileItem2.setId(importFileItem1.getId());
        assertThat(importFileItem1).isEqualTo(importFileItem2);

        importFileItem2 = getImportFileItemSample2();
        assertThat(importFileItem1).isNotEqualTo(importFileItem2);
    }

    @Test
    void importFileTest() {
        ImportFileItem importFileItem = getImportFileItemRandomSampleGenerator();
        ImportFile importFileBack = getImportFileRandomSampleGenerator();

        importFileItem.setImportFile(importFileBack);
        assertThat(importFileItem.getImportFile()).isEqualTo(importFileBack);

        importFileItem.importFile(null);
        assertThat(importFileItem.getImportFile()).isNull();
    }

    @Test
    void invoiceTest() {
        ImportFileItem importFileItem = getImportFileItemRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        importFileItem.setInvoice(invoiceBack);
        assertThat(importFileItem.getInvoice()).isEqualTo(invoiceBack);

        importFileItem.invoice(null);
        assertThat(importFileItem.getInvoice()).isNull();
    }
}
