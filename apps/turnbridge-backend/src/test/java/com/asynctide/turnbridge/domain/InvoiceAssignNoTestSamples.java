package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceAssignNoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InvoiceAssignNo getInvoiceAssignNoSample1() {
        return new InvoiceAssignNo()
            .id(1L)
            .track("track1")
            .period("period1")
            .fromNo("fromNo1")
            .toNo("toNo1")
            .usedCount(1)
            .rollSize(1)
            .status("status1");
    }

    public static InvoiceAssignNo getInvoiceAssignNoSample2() {
        return new InvoiceAssignNo()
            .id(2L)
            .track("track2")
            .period("period2")
            .fromNo("fromNo2")
            .toNo("toNo2")
            .usedCount(2)
            .rollSize(2)
            .status("status2");
    }

    public static InvoiceAssignNo getInvoiceAssignNoRandomSampleGenerator() {
        return new InvoiceAssignNo()
            .id(longCount.incrementAndGet())
            .track(UUID.randomUUID().toString())
            .period(UUID.randomUUID().toString())
            .fromNo(UUID.randomUUID().toString())
            .toNo(UUID.randomUUID().toString())
            .usedCount(intCount.incrementAndGet())
            .rollSize(intCount.incrementAndGet())
            .status(UUID.randomUUID().toString());
    }
}
