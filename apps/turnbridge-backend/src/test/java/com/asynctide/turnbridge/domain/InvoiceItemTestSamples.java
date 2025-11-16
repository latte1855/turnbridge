package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InvoiceItem getInvoiceItemSample1() {
        return new InvoiceItem().id(1L).description("description1").sequence(1);
    }

    public static InvoiceItem getInvoiceItemSample2() {
        return new InvoiceItem().id(2L).description("description2").sequence(2);
    }

    public static InvoiceItem getInvoiceItemRandomSampleGenerator() {
        return new InvoiceItem()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .sequence(intCount.incrementAndGet());
    }
}
