package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.TrackRangeStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.TrackRange} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.TrackRangeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /track-ranges?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackRangeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TrackRangeStatus
     */
    public static class TrackRangeStatusFilter extends Filter<TrackRangeStatus> {

        public TrackRangeStatusFilter() {}

        public TrackRangeStatusFilter(TrackRangeStatusFilter filter) {
            super(filter);
        }

        @Override
        public TrackRangeStatusFilter copy() {
            return new TrackRangeStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sellerId;

    private StringFilter period;

    private StringFilter prefix;

    private LongFilter startNo;

    private LongFilter endNo;

    private LongFilter currentNo;

    private TrackRangeStatusFilter status;

    private IntegerFilter version;

    private StringFilter lockOwner;

    private InstantFilter lockAt;

    private Boolean distinct;

    public TrackRangeCriteria() {}

    public TrackRangeCriteria(TrackRangeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sellerId = other.optionalSellerId().map(StringFilter::copy).orElse(null);
        this.period = other.optionalPeriod().map(StringFilter::copy).orElse(null);
        this.prefix = other.optionalPrefix().map(StringFilter::copy).orElse(null);
        this.startNo = other.optionalStartNo().map(LongFilter::copy).orElse(null);
        this.endNo = other.optionalEndNo().map(LongFilter::copy).orElse(null);
        this.currentNo = other.optionalCurrentNo().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TrackRangeStatusFilter::copy).orElse(null);
        this.version = other.optionalVersion().map(IntegerFilter::copy).orElse(null);
        this.lockOwner = other.optionalLockOwner().map(StringFilter::copy).orElse(null);
        this.lockAt = other.optionalLockAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrackRangeCriteria copy() {
        return new TrackRangeCriteria(this);
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

    public StringFilter getSellerId() {
        return sellerId;
    }

    public Optional<StringFilter> optionalSellerId() {
        return Optional.ofNullable(sellerId);
    }

    public StringFilter sellerId() {
        if (sellerId == null) {
            setSellerId(new StringFilter());
        }
        return sellerId;
    }

    public void setSellerId(StringFilter sellerId) {
        this.sellerId = sellerId;
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

    public StringFilter getPrefix() {
        return prefix;
    }

    public Optional<StringFilter> optionalPrefix() {
        return Optional.ofNullable(prefix);
    }

    public StringFilter prefix() {
        if (prefix == null) {
            setPrefix(new StringFilter());
        }
        return prefix;
    }

    public void setPrefix(StringFilter prefix) {
        this.prefix = prefix;
    }

    public LongFilter getStartNo() {
        return startNo;
    }

    public Optional<LongFilter> optionalStartNo() {
        return Optional.ofNullable(startNo);
    }

    public LongFilter startNo() {
        if (startNo == null) {
            setStartNo(new LongFilter());
        }
        return startNo;
    }

    public void setStartNo(LongFilter startNo) {
        this.startNo = startNo;
    }

    public LongFilter getEndNo() {
        return endNo;
    }

    public Optional<LongFilter> optionalEndNo() {
        return Optional.ofNullable(endNo);
    }

    public LongFilter endNo() {
        if (endNo == null) {
            setEndNo(new LongFilter());
        }
        return endNo;
    }

    public void setEndNo(LongFilter endNo) {
        this.endNo = endNo;
    }

    public LongFilter getCurrentNo() {
        return currentNo;
    }

    public Optional<LongFilter> optionalCurrentNo() {
        return Optional.ofNullable(currentNo);
    }

    public LongFilter currentNo() {
        if (currentNo == null) {
            setCurrentNo(new LongFilter());
        }
        return currentNo;
    }

    public void setCurrentNo(LongFilter currentNo) {
        this.currentNo = currentNo;
    }

    public TrackRangeStatusFilter getStatus() {
        return status;
    }

    public Optional<TrackRangeStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TrackRangeStatusFilter status() {
        if (status == null) {
            setStatus(new TrackRangeStatusFilter());
        }
        return status;
    }

    public void setStatus(TrackRangeStatusFilter status) {
        this.status = status;
    }

    public IntegerFilter getVersion() {
        return version;
    }

    public Optional<IntegerFilter> optionalVersion() {
        return Optional.ofNullable(version);
    }

    public IntegerFilter version() {
        if (version == null) {
            setVersion(new IntegerFilter());
        }
        return version;
    }

    public void setVersion(IntegerFilter version) {
        this.version = version;
    }

    public StringFilter getLockOwner() {
        return lockOwner;
    }

    public Optional<StringFilter> optionalLockOwner() {
        return Optional.ofNullable(lockOwner);
    }

    public StringFilter lockOwner() {
        if (lockOwner == null) {
            setLockOwner(new StringFilter());
        }
        return lockOwner;
    }

    public void setLockOwner(StringFilter lockOwner) {
        this.lockOwner = lockOwner;
    }

    public InstantFilter getLockAt() {
        return lockAt;
    }

    public Optional<InstantFilter> optionalLockAt() {
        return Optional.ofNullable(lockAt);
    }

    public InstantFilter lockAt() {
        if (lockAt == null) {
            setLockAt(new InstantFilter());
        }
        return lockAt;
    }

    public void setLockAt(InstantFilter lockAt) {
        this.lockAt = lockAt;
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
        final TrackRangeCriteria that = (TrackRangeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sellerId, that.sellerId) &&
            Objects.equals(period, that.period) &&
            Objects.equals(prefix, that.prefix) &&
            Objects.equals(startNo, that.startNo) &&
            Objects.equals(endNo, that.endNo) &&
            Objects.equals(currentNo, that.currentNo) &&
            Objects.equals(status, that.status) &&
            Objects.equals(version, that.version) &&
            Objects.equals(lockOwner, that.lockOwner) &&
            Objects.equals(lockAt, that.lockAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sellerId, period, prefix, startNo, endNo, currentNo, status, version, lockOwner, lockAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackRangeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSellerId().map(f -> "sellerId=" + f + ", ").orElse("") +
            optionalPeriod().map(f -> "period=" + f + ", ").orElse("") +
            optionalPrefix().map(f -> "prefix=" + f + ", ").orElse("") +
            optionalStartNo().map(f -> "startNo=" + f + ", ").orElse("") +
            optionalEndNo().map(f -> "endNo=" + f + ", ").orElse("") +
            optionalCurrentNo().map(f -> "currentNo=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalVersion().map(f -> "version=" + f + ", ").orElse("") +
            optionalLockOwner().map(f -> "lockOwner=" + f + ", ").orElse("") +
            optionalLockAt().map(f -> "lockAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
