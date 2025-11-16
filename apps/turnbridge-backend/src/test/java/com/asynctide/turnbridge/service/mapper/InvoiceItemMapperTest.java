package com.asynctide.turnbridge.service.mapper;

import static com.asynctide.turnbridge.domain.InvoiceItemAsserts.*;
import static com.asynctide.turnbridge.domain.InvoiceItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceItemMapperTest {

    private InvoiceItemMapper invoiceItemMapper;

    @BeforeEach
    void setUp() {
        invoiceItemMapper = new InvoiceItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInvoiceItemSample1();
        var actual = invoiceItemMapper.toEntity(invoiceItemMapper.toDto(expected));
        assertInvoiceItemAllPropertiesEquals(expected, actual);
    }
}
