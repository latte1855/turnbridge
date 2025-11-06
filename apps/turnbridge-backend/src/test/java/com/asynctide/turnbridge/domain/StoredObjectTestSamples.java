package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StoredObjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StoredObject getStoredObjectSample1() {
        return new StoredObject()
            .id(1L)
            .bucket("bucket1")
            .objectKey("objectKey1")
            .mediaType("mediaType1")
            .contentLength(1L)
            .sha256("sha2561")
            .filename("filename1")
            .storageClass("storageClass1")
            .encryption("encryption1");
    }

    public static StoredObject getStoredObjectSample2() {
        return new StoredObject()
            .id(2L)
            .bucket("bucket2")
            .objectKey("objectKey2")
            .mediaType("mediaType2")
            .contentLength(2L)
            .sha256("sha2562")
            .filename("filename2")
            .storageClass("storageClass2")
            .encryption("encryption2");
    }

    public static StoredObject getStoredObjectRandomSampleGenerator() {
        return new StoredObject()
            .id(longCount.incrementAndGet())
            .bucket(UUID.randomUUID().toString())
            .objectKey(UUID.randomUUID().toString())
            .mediaType(UUID.randomUUID().toString())
            .contentLength(longCount.incrementAndGet())
            .sha256(UUID.randomUUID().toString())
            .filename(UUID.randomUUID().toString())
            .storageClass(UUID.randomUUID().toString())
            .encryption(UUID.randomUUID().toString());
    }
}
