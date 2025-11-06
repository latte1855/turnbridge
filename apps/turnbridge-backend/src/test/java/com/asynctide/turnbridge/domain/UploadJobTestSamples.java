package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UploadJobTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UploadJob getUploadJobSample1() {
        return new UploadJob()
            .id(1L)
            .jobId("jobId1")
            .sellerId("sellerId1")
            .sellerName("sellerName1")
            .period("period1")
            .profile("profile1")
            .sourceFilename("sourceFilename1")
            .sourceMediaType("sourceMediaType1")
            .total(1)
            .accepted(1)
            .failed(1)
            .sent(1)
            .remark("remark1");
    }

    public static UploadJob getUploadJobSample2() {
        return new UploadJob()
            .id(2L)
            .jobId("jobId2")
            .sellerId("sellerId2")
            .sellerName("sellerName2")
            .period("period2")
            .profile("profile2")
            .sourceFilename("sourceFilename2")
            .sourceMediaType("sourceMediaType2")
            .total(2)
            .accepted(2)
            .failed(2)
            .sent(2)
            .remark("remark2");
    }

    public static UploadJob getUploadJobRandomSampleGenerator() {
        return new UploadJob()
            .id(longCount.incrementAndGet())
            .jobId(UUID.randomUUID().toString())
            .sellerId(UUID.randomUUID().toString())
            .sellerName(UUID.randomUUID().toString())
            .period(UUID.randomUUID().toString())
            .profile(UUID.randomUUID().toString())
            .sourceFilename(UUID.randomUUID().toString())
            .sourceMediaType(UUID.randomUUID().toString())
            .total(intCount.incrementAndGet())
            .accepted(intCount.incrementAndGet())
            .failed(intCount.incrementAndGet())
            .sent(intCount.incrementAndGet())
            .remark(UUID.randomUUID().toString());
    }
}
