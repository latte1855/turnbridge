package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ImportFileLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ImportFileLog getImportFileLogSample1() {
        return new ImportFileLog()
            .id(1L)
            .lineIndex(1)
            .field("field1")
            .errorCode("errorCode1")
            .message("message1")
            .sourceFamily("sourceFamily1")
            .normalizedFamily("normalizedFamily1");
    }

    public static ImportFileLog getImportFileLogSample2() {
        return new ImportFileLog()
            .id(2L)
            .lineIndex(2)
            .field("field2")
            .errorCode("errorCode2")
            .message("message2")
            .sourceFamily("sourceFamily2")
            .normalizedFamily("normalizedFamily2");
    }

    public static ImportFileLog getImportFileLogRandomSampleGenerator() {
        return new ImportFileLog()
            .id(longCount.incrementAndGet())
            .lineIndex(intCount.incrementAndGet())
            .field(UUID.randomUUID().toString())
            .errorCode(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .sourceFamily(UUID.randomUUID().toString())
            .normalizedFamily(UUID.randomUUID().toString());
    }
}
