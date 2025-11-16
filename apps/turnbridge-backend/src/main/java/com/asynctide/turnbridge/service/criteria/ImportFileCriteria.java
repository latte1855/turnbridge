package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.ImportFile} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.ImportFileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /import-files?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ImportType
     */
    public static class ImportTypeFilter extends Filter<ImportType> {

        public ImportTypeFilter() {}

        public ImportTypeFilter(ImportTypeFilter filter) {
            super(filter);
        }

        @Override
        public ImportTypeFilter copy() {
            return new ImportTypeFilter(this);
        }
    }

    /**
     * Class for filtering ImportStatus
     */
    public static class ImportStatusFilter extends Filter<ImportStatus> {

        public ImportStatusFilter() {}

        public ImportStatusFilter(ImportStatusFilter filter) {
            super(filter);
        }

        @Override
        public ImportStatusFilter copy() {
            return new ImportStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ImportTypeFilter importType;

    private StringFilter originalFilename;

    private StringFilter sha256;

    private IntegerFilter totalCount;

    private IntegerFilter successCount;

    private IntegerFilter errorCount;

    private ImportStatusFilter status;

    private StringFilter legacyType;

    private LongFilter tenantId;

    private Boolean distinct;

    public ImportFileCriteria() {}

    public ImportFileCriteria(ImportFileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.importType = other.optionalImportType().map(ImportTypeFilter::copy).orElse(null);
        this.originalFilename = other.optionalOriginalFilename().map(StringFilter::copy).orElse(null);
        this.sha256 = other.optionalSha256().map(StringFilter::copy).orElse(null);
        this.totalCount = other.optionalTotalCount().map(IntegerFilter::copy).orElse(null);
        this.successCount = other.optionalSuccessCount().map(IntegerFilter::copy).orElse(null);
        this.errorCount = other.optionalErrorCount().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ImportStatusFilter::copy).orElse(null);
        this.legacyType = other.optionalLegacyType().map(StringFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ImportFileCriteria copy() {
        return new ImportFileCriteria(this);
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

    public ImportTypeFilter getImportType() {
        return importType;
    }

    public Optional<ImportTypeFilter> optionalImportType() {
        return Optional.ofNullable(importType);
    }

    public ImportTypeFilter importType() {
        if (importType == null) {
            setImportType(new ImportTypeFilter());
        }
        return importType;
    }

    public void setImportType(ImportTypeFilter importType) {
        this.importType = importType;
    }

    public StringFilter getOriginalFilename() {
        return originalFilename;
    }

    public Optional<StringFilter> optionalOriginalFilename() {
        return Optional.ofNullable(originalFilename);
    }

    public StringFilter originalFilename() {
        if (originalFilename == null) {
            setOriginalFilename(new StringFilter());
        }
        return originalFilename;
    }

    public void setOriginalFilename(StringFilter originalFilename) {
        this.originalFilename = originalFilename;
    }

    public StringFilter getSha256() {
        return sha256;
    }

    public Optional<StringFilter> optionalSha256() {
        return Optional.ofNullable(sha256);
    }

    public StringFilter sha256() {
        if (sha256 == null) {
            setSha256(new StringFilter());
        }
        return sha256;
    }

    public void setSha256(StringFilter sha256) {
        this.sha256 = sha256;
    }

    public IntegerFilter getTotalCount() {
        return totalCount;
    }

    public Optional<IntegerFilter> optionalTotalCount() {
        return Optional.ofNullable(totalCount);
    }

    public IntegerFilter totalCount() {
        if (totalCount == null) {
            setTotalCount(new IntegerFilter());
        }
        return totalCount;
    }

    public void setTotalCount(IntegerFilter totalCount) {
        this.totalCount = totalCount;
    }

    public IntegerFilter getSuccessCount() {
        return successCount;
    }

    public Optional<IntegerFilter> optionalSuccessCount() {
        return Optional.ofNullable(successCount);
    }

    public IntegerFilter successCount() {
        if (successCount == null) {
            setSuccessCount(new IntegerFilter());
        }
        return successCount;
    }

    public void setSuccessCount(IntegerFilter successCount) {
        this.successCount = successCount;
    }

    public IntegerFilter getErrorCount() {
        return errorCount;
    }

    public Optional<IntegerFilter> optionalErrorCount() {
        return Optional.ofNullable(errorCount);
    }

    public IntegerFilter errorCount() {
        if (errorCount == null) {
            setErrorCount(new IntegerFilter());
        }
        return errorCount;
    }

    public void setErrorCount(IntegerFilter errorCount) {
        this.errorCount = errorCount;
    }

    public ImportStatusFilter getStatus() {
        return status;
    }

    public Optional<ImportStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ImportStatusFilter status() {
        if (status == null) {
            setStatus(new ImportStatusFilter());
        }
        return status;
    }

    public void setStatus(ImportStatusFilter status) {
        this.status = status;
    }

    public StringFilter getLegacyType() {
        return legacyType;
    }

    public Optional<StringFilter> optionalLegacyType() {
        return Optional.ofNullable(legacyType);
    }

    public StringFilter legacyType() {
        if (legacyType == null) {
            setLegacyType(new StringFilter());
        }
        return legacyType;
    }

    public void setLegacyType(StringFilter legacyType) {
        this.legacyType = legacyType;
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
        final ImportFileCriteria that = (ImportFileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(importType, that.importType) &&
            Objects.equals(originalFilename, that.originalFilename) &&
            Objects.equals(sha256, that.sha256) &&
            Objects.equals(totalCount, that.totalCount) &&
            Objects.equals(successCount, that.successCount) &&
            Objects.equals(errorCount, that.errorCount) &&
            Objects.equals(status, that.status) &&
            Objects.equals(legacyType, that.legacyType) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            importType,
            originalFilename,
            sha256,
            totalCount,
            successCount,
            errorCount,
            status,
            legacyType,
            tenantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalImportType().map(f -> "importType=" + f + ", ").orElse("") +
            optionalOriginalFilename().map(f -> "originalFilename=" + f + ", ").orElse("") +
            optionalSha256().map(f -> "sha256=" + f + ", ").orElse("") +
            optionalTotalCount().map(f -> "totalCount=" + f + ", ").orElse("") +
            optionalSuccessCount().map(f -> "successCount=" + f + ", ").orElse("") +
            optionalErrorCount().map(f -> "errorCount=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalLegacyType().map(f -> "legacyType=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
