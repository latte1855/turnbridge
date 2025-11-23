package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class WebhookDashboardDTOs {

    @Schema(description = "Turnkey TB summary item")
    public static class TbSummaryDTO {

        public String tbCode;
        public String tbCategory;
        public long count;
        public String sampleInvoice;
        public Long sampleImportId;
        public Instant lastUpdated;
    }

    @Schema(description = "Webhook DLQ row")
    public static class WebhookDlqDTO {

        public Long id;
        public String deliveryId;
        public String event;
        public String status;
        public Integer attempts;
        public String httpStatus;
        public String lastError;
        public String dlqReason;
        public Instant nextAttemptAt;
        public String webhookEndpointName;
        public String tbCode;
        public String tbCategory;
        public String invoiceNo;
        public Long importId;
        public String platformMessage;
    }

    private WebhookDashboardDTOs() {}
}
