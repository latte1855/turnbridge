package com.asynctide.turnbridge.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.Tenant} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.TenantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tenants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter code;

    private StringFilter status;

    private LongFilter importFilesId;

    private LongFilter invoicesId;

    private LongFilter webhookEndpointsId;

    private Boolean distinct;

    public TenantCriteria() {}

    public TenantCriteria(TenantCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.importFilesId = other.optionalImportFilesId().map(LongFilter::copy).orElse(null);
        this.invoicesId = other.optionalInvoicesId().map(LongFilter::copy).orElse(null);
        this.webhookEndpointsId = other.optionalWebhookEndpointsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TenantCriteria copy() {
        return new TenantCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
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

    public LongFilter getImportFilesId() {
        return importFilesId;
    }

    public Optional<LongFilter> optionalImportFilesId() {
        return Optional.ofNullable(importFilesId);
    }

    public LongFilter importFilesId() {
        if (importFilesId == null) {
            setImportFilesId(new LongFilter());
        }
        return importFilesId;
    }

    public void setImportFilesId(LongFilter importFilesId) {
        this.importFilesId = importFilesId;
    }

    public LongFilter getInvoicesId() {
        return invoicesId;
    }

    public Optional<LongFilter> optionalInvoicesId() {
        return Optional.ofNullable(invoicesId);
    }

    public LongFilter invoicesId() {
        if (invoicesId == null) {
            setInvoicesId(new LongFilter());
        }
        return invoicesId;
    }

    public void setInvoicesId(LongFilter invoicesId) {
        this.invoicesId = invoicesId;
    }

    public LongFilter getWebhookEndpointsId() {
        return webhookEndpointsId;
    }

    public Optional<LongFilter> optionalWebhookEndpointsId() {
        return Optional.ofNullable(webhookEndpointsId);
    }

    public LongFilter webhookEndpointsId() {
        if (webhookEndpointsId == null) {
            setWebhookEndpointsId(new LongFilter());
        }
        return webhookEndpointsId;
    }

    public void setWebhookEndpointsId(LongFilter webhookEndpointsId) {
        this.webhookEndpointsId = webhookEndpointsId;
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
        final TenantCriteria that = (TenantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(code, that.code) &&
            Objects.equals(status, that.status) &&
            Objects.equals(importFilesId, that.importFilesId) &&
            Objects.equals(invoicesId, that.invoicesId) &&
            Objects.equals(webhookEndpointsId, that.webhookEndpointsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, status, importFilesId, invoicesId, webhookEndpointsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalImportFilesId().map(f -> "importFilesId=" + f + ", ").orElse("") +
            optionalInvoicesId().map(f -> "invoicesId=" + f + ", ").orElse("") +
            optionalWebhookEndpointsId().map(f -> "webhookEndpointsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
