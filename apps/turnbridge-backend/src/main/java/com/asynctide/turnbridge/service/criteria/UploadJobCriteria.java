package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.UploadJob} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.UploadJobResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /upload-jobs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadJobCriteria implements Serializable, Criteria {

    /**
     * Class for filtering UploadJobStatus
     */
    public static class UploadJobStatusFilter extends Filter<UploadJobStatus> {

        public UploadJobStatusFilter() {}

        public UploadJobStatusFilter(UploadJobStatusFilter filter) {
            super(filter);
        }

        @Override
        public UploadJobStatusFilter copy() {
            return new UploadJobStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter jobId;

    private StringFilter sellerId;

    private StringFilter sellerName;

    private StringFilter period;

    private StringFilter profile;

    private StringFilter sourceFilename;

    private StringFilter sourceMediaType;

    private UploadJobStatusFilter status;

    private IntegerFilter total;

    private IntegerFilter accepted;

    private IntegerFilter failed;

    private IntegerFilter sent;

    private StringFilter remark;

    private LongFilter itemsId;

    private LongFilter originalFileId;

    private LongFilter resultFileId;

    private Boolean distinct;

    public UploadJobCriteria() {}

    public UploadJobCriteria(UploadJobCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.jobId = other.optionalJobId().map(StringFilter::copy).orElse(null);
        this.sellerId = other.optionalSellerId().map(StringFilter::copy).orElse(null);
        this.sellerName = other.optionalSellerName().map(StringFilter::copy).orElse(null);
        this.period = other.optionalPeriod().map(StringFilter::copy).orElse(null);
        this.profile = other.optionalProfile().map(StringFilter::copy).orElse(null);
        this.sourceFilename = other.optionalSourceFilename().map(StringFilter::copy).orElse(null);
        this.sourceMediaType = other.optionalSourceMediaType().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(UploadJobStatusFilter::copy).orElse(null);
        this.total = other.optionalTotal().map(IntegerFilter::copy).orElse(null);
        this.accepted = other.optionalAccepted().map(IntegerFilter::copy).orElse(null);
        this.failed = other.optionalFailed().map(IntegerFilter::copy).orElse(null);
        this.sent = other.optionalSent().map(IntegerFilter::copy).orElse(null);
        this.remark = other.optionalRemark().map(StringFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.originalFileId = other.optionalOriginalFileId().map(LongFilter::copy).orElse(null);
        this.resultFileId = other.optionalResultFileId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UploadJobCriteria copy() {
        return new UploadJobCriteria(this);
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

    public StringFilter getJobId() {
        return jobId;
    }

    public Optional<StringFilter> optionalJobId() {
        return Optional.ofNullable(jobId);
    }

    public StringFilter jobId() {
        if (jobId == null) {
            setJobId(new StringFilter());
        }
        return jobId;
    }

    public void setJobId(StringFilter jobId) {
        this.jobId = jobId;
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

    public StringFilter getSellerName() {
        return sellerName;
    }

    public Optional<StringFilter> optionalSellerName() {
        return Optional.ofNullable(sellerName);
    }

    public StringFilter sellerName() {
        if (sellerName == null) {
            setSellerName(new StringFilter());
        }
        return sellerName;
    }

    public void setSellerName(StringFilter sellerName) {
        this.sellerName = sellerName;
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

    public StringFilter getProfile() {
        return profile;
    }

    public Optional<StringFilter> optionalProfile() {
        return Optional.ofNullable(profile);
    }

    public StringFilter profile() {
        if (profile == null) {
            setProfile(new StringFilter());
        }
        return profile;
    }

    public void setProfile(StringFilter profile) {
        this.profile = profile;
    }

    public StringFilter getSourceFilename() {
        return sourceFilename;
    }

    public Optional<StringFilter> optionalSourceFilename() {
        return Optional.ofNullable(sourceFilename);
    }

    public StringFilter sourceFilename() {
        if (sourceFilename == null) {
            setSourceFilename(new StringFilter());
        }
        return sourceFilename;
    }

    public void setSourceFilename(StringFilter sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public StringFilter getSourceMediaType() {
        return sourceMediaType;
    }

    public Optional<StringFilter> optionalSourceMediaType() {
        return Optional.ofNullable(sourceMediaType);
    }

    public StringFilter sourceMediaType() {
        if (sourceMediaType == null) {
            setSourceMediaType(new StringFilter());
        }
        return sourceMediaType;
    }

    public void setSourceMediaType(StringFilter sourceMediaType) {
        this.sourceMediaType = sourceMediaType;
    }

    public UploadJobStatusFilter getStatus() {
        return status;
    }

    public Optional<UploadJobStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public UploadJobStatusFilter status() {
        if (status == null) {
            setStatus(new UploadJobStatusFilter());
        }
        return status;
    }

    public void setStatus(UploadJobStatusFilter status) {
        this.status = status;
    }

    public IntegerFilter getTotal() {
        return total;
    }

    public Optional<IntegerFilter> optionalTotal() {
        return Optional.ofNullable(total);
    }

    public IntegerFilter total() {
        if (total == null) {
            setTotal(new IntegerFilter());
        }
        return total;
    }

    public void setTotal(IntegerFilter total) {
        this.total = total;
    }

    public IntegerFilter getAccepted() {
        return accepted;
    }

    public Optional<IntegerFilter> optionalAccepted() {
        return Optional.ofNullable(accepted);
    }

    public IntegerFilter accepted() {
        if (accepted == null) {
            setAccepted(new IntegerFilter());
        }
        return accepted;
    }

    public void setAccepted(IntegerFilter accepted) {
        this.accepted = accepted;
    }

    public IntegerFilter getFailed() {
        return failed;
    }

    public Optional<IntegerFilter> optionalFailed() {
        return Optional.ofNullable(failed);
    }

    public IntegerFilter failed() {
        if (failed == null) {
            setFailed(new IntegerFilter());
        }
        return failed;
    }

    public void setFailed(IntegerFilter failed) {
        this.failed = failed;
    }

    public IntegerFilter getSent() {
        return sent;
    }

    public Optional<IntegerFilter> optionalSent() {
        return Optional.ofNullable(sent);
    }

    public IntegerFilter sent() {
        if (sent == null) {
            setSent(new IntegerFilter());
        }
        return sent;
    }

    public void setSent(IntegerFilter sent) {
        this.sent = sent;
    }

    public StringFilter getRemark() {
        return remark;
    }

    public Optional<StringFilter> optionalRemark() {
        return Optional.ofNullable(remark);
    }

    public StringFilter remark() {
        if (remark == null) {
            setRemark(new StringFilter());
        }
        return remark;
    }

    public void setRemark(StringFilter remark) {
        this.remark = remark;
    }

    public LongFilter getItemsId() {
        return itemsId;
    }

    public Optional<LongFilter> optionalItemsId() {
        return Optional.ofNullable(itemsId);
    }

    public LongFilter itemsId() {
        if (itemsId == null) {
            setItemsId(new LongFilter());
        }
        return itemsId;
    }

    public void setItemsId(LongFilter itemsId) {
        this.itemsId = itemsId;
    }

    public LongFilter getOriginalFileId() {
        return originalFileId;
    }

    public Optional<LongFilter> optionalOriginalFileId() {
        return Optional.ofNullable(originalFileId);
    }

    public LongFilter originalFileId() {
        if (originalFileId == null) {
            setOriginalFileId(new LongFilter());
        }
        return originalFileId;
    }

    public void setOriginalFileId(LongFilter originalFileId) {
        this.originalFileId = originalFileId;
    }

    public LongFilter getResultFileId() {
        return resultFileId;
    }

    public Optional<LongFilter> optionalResultFileId() {
        return Optional.ofNullable(resultFileId);
    }

    public LongFilter resultFileId() {
        if (resultFileId == null) {
            setResultFileId(new LongFilter());
        }
        return resultFileId;
    }

    public void setResultFileId(LongFilter resultFileId) {
        this.resultFileId = resultFileId;
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
        final UploadJobCriteria that = (UploadJobCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(jobId, that.jobId) &&
            Objects.equals(sellerId, that.sellerId) &&
            Objects.equals(sellerName, that.sellerName) &&
            Objects.equals(period, that.period) &&
            Objects.equals(profile, that.profile) &&
            Objects.equals(sourceFilename, that.sourceFilename) &&
            Objects.equals(sourceMediaType, that.sourceMediaType) &&
            Objects.equals(status, that.status) &&
            Objects.equals(total, that.total) &&
            Objects.equals(accepted, that.accepted) &&
            Objects.equals(failed, that.failed) &&
            Objects.equals(sent, that.sent) &&
            Objects.equals(remark, that.remark) &&
            Objects.equals(itemsId, that.itemsId) &&
            Objects.equals(originalFileId, that.originalFileId) &&
            Objects.equals(resultFileId, that.resultFileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            jobId,
            sellerId,
            sellerName,
            period,
            profile,
            sourceFilename,
            sourceMediaType,
            status,
            total,
            accepted,
            failed,
            sent,
            remark,
            itemsId,
            originalFileId,
            resultFileId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadJobCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalJobId().map(f -> "jobId=" + f + ", ").orElse("") +
            optionalSellerId().map(f -> "sellerId=" + f + ", ").orElse("") +
            optionalSellerName().map(f -> "sellerName=" + f + ", ").orElse("") +
            optionalPeriod().map(f -> "period=" + f + ", ").orElse("") +
            optionalProfile().map(f -> "profile=" + f + ", ").orElse("") +
            optionalSourceFilename().map(f -> "sourceFilename=" + f + ", ").orElse("") +
            optionalSourceMediaType().map(f -> "sourceMediaType=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalTotal().map(f -> "total=" + f + ", ").orElse("") +
            optionalAccepted().map(f -> "accepted=" + f + ", ").orElse("") +
            optionalFailed().map(f -> "failed=" + f + ", ").orElse("") +
            optionalSent().map(f -> "sent=" + f + ", ").orElse("") +
            optionalRemark().map(f -> "remark=" + f + ", ").orElse("") +
            optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
            optionalOriginalFileId().map(f -> "originalFileId=" + f + ", ").orElse("") +
            optionalResultFileId().map(f -> "resultFileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
