package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.WebhookDeliveryLog} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.WebhookDeliveryLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /webhook-delivery-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookDeliveryLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DeliveryResult
     */
    public static class DeliveryResultFilter extends Filter<DeliveryResult> {

        public DeliveryResultFilter() {}

        public DeliveryResultFilter(DeliveryResultFilter filter) {
            super(filter);
        }

        @Override
        public DeliveryResultFilter copy() {
            return new DeliveryResultFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter deliveryId;

    private StringFilter event;

    private DeliveryResultFilter status;

    private IntegerFilter httpStatus;

    private IntegerFilter attempts;

    private StringFilter lastError;

    private InstantFilter deliveredAt;

    private InstantFilter nextAttemptAt;

    private InstantFilter lockedAt;

    private StringFilter dlqReason;

    private LongFilter webhookEndpointId;

    private Boolean distinct;

    public WebhookDeliveryLogCriteria() {}

    public WebhookDeliveryLogCriteria(WebhookDeliveryLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.deliveryId = other.optionalDeliveryId().map(StringFilter::copy).orElse(null);
        this.event = other.optionalEvent().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DeliveryResultFilter::copy).orElse(null);
        this.httpStatus = other.optionalHttpStatus().map(IntegerFilter::copy).orElse(null);
        this.attempts = other.optionalAttempts().map(IntegerFilter::copy).orElse(null);
        this.lastError = other.optionalLastError().map(StringFilter::copy).orElse(null);
        this.deliveredAt = other.optionalDeliveredAt().map(InstantFilter::copy).orElse(null);
        this.nextAttemptAt = other.optionalNextAttemptAt().map(InstantFilter::copy).orElse(null);
        this.lockedAt = other.optionalLockedAt().map(InstantFilter::copy).orElse(null);
        this.dlqReason = other.optionalDlqReason().map(StringFilter::copy).orElse(null);
        this.webhookEndpointId = other.optionalWebhookEndpointId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WebhookDeliveryLogCriteria copy() {
        return new WebhookDeliveryLogCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDeliveryId() {
        return deliveryId;
    }

    public Optional<StringFilter> optionalDeliveryId() {
        return Optional.ofNullable(deliveryId);
    }

    public StringFilter deliveryId() {
        if (deliveryId == null) {
            setDeliveryId(new StringFilter());
        }
        return deliveryId;
    }

    public void setDeliveryId(StringFilter deliveryId) {
        this.deliveryId = deliveryId;
    }

    public StringFilter getEvent() {
        return event;
    }

    public Optional<StringFilter> optionalEvent() {
        return Optional.ofNullable(event);
    }

    public StringFilter event() {
        if (event == null) {
            setEvent(new StringFilter());
        }
        return event;
    }

    public void setEvent(StringFilter event) {
        this.event = event;
    }

    public DeliveryResultFilter getStatus() {
        return status;
    }

    public Optional<DeliveryResultFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public DeliveryResultFilter status() {
        if (status == null) {
            setStatus(new DeliveryResultFilter());
        }
        return status;
    }

    public void setStatus(DeliveryResultFilter status) {
        this.status = status;
    }

    public IntegerFilter getHttpStatus() {
        return httpStatus;
    }

    public Optional<IntegerFilter> optionalHttpStatus() {
        return Optional.ofNullable(httpStatus);
    }

    public IntegerFilter httpStatus() {
        if (httpStatus == null) {
            setHttpStatus(new IntegerFilter());
        }
        return httpStatus;
    }

    public void setHttpStatus(IntegerFilter httpStatus) {
        this.httpStatus = httpStatus;
    }

    public IntegerFilter getAttempts() {
        return attempts;
    }

    public Optional<IntegerFilter> optionalAttempts() {
        return Optional.ofNullable(attempts);
    }

    public IntegerFilter attempts() {
        if (attempts == null) {
            setAttempts(new IntegerFilter());
        }
        return attempts;
    }

    public void setAttempts(IntegerFilter attempts) {
        this.attempts = attempts;
    }

    public StringFilter getLastError() {
        return lastError;
    }

    public Optional<StringFilter> optionalLastError() {
        return Optional.ofNullable(lastError);
    }

    public StringFilter lastError() {
        if (lastError == null) {
            setLastError(new StringFilter());
        }
        return lastError;
    }

    public void setLastError(StringFilter lastError) {
        this.lastError = lastError;
    }

    public InstantFilter getDeliveredAt() {
        return deliveredAt;
    }

    public Optional<InstantFilter> optionalDeliveredAt() {
        return Optional.ofNullable(deliveredAt);
    }

    public InstantFilter deliveredAt() {
        if (deliveredAt == null) {
            setDeliveredAt(new InstantFilter());
        }
        return deliveredAt;
    }

    public void setDeliveredAt(InstantFilter deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public InstantFilter getNextAttemptAt() {
        return nextAttemptAt;
    }

    public Optional<InstantFilter> optionalNextAttemptAt() {
        return Optional.ofNullable(nextAttemptAt);
    }

    public InstantFilter nextAttemptAt() {
        if (nextAttemptAt == null) {
            setNextAttemptAt(new InstantFilter());
        }
        return nextAttemptAt;
    }

    public void setNextAttemptAt(InstantFilter nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public InstantFilter getLockedAt() {
        return lockedAt;
    }

    public Optional<InstantFilter> optionalLockedAt() {
        return Optional.ofNullable(lockedAt);
    }

    public InstantFilter lockedAt() {
        if (lockedAt == null) {
            setLockedAt(new InstantFilter());
        }
        return lockedAt;
    }

    public void setLockedAt(InstantFilter lockedAt) {
        this.lockedAt = lockedAt;
    }

    public StringFilter getDlqReason() {
        return dlqReason;
    }

    public Optional<StringFilter> optionalDlqReason() {
        return Optional.ofNullable(dlqReason);
    }

    public StringFilter dlqReason() {
        if (dlqReason == null) {
            setDlqReason(new StringFilter());
        }
        return dlqReason;
    }

    public void setDlqReason(StringFilter dlqReason) {
        this.dlqReason = dlqReason;
    }

    public LongFilter getWebhookEndpointId() {
        return webhookEndpointId;
    }

    public Optional<LongFilter> optionalWebhookEndpointId() {
        return Optional.ofNullable(webhookEndpointId);
    }

    public LongFilter webhookEndpointId() {
        if (webhookEndpointId == null) {
            setWebhookEndpointId(new LongFilter());
        }
        return webhookEndpointId;
    }

    public void setWebhookEndpointId(LongFilter webhookEndpointId) {
        this.webhookEndpointId = webhookEndpointId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WebhookDeliveryLogCriteria that = (WebhookDeliveryLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deliveryId, that.deliveryId) &&
            Objects.equals(event, that.event) &&
            Objects.equals(status, that.status) &&
            Objects.equals(httpStatus, that.httpStatus) &&
            Objects.equals(attempts, that.attempts) &&
            Objects.equals(lastError, that.lastError) &&
            Objects.equals(deliveredAt, that.deliveredAt) &&
            Objects.equals(nextAttemptAt, that.nextAttemptAt) &&
            Objects.equals(lockedAt, that.lockedAt) &&
            Objects.equals(dlqReason, that.dlqReason) &&
            Objects.equals(webhookEndpointId, that.webhookEndpointId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            deliveryId,
            event,
            status,
            httpStatus,
            attempts,
            lastError,
            deliveredAt,
            nextAttemptAt,
            lockedAt,
            dlqReason,
            webhookEndpointId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookDeliveryLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDeliveryId().map(f -> "deliveryId=" + f + ", ").orElse("") +
            optionalEvent().map(f -> "event=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalHttpStatus().map(f -> "httpStatus=" + f + ", ").orElse("") +
            optionalAttempts().map(f -> "attempts=" + f + ", ").orElse("") +
            optionalLastError().map(f -> "lastError=" + f + ", ").orElse("") +
            optionalDeliveredAt().map(f -> "deliveredAt=" + f + ", ").orElse("") +
            optionalNextAttemptAt().map(f -> "nextAttemptAt=" + f + ", ").orElse("") +
            optionalLockedAt().map(f -> "lockedAt=" + f + ", ").orElse("") +
            optionalDlqReason().map(f -> "dlqReason=" + f + ", ").orElse("") +
            optionalWebhookEndpointId().map(f -> "webhookEndpointId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
