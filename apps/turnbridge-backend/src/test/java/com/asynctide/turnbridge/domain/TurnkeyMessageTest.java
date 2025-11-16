package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.InvoiceTestSamples.*;
import static com.asynctide.turnbridge.domain.TurnkeyMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TurnkeyMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TurnkeyMessage.class);
        TurnkeyMessage turnkeyMessage1 = getTurnkeyMessageSample1();
        TurnkeyMessage turnkeyMessage2 = new TurnkeyMessage();
        assertThat(turnkeyMessage1).isNotEqualTo(turnkeyMessage2);

        turnkeyMessage2.setId(turnkeyMessage1.getId());
        assertThat(turnkeyMessage1).isEqualTo(turnkeyMessage2);

        turnkeyMessage2 = getTurnkeyMessageSample2();
        assertThat(turnkeyMessage1).isNotEqualTo(turnkeyMessage2);
    }

    @Test
    void invoiceTest() {
        TurnkeyMessage turnkeyMessage = getTurnkeyMessageRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        turnkeyMessage.setInvoice(invoiceBack);
        assertThat(turnkeyMessage.getInvoice()).isEqualTo(invoiceBack);

        turnkeyMessage.invoice(null);
        assertThat(turnkeyMessage.getInvoice()).isNull();
    }
}
