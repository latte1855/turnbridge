package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Webhook 投遞紀錄
 */
@Entity
@Table(name = "webhook_delivery_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookDeliveryLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 投遞 ID
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "delivery_id", length = 64, nullable = false)
    private String deliveryId;

    /**
     * 事件
     */
    @NotNull
    @Size(max = 128)
    @Column(name = "event", length = 128, nullable = false)
    private String event;

    /**
     * Payload
     */
    @Column(name = "payload")
    private String payload;

    /**
     * 投遞狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryResult status;

    /**
     * HTTP 回應碼
     */
    @Column(name = "http_status")
    private Integer httpStatus;

    /**
     * 嘗試次數
     */
    @Min(value = 0)
    @Column(name = "attempts")
    private Integer attempts;

    /**
     * 最後錯誤訊息
     */
    @Size(max = 1024)
    @Column(name = "last_error", length = 1024)
    private String lastError;

    /**
     * 投遞完成時間
     */
    @Column(name = "delivered_at")
    private Instant deliveredAt;

    /**
     * 下一次投遞時間（排程用）
     */
    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    /**
     * 最長鎖定時間，避免重複 worker 併發
     */
    @Column(name = "locked_at")
    private Instant lockedAt;

    /**
     * DLQ 原因（超過上限後紀錄）
     */
    @Size(max = 1024)
    @Column(name = "dlq_reason", length = 1024)
    private String dlqReason;

    /**
     * Webhook 端點設定
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private WebhookEndpoint webhookEndpoint;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WebhookDeliveryLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryId() {
        return this.deliveryId;
    }

    public WebhookDeliveryLog deliveryId(String deliveryId) {
        this.setDeliveryId(deliveryId);
        return this;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getEvent() {
        return this.event;
    }

    public WebhookDeliveryLog event(String event) {
        this.setEvent(event);
        return this;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPayload() {
        return this.payload;
    }

    public WebhookDeliveryLog payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public DeliveryResult getStatus() {
        return this.status;
    }

    public WebhookDeliveryLog status(DeliveryResult status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DeliveryResult status) {
        this.status = status;
    }

    public Integer getHttpStatus() {
        return this.httpStatus;
    }

    public WebhookDeliveryLog httpStatus(Integer httpStatus) {
        this.setHttpStatus(httpStatus);
        return this;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Integer getAttempts() {
        return this.attempts;
    }

    public WebhookDeliveryLog attempts(Integer attempts) {
        this.setAttempts(attempts);
        return this;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getLastError() {
        return this.lastError;
    }

    public WebhookDeliveryLog lastError(String lastError) {
        this.setLastError(lastError);
        return this;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Instant getDeliveredAt() {
        return this.deliveredAt;
    }

    public WebhookDeliveryLog deliveredAt(Instant deliveredAt) {
        this.setDeliveredAt(deliveredAt);
        return this;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Instant getNextAttemptAt() {
        return this.nextAttemptAt;
    }

    public WebhookDeliveryLog nextAttemptAt(Instant nextAttemptAt) {
        this.setNextAttemptAt(nextAttemptAt);
        return this;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public Instant getLockedAt() {
        return this.lockedAt;
    }

    public WebhookDeliveryLog lockedAt(Instant lockedAt) {
        this.setLockedAt(lockedAt);
        return this;
    }

    public void setLockedAt(Instant lockedAt) {
        this.lockedAt = lockedAt;
    }

    public String getDlqReason() {
        return this.dlqReason;
    }

    public WebhookDeliveryLog dlqReason(String dlqReason) {
        this.setDlqReason(dlqReason);
        return this;
    }

    public void setDlqReason(String dlqReason) {
        this.dlqReason = dlqReason;
    }

    public WebhookEndpoint getWebhookEndpoint() {
        return this.webhookEndpoint;
    }

    public void setWebhookEndpoint(WebhookEndpoint webhookEndpoint) {
        this.webhookEndpoint = webhookEndpoint;
    }

    public WebhookDeliveryLog webhookEndpoint(WebhookEndpoint webhookEndpoint) {
        this.setWebhookEndpoint(webhookEndpoint);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookDeliveryLog)) {
            return false;
        }
        return getId() != null && getId().equals(((WebhookDeliveryLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookDeliveryLog{" +
            "id=" + getId() +
            ", deliveryId='" + getDeliveryId() + "'" +
            ", event='" + getEvent() + "'" +
            ", payload='" + getPayload() + "'" +
            ", status='" + getStatus() + "'" +
            ", httpStatus=" + getHttpStatus() +
            ", attempts=" + getAttempts() +
            ", lastError='" + getLastError() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            ", nextAttemptAt='" + getNextAttemptAt() + "'" +
            ", lockedAt='" + getLockedAt() + "'" +
            ", dlqReason='" + getDlqReason() + "'" +
            "}";
    }
}
