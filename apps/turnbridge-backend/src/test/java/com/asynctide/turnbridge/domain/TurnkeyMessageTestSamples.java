package com.asynctide.turnbridge.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TurnkeyMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TurnkeyMessage getTurnkeyMessageSample1() {
        return new TurnkeyMessage().id(1L).messageId("messageId1").type("type1").code("code1").payloadPath("payloadPath1");
    }

    public static TurnkeyMessage getTurnkeyMessageSample2() {
        return new TurnkeyMessage().id(2L).messageId("messageId2").type("type2").code("code2").payloadPath("payloadPath2");
    }

    public static TurnkeyMessage getTurnkeyMessageRandomSampleGenerator() {
        return new TurnkeyMessage()
            .id(longCount.incrementAndGet())
            .messageId(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .payloadPath(UUID.randomUUID().toString());
    }
}
