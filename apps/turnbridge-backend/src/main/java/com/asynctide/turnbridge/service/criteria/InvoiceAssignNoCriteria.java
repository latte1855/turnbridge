package com.asynctide.turnbridge.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.InvoiceAssignNo} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.InvoiceAssignNoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /invoice-assign-nos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceAssignNoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter track;

    private StringFilter period;

    private StringFilter fromNo;

    private StringFilter toNo;

    private IntegerFilter usedCount;

    private IntegerFilter rollSize;

    private StringFilter status;

    private LongFilter tenantId;

    private Boolean distinct;

    public InvoiceAssignNoCriteria() {}

    public InvoiceAssignNoCriteria(InvoiceAssignNoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.track = other.optionalTrack().map(StringFilter::copy).orElse(null);
        this.period = other.optionalPeriod().map(StringFilter::copy).orElse(null);
        this.fromNo = other.optionalFromNo().map(StringFilter::copy).orElse(null);
        this.toNo = other.optionalToNo().map(StringFilter::copy).orElse(null);
        this.usedCount = other.optionalUsedCount().map(IntegerFilter::copy).orElse(null);
        this.rollSize = other.optionalRollSize().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InvoiceAssignNoCriteria copy() {
        return new InvoiceAssignNoCriteria(this);
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

    public StringFilter getTrack() {
        return track;
    }

    public Optional<StringFilter> optionalTrack() {
        return Optional.ofNullable(track);
    }

    public StringFilter track() {
        if (track == null) {
            setTrack(new StringFilter());
        }
        return track;
    }

    public void setTrack(StringFilter track) {
        this.track = track;
    }

    public StringFilter getPeriod() {
        return period;
    }

    public Optional<StringFilter> optionalPeriod() {
        return Optional.ofNullable(period);
    }

    public StringFilter period() {
        if (period == null) {
            setPeriod(new StringFilter());
        }
        return period;
    }

    public void setPeriod(StringFilter period) {
        this.period = period;
    }

    public StringFilter getFromNo() {
        return fromNo;
    }

    public Optional<StringFilter> optionalFromNo() {
        return Optional.ofNullable(fromNo);
    }

    public StringFilter fromNo() {
        if (fromNo == null) {
            setFromNo(new StringFilter());
        }
        return fromNo;
    }

    public void setFromNo(StringFilter fromNo) {
        this.fromNo = fromNo;
    }

    public StringFilter getToNo() {
        return toNo;
    }

    public Optional<StringFilter> optionalToNo() {
        return Optional.ofNullable(toNo);
    }

    public StringFilter toNo() {
        if (toNo == null) {
            setToNo(new StringFilter());
        }
        return toNo;
    }

    public void setToNo(StringFilter toNo) {
        this.toNo = toNo;
    }

    public IntegerFilter getUsedCount() {
        return usedCount;
    }

    public Optional<IntegerFilter> optionalUsedCount() {
        return Optional.ofNullable(usedCount);
    }

    public IntegerFilter usedCount() {
        if (usedCount == null) {
            setUsedCount(new IntegerFilter());
        }
        return usedCount;
    }

    public void setUsedCount(IntegerFilter usedCount) {
        this.usedCount = usedCount;
    }

    public IntegerFilter getRollSize() {
        return rollSize;
    }

    public Optional<IntegerFilter> optionalRollSize() {
        return Optional.ofNullable(rollSize);
    }

    public IntegerFilter rollSize() {
        if (rollSize == null) {
            setRollSize(new IntegerFilter());
        }
        return rollSize;
    }

    public void setRollSize(IntegerFilter rollSize) {
        this.rollSize = rollSize;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
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
        final InvoiceAssignNoCriteria that = (InvoiceAssignNoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(track, that.track) &&
            Objects.equals(period, that.period) &&
            Objects.equals(fromNo, that.fromNo) &&
            Objects.equals(toNo, that.toNo) &&
            Objects.equals(usedCount, that.usedCount) &&
            Objects.equals(rollSize, that.rollSize) &&
            Objects.equals(status, that.status) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, track, period, fromNo, toNo, usedCount, rollSize, status, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceAssignNoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTrack().map(f -> "track=" + f + ", ").orElse("") +
            optionalPeriod().map(f -> "period=" + f + ", ").orElse("") +
            optionalFromNo().map(f -> "fromNo=" + f + ", ").orElse("") +
            optionalToNo().map(f -> "toNo=" + f + ", ").orElse("") +
            optionalUsedCount().map(f -> "usedCount=" + f + ", ").orElse("") +
            optionalRollSize().map(f -> "rollSize=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
