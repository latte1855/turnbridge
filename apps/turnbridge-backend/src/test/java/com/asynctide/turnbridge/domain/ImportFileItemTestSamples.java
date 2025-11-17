package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ImportFileItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ImportFileItem getImportFileItemSample1() {
        return new ImportFileItem()
            .id(1L)
            .lineIndex(1)
            .rawHash("rawHash1")
            .sourceFamily("sourceFamily1")
            .normalizedFamily("normalizedFamily1")
            .errorCode("errorCode1")
            .errorMessage("errorMessage1");
    }

    public static ImportFileItem getImportFileItemSample2() {
        return new ImportFileItem()
            .id(2L)
            .lineIndex(2)
            .rawHash("rawHash2")
            .sourceFamily("sourceFamily2")
            .normalizedFamily("normalizedFamily2")
            .errorCode("errorCode2")
            .errorMessage("errorMessage2");
    }

    public static ImportFileItem getImportFileItemRandomSampleGenerator() {
        return new ImportFileItem()
            .id(longCount.incrementAndGet())
            .lineIndex(intCount.incrementAndGet())
            .rawHash(UUID.randomUUID().toString())
            .sourceFamily(UUID.randomUUID().toString())
            .normalizedFamily(UUID.randomUUID().toString())
            .errorCode(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString());
    }
}
