package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Invoice getInvoiceSample1() {
        return new Invoice()
            .id(1L)
            .invoiceNo("invoiceNo1")
            .buyerId("buyerId1")
            .buyerName("buyerName1")
            .sellerId("sellerId1")
            .sellerName("sellerName1")
            .taxType("taxType1")
            .legacyType("legacyType1")
            .tbCode("tbCode1")
            .tbCategory("tbCategory1")
            .tbRecommendedAction("tbRecommendedAction1")
            .tbSourceCode("tbSourceCode1")
            .tbSourceMessage("tbSourceMessage1")
            .tbResultCode("tbResultCode1");
    }

    public static Invoice getInvoiceSample2() {
        return new Invoice()
            .id(2L)
            .invoiceNo("invoiceNo2")
            .buyerId("buyerId2")
            .buyerName("buyerName2")
            .sellerId("sellerId2")
            .sellerName("sellerName2")
            .taxType("taxType2")
            .legacyType("legacyType2")
            .tbCode("tbCode2")
            .tbCategory("tbCategory2")
            .tbRecommendedAction("tbRecommendedAction2")
            .tbSourceCode("tbSourceCode2")
            .tbSourceMessage("tbSourceMessage2")
            .tbResultCode("tbResultCode2");
    }

    public static Invoice getInvoiceRandomSampleGenerator() {
        return new Invoice()
            .id(longCount.incrementAndGet())
            .invoiceNo(UUID.randomUUID().toString())
            .buyerId(UUID.randomUUID().toString())
            .buyerName(UUID.randomUUID().toString())
            .sellerId(UUID.randomUUID().toString())
            .sellerName(UUID.randomUUID().toString())
            .taxType(UUID.randomUUID().toString())
            .legacyType(UUID.randomUUID().toString())
            .tbCode(UUID.randomUUID().toString())
            .tbCategory(UUID.randomUUID().toString())
            .tbRecommendedAction(UUID.randomUUID().toString())
            .tbSourceCode(UUID.randomUUID().toString())
            .tbSourceMessage(UUID.randomUUID().toString())
            .tbResultCode(UUID.randomUUID().toString());
    }
}
