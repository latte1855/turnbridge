package com.asynctide.turnbridge.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceAssignNoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceAssignNoDTO.class);
        InvoiceAssignNoDTO invoiceAssignNoDTO1 = new InvoiceAssignNoDTO();
        invoiceAssignNoDTO1.setId(1L);
        InvoiceAssignNoDTO invoiceAssignNoDTO2 = new InvoiceAssignNoDTO();
        assertThat(invoiceAssignNoDTO1).isNotEqualTo(invoiceAssignNoDTO2);
        invoiceAssignNoDTO2.setId(invoiceAssignNoDTO1.getId());
        assertThat(invoiceAssignNoDTO1).isEqualTo(invoiceAssignNoDTO2);
        invoiceAssignNoDTO2.setId(2L);
        assertThat(invoiceAssignNoDTO1).isNotEqualTo(invoiceAssignNoDTO2);
        invoiceAssignNoDTO1.setId(null);
        assertThat(invoiceAssignNoDTO1).isNotEqualTo(invoiceAssignNoDTO2);
    }
}
