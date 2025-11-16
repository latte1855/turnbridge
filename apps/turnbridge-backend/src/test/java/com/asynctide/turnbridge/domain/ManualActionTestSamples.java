package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ManualActionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ManualAction getManualActionSample1() {
        return new ManualAction().id(1L).reason("reason1").requestedBy("requestedBy1").approvedBy("approvedBy1");
    }

    public static ManualAction getManualActionSample2() {
        return new ManualAction().id(2L).reason("reason2").requestedBy("requestedBy2").approvedBy("approvedBy2");
    }

    public static ManualAction getManualActionRandomSampleGenerator() {
        return new ManualAction()
            .id(longCount.incrementAndGet())
            .reason(UUID.randomUUID().toString())
            .requestedBy(UUID.randomUUID().toString())
            .approvedBy(UUID.randomUUID().toString());
    }
}
