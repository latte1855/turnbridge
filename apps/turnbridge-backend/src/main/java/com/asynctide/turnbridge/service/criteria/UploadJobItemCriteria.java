package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.domain.enumeration.TaxType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.UploadJobItem} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.UploadJobItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /upload-job-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadJobItemCriteria implements Serializable, Criteria {

    /**
     * Class for filtering JobItemStatus
     */
    public static class JobItemStatusFilter extends Filter<JobItemStatus> {

        public JobItemStatusFilter() {}

        public JobItemStatusFilter(JobItemStatusFilter filter) {
            super(filter);
        }

        @Override
        public JobItemStatusFilter copy() {
            return new JobItemStatusFilter(this);
        }
    }

    /**
     * Class for filtering TaxType
     */
    public static class TaxTypeFilter extends Filter<TaxType> {

        public TaxTypeFilter() {}

        public TaxTypeFilter(TaxTypeFilter filter) {
            super(filter);
        }

        @Override
        public TaxTypeFilter copy() {
            return new TaxTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter lineNo;

    private StringFilter traceId;

    private JobItemStatusFilter status;

    private StringFilter resultCode;

    private StringFilter resultMsg;

    private StringFilter buyerId;

    private StringFilter buyerName;

    private StringFilter currency;

    private BigDecimalFilter amountExcl;

    private BigDecimalFilter taxAmount;

    private BigDecimalFilter amountIncl;

    private TaxTypeFilter taxType;

    private LocalDateFilter invoiceDate;

    private StringFilter invoiceNo;

    private StringFilter assignedPrefix;

    private StringFilter rawHash;

    private StringFilter profileDetected;

    private LongFilter jobId;

    private Boolean distinct;

    public UploadJobItemCriteria() {}

    public UploadJobItemCriteria(UploadJobItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.lineNo = other.optionalLineNo().map(IntegerFilter::copy).orElse(null);
        this.traceId = other.optionalTraceId().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(JobItemStatusFilter::copy).orElse(null);
        this.resultCode = other.optionalResultCode().map(StringFilter::copy).orElse(null);
        this.resultMsg = other.optionalResultMsg().map(StringFilter::copy).orElse(null);
        this.buyerId = other.optionalBuyerId().map(StringFilter::copy).orElse(null);
        this.buyerName = other.optionalBuyerName().map(StringFilter::copy).orElse(null);
        this.currency = other.optionalCurrency().map(StringFilter::copy).orElse(null);
        this.amountExcl = other.optionalAmountExcl().map(BigDecimalFilter::copy).orElse(null);
        this.taxAmount = other.optionalTaxAmount().map(BigDecimalFilter::copy).orElse(null);
        this.amountIncl = other.optionalAmountIncl().map(BigDecimalFilter::copy).orElse(null);
        this.taxType = other.optionalTaxType().map(TaxTypeFilter::copy).orElse(null);
        this.invoiceDate = other.optionalInvoiceDate().map(LocalDateFilter::copy).orElse(null);
        this.invoiceNo = other.optionalInvoiceNo().map(StringFilter::copy).orElse(null);
        this.assignedPrefix = other.optionalAssignedPrefix().map(StringFilter::copy).orElse(null);
        this.rawHash = other.optionalRawHash().map(StringFilter::copy).orElse(null);
        this.profileDetected = other.optionalProfileDetected().map(StringFilter::copy).orElse(null);
        this.jobId = other.optionalJobId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UploadJobItemCriteria copy() {
        return new UploadJobItemCriteria(this);
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

    public IntegerFilter getLineNo() {
        return lineNo;
    }

    public Optional<IntegerFilter> optionalLineNo() {
        return Optional.ofNullable(lineNo);
    }

    public IntegerFilter lineNo() {
        if (lineNo == null) {
            setLineNo(new IntegerFilter());
        }
        return lineNo;
    }

    public void setLineNo(IntegerFilter lineNo) {
        this.lineNo = lineNo;
    }

    public StringFilter getTraceId() {
        return traceId;
    }

    public Optional<StringFilter> optionalTraceId() {
        return Optional.ofNullable(traceId);
    }

    public StringFilter traceId() {
        if (traceId == null) {
            setTraceId(new StringFilter());
        }
        return traceId;
    }

    public void setTraceId(StringFilter traceId) {
        this.traceId = traceId;
    }

    public JobItemStatusFilter getStatus() {
        return status;
    }

    public Optional<JobItemStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public JobItemStatusFilter status() {
        if (status == null) {
            setStatus(new JobItemStatusFilter());
        }
        return status;
    }

    public void setStatus(JobItemStatusFilter status) {
        this.status = status;
    }

    public StringFilter getResultCode() {
        return resultCode;
    }

    public Optional<StringFilter> optionalResultCode() {
        return Optional.ofNullable(resultCode);
    }

    public StringFilter resultCode() {
        if (resultCode == null) {
            setResultCode(new StringFilter());
        }
        return resultCode;
    }

    public void setResultCode(StringFilter resultCode) {
        this.resultCode = resultCode;
    }

    public StringFilter getResultMsg() {
        return resultMsg;
    }

    public Optional<StringFilter> optionalResultMsg() {
        return Optional.ofNullable(resultMsg);
    }

    public StringFilter resultMsg() {
        if (resultMsg == null) {
            setResultMsg(new StringFilter());
        }
        return resultMsg;
    }

    public void setResultMsg(StringFilter resultMsg) {
        this.resultMsg = resultMsg;
    }

    public StringFilter getBuyerId() {
        return buyerId;
    }

    public Optional<StringFilter> optionalBuyerId() {
        return Optional.ofNullable(buyerId);
    }

    public StringFilter buyerId() {
        if (buyerId == null) {
            setBuyerId(new StringFilter());
        }
        return buyerId;
    }

    public void setBuyerId(StringFilter buyerId) {
        this.buyerId = buyerId;
    }

    public StringFilter getBuyerName() {
        return buyerName;
    }

    public Optional<StringFilter> optionalBuyerName() {
        return Optional.ofNullable(buyerName);
    }

    public StringFilter buyerName() {
        if (buyerName == null) {
            setBuyerName(new StringFilter());
        }
        return buyerName;
    }

    public void setBuyerName(StringFilter buyerName) {
        this.buyerName = buyerName;
    }

    public StringFilter getCurrency() {
        return currency;
    }

    public Optional<StringFilter> optionalCurrency() {
        return Optional.ofNullable(currency);
    }

    public StringFilter currency() {
        if (currency == null) {
            setCurrency(new StringFilter());
        }
        return currency;
    }

    public void setCurrency(StringFilter currency) {
        this.currency = currency;
    }

    public BigDecimalFilter getAmountExcl() {
        return amountExcl;
    }

    public Optional<BigDecimalFilter> optionalAmountExcl() {
        return Optional.ofNullable(amountExcl);
    }

    public BigDecimalFilter amountExcl() {
        if (amountExcl == null) {
            setAmountExcl(new BigDecimalFilter());
        }
        return amountExcl;
    }

    public void setAmountExcl(BigDecimalFilter amountExcl) {
        this.amountExcl = amountExcl;
    }

    public BigDecimalFilter getTaxAmount() {
        return taxAmount;
    }

    public Optional<BigDecimalFilter> optionalTaxAmount() {
        return Optional.ofNullable(taxAmount);
    }

    public BigDecimalFilter taxAmount() {
        if (taxAmount == null) {
            setTaxAmount(new BigDecimalFilter());
        }
        return taxAmount;
    }

    public void setTaxAmount(BigDecimalFilter taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimalFilter getAmountIncl() {
        return amountIncl;
    }

    public Optional<BigDecimalFilter> optionalAmountIncl() {
        return Optional.ofNullable(amountIncl);
    }

    public BigDecimalFilter amountIncl() {
        if (amountIncl == null) {
            setAmountIncl(new BigDecimalFilter());
        }
        return amountIncl;
    }

    public void setAmountIncl(BigDecimalFilter amountIncl) {
        this.amountIncl = amountIncl;
    }

    public TaxTypeFilter getTaxType() {
        return taxType;
    }

    public Optional<TaxTypeFilter> optionalTaxType() {
        return Optional.ofNullable(taxType);
    }

    public TaxTypeFilter taxType() {
        if (taxType == null) {
            setTaxType(new TaxTypeFilter());
        }
        return taxType;
    }

    public void setTaxType(TaxTypeFilter taxType) {
        this.taxType = taxType;
    }

    public LocalDateFilter getInvoiceDate() {
        return invoiceDate;
    }

    public Optional<LocalDateFilter> optionalInvoiceDate() {
        return Optional.ofNullable(invoiceDate);
    }

    public LocalDateFilter invoiceDate() {
        if (invoiceDate == null) {
            setInvoiceDate(new LocalDateFilter());
        }
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateFilter invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public StringFilter getInvoiceNo() {
        return invoiceNo;
    }

    public Optional<StringFilter> optionalInvoiceNo() {
        return Optional.ofNullable(invoiceNo);
    }

    public StringFilter invoiceNo() {
        if (invoiceNo == null) {
            setInvoiceNo(new StringFilter());
        }
        return invoiceNo;
    }

    public void setInvoiceNo(StringFilter invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public StringFilter getAssignedPrefix() {
        return assignedPrefix;
    }

    public Optional<StringFilter> optionalAssignedPrefix() {
        return Optional.ofNullable(assignedPrefix);
    }

    public StringFilter assignedPrefix() {
        if (assignedPrefix == null) {
            setAssignedPrefix(new StringFilter());
        }
        return assignedPrefix;
    }

    public void setAssignedPrefix(StringFilter assignedPrefix) {
        this.assignedPrefix = assignedPrefix;
    }

    public StringFilter getRawHash() {
        return rawHash;
    }

    public Optional<StringFilter> optionalRawHash() {
        return Optional.ofNullable(rawHash);
    }

    public StringFilter rawHash() {
        if (rawHash == null) {
            setRawHash(new StringFilter());
        }
        return rawHash;
    }

    public void setRawHash(StringFilter rawHash) {
        this.rawHash = rawHash;
    }

    public StringFilter getProfileDetected() {
        return profileDetected;
    }

    public Optional<StringFilter> optionalProfileDetected() {
        return Optional.ofNullable(profileDetected);
    }

    public StringFilter profileDetected() {
        if (profileDetected == null) {
            setProfileDetected(new StringFilter());
        }
        return profileDetected;
    }

    public void setProfileDetected(StringFilter profileDetected) {
        this.profileDetected = profileDetected;
    }

    public LongFilter getJobId() {
        return jobId;
    }

    public Optional<LongFilter> optionalJobId() {
        return Optional.ofNullable(jobId);
    }

    public LongFilter jobId() {
        if (jobId == null) {
            setJobId(new LongFilter());
        }
        return jobId;
    }

    public void setJobId(LongFilter jobId) {
        this.jobId = jobId;
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
        final UploadJobItemCriteria that = (UploadJobItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lineNo, that.lineNo) &&
            Objects.equals(traceId, that.traceId) &&
            Objects.equals(status, that.status) &&
            Objects.equals(resultCode, that.resultCode) &&
            Objects.equals(resultMsg, that.resultMsg) &&
            Objects.equals(buyerId, that.buyerId) &&
            Objects.equals(buyerName, that.buyerName) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(amountExcl, that.amountExcl) &&
            Objects.equals(taxAmount, that.taxAmount) &&
            Objects.equals(amountIncl, that.amountIncl) &&
            Objects.equals(taxType, that.taxType) &&
            Objects.equals(invoiceDate, that.invoiceDate) &&
            Objects.equals(invoiceNo, that.invoiceNo) &&
            Objects.equals(assignedPrefix, that.assignedPrefix) &&
            Objects.equals(rawHash, that.rawHash) &&
            Objects.equals(profileDetected, that.profileDetected) &&
            Objects.equals(jobId, that.jobId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            lineNo,
            traceId,
            status,
            resultCode,
            resultMsg,
            buyerId,
            buyerName,
            currency,
            amountExcl,
            taxAmount,
            amountIncl,
            taxType,
            invoiceDate,
            invoiceNo,
            assignedPrefix,
            rawHash,
            profileDetected,
            jobId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadJobItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLineNo().map(f -> "lineNo=" + f + ", ").orElse("") +
            optionalTraceId().map(f -> "traceId=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalResultCode().map(f -> "resultCode=" + f + ", ").orElse("") +
            optionalResultMsg().map(f -> "resultMsg=" + f + ", ").orElse("") +
            optionalBuyerId().map(f -> "buyerId=" + f + ", ").orElse("") +
            optionalBuyerName().map(f -> "buyerName=" + f + ", ").orElse("") +
            optionalCurrency().map(f -> "currency=" + f + ", ").orElse("") +
            optionalAmountExcl().map(f -> "amountExcl=" + f + ", ").orElse("") +
            optionalTaxAmount().map(f -> "taxAmount=" + f + ", ").orElse("") +
            optionalAmountIncl().map(f -> "amountIncl=" + f + ", ").orElse("") +
            optionalTaxType().map(f -> "taxType=" + f + ", ").orElse("") +
            optionalInvoiceDate().map(f -> "invoiceDate=" + f + ", ").orElse("") +
            optionalInvoiceNo().map(f -> "invoiceNo=" + f + ", ").orElse("") +
            optionalAssignedPrefix().map(f -> "assignedPrefix=" + f + ", ").orElse("") +
            optionalRawHash().map(f -> "rawHash=" + f + ", ").orElse("") +
            optionalProfileDetected().map(f -> "profileDetected=" + f + ", ").orElse("") +
            optionalJobId().map(f -> "jobId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
