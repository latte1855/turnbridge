package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UploadJobItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UploadJobItem getUploadJobItemSample1() {
        return new UploadJobItem()
            .id(1L)
            .lineNo(1)
            .traceId("traceId1")
            .resultCode("resultCode1")
            .resultMsg("resultMsg1")
            .buyerId("buyerId1")
            .buyerName("buyerName1")
            .currency("currency1")
            .invoiceNo("invoiceNo1")
            .assignedPrefix("assignedPrefix1")
            .rawHash("rawHash1")
            .profileDetected("profileDetected1");
    }

    public static UploadJobItem getUploadJobItemSample2() {
        return new UploadJobItem()
            .id(2L)
            .lineNo(2)
            .traceId("traceId2")
            .resultCode("resultCode2")
            .resultMsg("resultMsg2")
            .buyerId("buyerId2")
            .buyerName("buyerName2")
            .currency("currency2")
            .invoiceNo("invoiceNo2")
            .assignedPrefix("assignedPrefix2")
            .rawHash("rawHash2")
            .profileDetected("profileDetected2");
    }

    public static UploadJobItem getUploadJobItemRandomSampleGenerator() {
        return new UploadJobItem()
            .id(longCount.incrementAndGet())
            .lineNo(intCount.incrementAndGet())
            .traceId(UUID.randomUUID().toString())
            .resultCode(UUID.randomUUID().toString())
            .resultMsg(UUID.randomUUID().toString())
            .buyerId(UUID.randomUUID().toString())
            .buyerName(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .invoiceNo(UUID.randomUUID().toString())
            .assignedPrefix(UUID.randomUUID().toString())
            .rawHash(UUID.randomUUID().toString())
            .profileDetected(UUID.randomUUID().toString());
    }
}
