package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.TurnkeyMessage} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.TurnkeyMessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /turnkey-messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurnkeyMessageCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MessageFamily
     */
    public static class MessageFamilyFilter extends Filter<MessageFamily> {

        public MessageFamilyFilter() {}

        public MessageFamilyFilter(MessageFamilyFilter filter) {
            super(filter);
        }

        @Override
        public MessageFamilyFilter copy() {
            return new MessageFamilyFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter messageId;

    private MessageFamilyFilter messageFamily;

    private StringFilter type;

    private StringFilter code;

    private StringFilter payloadPath;

    private InstantFilter receivedAt;

    private LongFilter invoiceId;

    private Boolean distinct;

    public TurnkeyMessageCriteria() {}

    public TurnkeyMessageCriteria(TurnkeyMessageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.messageId = other.optionalMessageId().map(StringFilter::copy).orElse(null);
        this.messageFamily = other.optionalMessageFamily().map(MessageFamilyFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.payloadPath = other.optionalPayloadPath().map(StringFilter::copy).orElse(null);
        this.receivedAt = other.optionalReceivedAt().map(InstantFilter::copy).orElse(null);
        this.invoiceId = other.optionalInvoiceId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TurnkeyMessageCriteria copy() {
        return new TurnkeyMessageCriteria(this);
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

    public StringFilter getMessageId() {
        return messageId;
    }

    public Optional<StringFilter> optionalMessageId() {
        return Optional.ofNullable(messageId);
    }

    public StringFilter messageId() {
        if (messageId == null) {
            setMessageId(new StringFilter());
        }
        return messageId;
    }

    public void setMessageId(StringFilter messageId) {
        this.messageId = messageId;
    }

    public MessageFamilyFilter getMessageFamily() {
        return messageFamily;
    }

    public Optional<MessageFamilyFilter> optionalMessageFamily() {
        return Optional.ofNullable(messageFamily);
    }

    public MessageFamilyFilter messageFamily() {
        if (messageFamily == null) {
            setMessageFamily(new MessageFamilyFilter());
        }
        return messageFamily;
    }

    public void setMessageFamily(MessageFamilyFilter messageFamily) {
        this.messageFamily = messageFamily;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getPayloadPath() {
        return payloadPath;
    }

    public Optional<StringFilter> optionalPayloadPath() {
        return Optional.ofNullable(payloadPath);
    }

    public StringFilter payloadPath() {
        if (payloadPath == null) {
            setPayloadPath(new StringFilter());
        }
        return payloadPath;
    }

    public void setPayloadPath(StringFilter payloadPath) {
        this.payloadPath = payloadPath;
    }

    public InstantFilter getReceivedAt() {
        return receivedAt;
    }

    public Optional<InstantFilter> optionalReceivedAt() {
        return Optional.ofNullable(receivedAt);
    }

    public InstantFilter receivedAt() {
        if (receivedAt == null) {
            setReceivedAt(new InstantFilter());
        }
        return receivedAt;
    }

    public void setReceivedAt(InstantFilter receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LongFilter getInvoiceId() {
        return invoiceId;
    }

    public Optional<LongFilter> optionalInvoiceId() {
        return Optional.ofNullable(invoiceId);
    }

    public LongFilter invoiceId() {
        if (invoiceId == null) {
            setInvoiceId(new LongFilter());
        }
        return invoiceId;
    }

    public void setInvoiceId(LongFilter invoiceId) {
        this.invoiceId = invoiceId;
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
        final TurnkeyMessageCriteria that = (TurnkeyMessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(messageId, that.messageId) &&
            Objects.equals(messageFamily, that.messageFamily) &&
            Objects.equals(type, that.type) &&
            Objects.equals(code, that.code) &&
            Objects.equals(payloadPath, that.payloadPath) &&
            Objects.equals(receivedAt, that.receivedAt) &&
            Objects.equals(invoiceId, that.invoiceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, messageId, messageFamily, type, code, payloadPath, receivedAt, invoiceId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurnkeyMessageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMessageId().map(f -> "messageId=" + f + ", ").orElse("") +
            optionalMessageFamily().map(f -> "messageFamily=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalPayloadPath().map(f -> "payloadPath=" + f + ", ").orElse("") +
            optionalReceivedAt().map(f -> "receivedAt=" + f + ", ").orElse("") +
            optionalInvoiceId().map(f -> "invoiceId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
