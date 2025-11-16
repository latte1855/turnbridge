package com.asynctide.turnbridge.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.ImportFileLog} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.ImportFileLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /import-file-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter lineIndex;

    private StringFilter field;

    private StringFilter errorCode;

    private StringFilter message;

    private StringFilter sourceFamily;

    private StringFilter normalizedFamily;

    private LongFilter importFileId;

    private Boolean distinct;

    public ImportFileLogCriteria() {}

    public ImportFileLogCriteria(ImportFileLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.lineIndex = other.optionalLineIndex().map(IntegerFilter::copy).orElse(null);
        this.field = other.optionalField().map(StringFilter::copy).orElse(null);
        this.errorCode = other.optionalErrorCode().map(StringFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.sourceFamily = other.optionalSourceFamily().map(StringFilter::copy).orElse(null);
        this.normalizedFamily = other.optionalNormalizedFamily().map(StringFilter::copy).orElse(null);
        this.importFileId = other.optionalImportFileId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ImportFileLogCriteria copy() {
        return new ImportFileLogCriteria(this);
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

    public StringFilter getField() {
        return field;
    }

    public Optional<StringFilter> optionalField() {
        return Optional.ofNullable(field);
    }

    public StringFilter field() {
        if (field == null) {
            setField(new StringFilter());
        }
        return field;
    }

    public void setField(StringFilter field) {
        this.field = field;
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

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
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
        final ImportFileLogCriteria that = (ImportFileLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lineIndex, that.lineIndex) &&
            Objects.equals(field, that.field) &&
            Objects.equals(errorCode, that.errorCode) &&
            Objects.equals(message, that.message) &&
            Objects.equals(sourceFamily, that.sourceFamily) &&
            Objects.equals(normalizedFamily, that.normalizedFamily) &&
            Objects.equals(importFileId, that.importFileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineIndex, field, errorCode, message, sourceFamily, normalizedFamily, importFileId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLineIndex().map(f -> "lineIndex=" + f + ", ").orElse("") +
            optionalField().map(f -> "field=" + f + ", ").orElse("") +
            optionalErrorCode().map(f -> "errorCode=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalSourceFamily().map(f -> "sourceFamily=" + f + ", ").orElse("") +
            optionalNormalizedFamily().map(f -> "normalizedFamily=" + f + ", ").orElse("") +
            optionalImportFileId().map(f -> "importFileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
