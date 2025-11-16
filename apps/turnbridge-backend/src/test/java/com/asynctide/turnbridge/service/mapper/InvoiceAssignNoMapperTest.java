package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.InvoiceAssignNoAsserts.*;
import static com.asynctide.turnbridge.domain.InvoiceAssignNoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceAssignNoMapperTest {

    private InvoiceAssignNoMapper invoiceAssignNoMapper;

    @BeforeEach
    void setUp() {
        invoiceAssignNoMapper = new InvoiceAssignNoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInvoiceAssignNoSample1();
        var actual = invoiceAssignNoMapper.toEntity(invoiceAssignNoMapper.toDto(expected));
        assertInvoiceAssignNoAllPropertiesEquals(expected, actual);
    }
}
