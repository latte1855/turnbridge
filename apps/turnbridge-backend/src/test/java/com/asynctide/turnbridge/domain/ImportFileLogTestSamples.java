package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ImportFileLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ImportFileLog getImportFileLogSample1() {
        return new ImportFileLog().id(1L).eventCode("eventCode1").level("level1").message("message1");
    }

    public static ImportFileLog getImportFileLogSample2() {
        return new ImportFileLog().id(2L).eventCode("eventCode2").level("level2").message("message2");
    }

    public static ImportFileLog getImportFileLogRandomSampleGenerator() {
        return new ImportFileLog()
            .id(longCount.incrementAndGet())
            .eventCode(UUID.randomUUID().toString())
            .level(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString());
    }
}
