package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrackRangeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrackRange getTrackRangeSample1() {
        return new TrackRange()
            .id(1L)
            .sellerId("sellerId1")
            .period("period1")
            .prefix("prefix1")
            .startNo(1L)
            .endNo(1L)
            .currentNo(1L)
            .version(1)
            .lockOwner("lockOwner1");
    }

    public static TrackRange getTrackRangeSample2() {
        return new TrackRange()
            .id(2L)
            .sellerId("sellerId2")
            .period("period2")
            .prefix("prefix2")
            .startNo(2L)
            .endNo(2L)
            .currentNo(2L)
            .version(2)
            .lockOwner("lockOwner2");
    }

    public static TrackRange getTrackRangeRandomSampleGenerator() {
        return new TrackRange()
            .id(longCount.incrementAndGet())
            .sellerId(UUID.randomUUID().toString())
            .period(UUID.randomUUID().toString())
            .prefix(UUID.randomUUID().toString())
            .startNo(longCount.incrementAndGet())
            .endNo(longCount.incrementAndGet())
            .currentNo(longCount.incrementAndGet())
            .version(intCount.incrementAndGet())
            .lockOwner(UUID.randomUUID().toString());
    }
}
