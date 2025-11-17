package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ImportFileItemErrorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ImportFileItemError getImportFileItemErrorSample1() {
        return new ImportFileItemError()
            .id(1L)
            .columnIndex(1)
            .fieldName("fieldName1")
            .errorCode("errorCode1")
            .message("message1")
            .severity("severity1");
    }

    public static ImportFileItemError getImportFileItemErrorSample2() {
        return new ImportFileItemError()
            .id(2L)
            .columnIndex(2)
            .fieldName("fieldName2")
            .errorCode("errorCode2")
            .message("message2")
            .severity("severity2");
    }

    public static ImportFileItemError getImportFileItemErrorRandomSampleGenerator() {
        return new ImportFileItemError()
            .id(longCount.incrementAndGet())
            .columnIndex(intCount.incrementAndGet())
            .fieldName(UUID.randomUUID().toString())
            .errorCode(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .severity(UUID.randomUUID().toString());
    }
}
