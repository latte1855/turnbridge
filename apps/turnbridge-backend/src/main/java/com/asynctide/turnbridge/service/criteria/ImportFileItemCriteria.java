package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.ImportFileItem} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.ImportFileItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /import-file-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileItemCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ImportItemStatus
     */
    public static class ImportItemStatusFilter extends Filter<ImportItemStatus> {

        public ImportItemStatusFilter() {}

        public ImportItemStatusFilter(ImportItemStatusFilter filter) {
            super(filter);
        }

        @Override
        public ImportItemStatusFilter copy() {
            return new ImportItemStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter lineIndex;

    private StringFilter rawHash;

    private StringFilter sourceFamily;

    private StringFilter normalizedFamily;

    private ImportItemStatusFilter status;

    private StringFilter errorCode;

    private StringFilter errorMessage;

    private LongFilter importFileId;

    private LongFilter invoiceId;

    private Boolean distinct;

    public ImportFileItemCriteria() {}

    public ImportFileItemCriteria(ImportFileItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.lineIndex = other.optionalLineIndex().map(IntegerFilter::copy).orElse(null);
        this.rawHash = other.optionalRawHash().map(StringFilter::copy).orElse(null);
        this.sourceFamily = other.optionalSourceFamily().map(StringFilter::copy).orElse(null);
        this.normalizedFamily = other.optionalNormalizedFamily().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ImportItemStatusFilter::copy).orElse(null);
        this.errorCode = other.optionalErrorCode().map(StringFilter::copy).orElse(null);
        this.errorMessage = other.optionalErrorMessage().map(StringFilter::copy).orElse(null);
        this.importFileId = other.optionalImportFileId().map(LongFilter::copy).orElse(null);
        this.invoiceId = other.optionalInvoiceId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ImportFileItemCriteria copy() {
        return new ImportFileItemCriteria(this);
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

    public IntegerFilter getLineIndex() {
        return lineIndex;
    }

    public Optional<IntegerFilter> optionalLineIndex() {
        return Optional.ofNullable(lineIndex);
    }

    public IntegerFilter lineIndex() {
        if (lineIndex == null) {
            setLineIndex(new IntegerFilter());
        }
        return lineIndex;
    }

    public void setLineIndex(IntegerFilter lineIndex) {
        this.lineIndex = lineIndex;
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

    public StringFilter getSourceFamily() {
        return sourceFamily;
    }

    public Optional<StringFilter> optionalSourceFamily() {
        return Optional.ofNullable(sourceFamily);
    }

    public StringFilter sourceFamily() {
        if (sourceFamily == null) {
            setSourceFamily(new StringFilter());
        }
        return sourceFamily;
    }

    public void setSourceFamily(StringFilter sourceFamily) {
        this.sourceFamily = sourceFamily;
    }

    public StringFilter getNormalizedFamily() {
        return normalizedFamily;
    }

    public Optional<StringFilter> optionalNormalizedFamily() {
        return Optional.ofNullable(normalizedFamily);
    }

    public StringFilter normalizedFamily() {
        if (normalizedFamily == null) {
            setNormalizedFamily(new StringFilter());
        }
        return normalizedFamily;
    }

    public void setNormalizedFamily(StringFilter normalizedFamily) {
        this.normalizedFamily = normalizedFamily;
    }

    public ImportItemStatusFilter getStatus() {
        return status;
    }

    public Optional<ImportItemStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ImportItemStatusFilter status() {
        if (status == null) {
            setStatus(new ImportItemStatusFilter());
        }
        return status;
    }

    public void setStatus(ImportItemStatusFilter status) {
        this.status = status;
    }

    public StringFilter getErrorCode() {
        return errorCode;
    }

    public Optional<StringFilter> optionalErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    public StringFilter errorCode() {
        if (errorCode == null) {
            setErrorCode(new StringFilter());
        }
        return errorCode;
    }

    public void setErrorCode(StringFilter errorCode) {
        this.errorCode = errorCode;
    }

    public StringFilter getErrorMessage() {
        return errorMessage;
    }

    public Optional<StringFilter> optionalErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            setErrorMessage(new StringFilter());
        }
        return errorMessage;
    }

    public void setErrorMessage(StringFilter errorMessage) {
        this.errorMessage = errorMessage;
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
        final ImportFileItemCriteria that = (ImportFileItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lineIndex, that.lineIndex) &&
            Objects.equals(rawHash, that.rawHash) &&
            Objects.equals(sourceFamily, that.sourceFamily) &&
            Objects.equals(normalizedFamily, that.normalizedFamily) &&
            Objects.equals(status, that.status) &&
            Objects.equals(errorCode, that.errorCode) &&
            Objects.equals(errorMessage, that.errorMessage) &&
            Objects.equals(importFileId, that.importFileId) &&
            Objects.equals(invoiceId, that.invoiceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            lineIndex,
            rawHash,
            sourceFamily,
            normalizedFamily,
            status,
            errorCode,
            errorMessage,
            importFileId,
            invoiceId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLineIndex().map(f -> "lineIndex=" + f + ", ").orElse("") +
            optionalRawHash().map(f -> "rawHash=" + f + ", ").orElse("") +
            optionalSourceFamily().map(f -> "sourceFamily=" + f + ", ").orElse("") +
            optionalNormalizedFamily().map(f -> "normalizedFamily=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalErrorCode().map(f -> "errorCode=" + f + ", ").orElse("") +
            optionalErrorMessage().map(f -> "errorMessage=" + f + ", ").orElse("") +
            optionalImportFileId().map(f -> "importFileId=" + f + ", ").orElse("") +
            optionalInvoiceId().map(f -> "invoiceId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
