package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.ApprovalStatus;
import com.asynctide.turnbridge.domain.enumeration.ManualActionType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.ManualAction} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.ManualActionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /manual-actions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ManualActionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ManualActionType
     */
    public static class ManualActionTypeFilter extends Filter<ManualActionType> {

        public ManualActionTypeFilter() {}

        public ManualActionTypeFilter(ManualActionTypeFilter filter) {
            super(filter);
        }

        @Override
        public ManualActionTypeFilter copy() {
            return new ManualActionTypeFilter(this);
        }
    }

    /**
     * Class for filtering ApprovalStatus
     */
    public static class ApprovalStatusFilter extends Filter<ApprovalStatus> {

        public ApprovalStatusFilter() {}

        public ApprovalStatusFilter(ApprovalStatusFilter filter) {
            super(filter);
        }

        @Override
        public ApprovalStatusFilter copy() {
            return new ApprovalStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ManualActionTypeFilter actionType;

    private StringFilter reason;

    private ApprovalStatusFilter status;

    private StringFilter requestedBy;

    private InstantFilter requestedAt;

    private StringFilter approvedBy;

    private InstantFilter approvedAt;

    private LongFilter tenantId;

    private LongFilter invoiceId;

    private LongFilter importFileId;

    private Boolean distinct;

    public ManualActionCriteria() {}

    public ManualActionCriteria(ManualActionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.actionType = other.optionalActionType().map(ManualActionTypeFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ApprovalStatusFilter::copy).orElse(null);
        this.requestedBy = other.optionalRequestedBy().map(StringFilter::copy).orElse(null);
        this.requestedAt = other.optionalRequestedAt().map(InstantFilter::copy).orElse(null);
        this.approvedBy = other.optionalApprovedBy().map(StringFilter::copy).orElse(null);
        this.approvedAt = other.optionalApprovedAt().map(InstantFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.invoiceId = other.optionalInvoiceId().map(LongFilter::copy).orElse(null);
        this.importFileId = other.optionalImportFileId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ManualActionCriteria copy() {
        return new ManualActionCriteria(this);
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

    public ManualActionTypeFilter getActionType() {
        return actionType;
    }

    public Optional<ManualActionTypeFilter> optionalActionType() {
        return Optional.ofNullable(actionType);
    }

    public ManualActionTypeFilter actionType() {
        if (actionType == null) {
            setActionType(new ManualActionTypeFilter());
        }
        return actionType;
    }

    public void setActionType(ManualActionTypeFilter actionType) {
        this.actionType = actionType;
    }

    public StringFilter getReason() {
        return reason;
    }

    public Optional<StringFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public StringFilter reason() {
        if (reason == null) {
            setReason(new StringFilter());
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public ApprovalStatusFilter getStatus() {
        return status;
    }

    public Optional<ApprovalStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ApprovalStatusFilter status() {
        if (status == null) {
            setStatus(new ApprovalStatusFilter());
        }
        return status;
    }

    public void setStatus(ApprovalStatusFilter status) {
        this.status = status;
    }

    public StringFilter getRequestedBy() {
        return requestedBy;
    }

    public Optional<StringFilter> optionalRequestedBy() {
        return Optional.ofNullable(requestedBy);
    }

    public StringFilter requestedBy() {
        if (requestedBy == null) {
            setRequestedBy(new StringFilter());
        }
        return requestedBy;
    }

    public void setRequestedBy(StringFilter requestedBy) {
        this.requestedBy = requestedBy;
    }

    public InstantFilter getRequestedAt() {
        return requestedAt;
    }

    public Optional<InstantFilter> optionalRequestedAt() {
        return Optional.ofNullable(requestedAt);
    }

    public InstantFilter requestedAt() {
        if (requestedAt == null) {
            setRequestedAt(new InstantFilter());
        }
        return requestedAt;
    }

    public void setRequestedAt(InstantFilter requestedAt) {
        this.requestedAt = requestedAt;
    }

    public StringFilter getApprovedBy() {
        return approvedBy;
    }

    public Optional<StringFilter> optionalApprovedBy() {
        return Optional.ofNullable(approvedBy);
    }

    public StringFilter approvedBy() {
        if (approvedBy == null) {
            setApprovedBy(new StringFilter());
        }
        return approvedBy;
    }

    public void setApprovedBy(StringFilter approvedBy) {
        this.approvedBy = approvedBy;
    }

    public InstantFilter getApprovedAt() {
        return approvedAt;
    }

    public Optional<InstantFilter> optionalApprovedAt() {
        return Optional.ofNullable(approvedAt);
    }

    public InstantFilter approvedAt() {
        if (approvedAt == null) {
            setApprovedAt(new InstantFilter());
        }
        return approvedAt;
    }

    public void setApprovedAt(InstantFilter approvedAt) {
        this.approvedAt = approvedAt;
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

    public LongFilter getImportFileId() {
        return importFileId;
    }

    public Optional<LongFilter> optionalImportFileId() {
        return Optional.ofNullable(importFileId);
    }

    public LongFilter importFileId() {
        if (importFileId == null) {
            setImportFileId(new LongFilter());
        }
        return importFileId;
    }

    public void setImportFileId(LongFilter importFileId) {
        this.importFileId = importFileId;
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
        final ManualActionCriteria that = (ManualActionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(actionType, that.actionType) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(status, that.status) &&
            Objects.equals(requestedBy, that.requestedBy) &&
            Objects.equals(requestedAt, that.requestedAt) &&
            Objects.equals(approvedBy, that.approvedBy) &&
            Objects.equals(approvedAt, that.approvedAt) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(invoiceId, that.invoiceId) &&
            Objects.equals(importFileId, that.importFileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            actionType,
            reason,
            status,
            requestedBy,
            requestedAt,
            approvedBy,
            approvedAt,
            tenantId,
            invoiceId,
            importFileId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManualActionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalActionType().map(f -> "actionType=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRequestedBy().map(f -> "requestedBy=" + f + ", ").orElse("") +
            optionalRequestedAt().map(f -> "requestedAt=" + f + ", ").orElse("") +
            optionalApprovedBy().map(f -> "approvedBy=" + f + ", ").orElse("") +
            optionalApprovedAt().map(f -> "approvedAt=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalInvoiceId().map(f -> "invoiceId=" + f + ", ").orElse("") +
            optionalImportFileId().map(f -> "importFileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
