package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.WebhookDeliveryLog} entity.
 */
@Schema(description = "Webhook 投遞紀錄")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookDeliveryLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 64)
    @Schema(description = "投遞 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryId;

    @NotNull
    @Size(max = 128)
    @Schema(description = "事件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String event;

    @Schema(description = "Payload")
    @Lob
    private String payload;

    @NotNull
    @Schema(description = "投遞狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private DeliveryResult status;

    @Schema(description = "HTTP 回應碼")
    private Integer httpStatus;

    @Min(value = 0)
    @Schema(description = "嘗試次數")
    private Integer attempts;

    @Size(max = 1024)
    @Schema(description = "最後錯誤訊息")
    private String lastError;

    @Schema(description = "投遞完成時間")
    private Instant deliveredAt;

    @NotNull
    @Schema(description = "Webhook 端點設定")
    private WebhookEndpointDTO webhookEndpoint;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public DeliveryResult getStatus() {
        return status;
    }

    public void setStatus(DeliveryResult status) {
        this.status = status;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public WebhookEndpointDTO getWebhookEndpoint() {
        return webhookEndpoint;
    }

    public void setWebhookEndpoint(WebhookEndpointDTO webhookEndpoint) {
        this.webhookEndpoint = webhookEndpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookDeliveryLogDTO)) {
            return false;
        }

        WebhookDeliveryLogDTO webhookDeliveryLogDTO = (WebhookDeliveryLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, webhookDeliveryLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookDeliveryLogDTO{" +
            "id=" + getId() +
            ", deliveryId='" + getDeliveryId() + "'" +
            ", event='" + getEvent() + "'" +
            ", payload='" + getPayload() + "'" +
            ", status='" + getStatus() + "'" +
            ", httpStatus=" + getHttpStatus() +
            ", attempts=" + getAttempts() +
            ", lastError='" + getLastError() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            ", webhookEndpoint=" + getWebhookEndpoint() +
            "}";
    }
}
