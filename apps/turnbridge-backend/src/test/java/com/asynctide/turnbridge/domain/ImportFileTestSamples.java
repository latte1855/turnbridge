package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ImportFileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ImportFile getImportFileSample1() {
        return new ImportFile()
            .id(1L)
            .originalFilename("originalFilename1")
            .sha256("sha2561")
            .totalCount(1)
            .successCount(1)
            .errorCount(1)
            .legacyType("legacyType1");
    }

    public static ImportFile getImportFileSample2() {
        return new ImportFile()
            .id(2L)
            .originalFilename("originalFilename2")
            .sha256("sha2562")
            .totalCount(2)
            .successCount(2)
            .errorCount(2)
            .legacyType("legacyType2");
    }

    public static ImportFile getImportFileRandomSampleGenerator() {
        return new ImportFile()
            .id(longCount.incrementAndGet())
            .originalFilename(UUID.randomUUID().toString())
            .sha256(UUID.randomUUID().toString())
            .totalCount(intCount.incrementAndGet())
            .successCount(intCount.incrementAndGet())
            .errorCount(intCount.incrementAndGet())
            .legacyType(UUID.randomUUID().toString());
    }
}
