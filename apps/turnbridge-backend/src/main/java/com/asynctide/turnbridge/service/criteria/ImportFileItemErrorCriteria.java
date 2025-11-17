package com.asynctide.turnbridge.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.ImportFileItemError} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.ImportFileItemErrorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /import-file-item-errors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileItemErrorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter columnIndex;

    private StringFilter fieldName;

    private StringFilter errorCode;

    private StringFilter message;

    private StringFilter severity;

    private InstantFilter occurredAt;

    private LongFilter importFileItemId;

    private Boolean distinct;

    public ImportFileItemErrorCriteria() {}

    public ImportFileItemErrorCriteria(ImportFileItemErrorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.columnIndex = other.optionalColumnIndex().map(IntegerFilter::copy).orElse(null);
        this.fieldName = other.optionalFieldName().map(StringFilter::copy).orElse(null);
        this.errorCode = other.optionalErrorCode().map(StringFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.severity = other.optionalSeverity().map(StringFilter::copy).orElse(null);
        this.occurredAt = other.optionalOccurredAt().map(InstantFilter::copy).orElse(null);
        this.importFileItemId = other.optionalImportFileItemId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ImportFileItemErrorCriteria copy() {
        return new ImportFileItemErrorCriteria(this);
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

    public IntegerFilter getColumnIndex() {
        return columnIndex;
    }

    public Optional<IntegerFilter> optionalColumnIndex() {
        return Optional.ofNullable(columnIndex);
    }

    public IntegerFilter columnIndex() {
        if (columnIndex == null) {
            setColumnIndex(new IntegerFilter());
        }
        return columnIndex;
    }

    public void setColumnIndex(IntegerFilter columnIndex) {
        this.columnIndex = columnIndex;
    }

    public StringFilter getFieldName() {
        return fieldName;
    }

    public Optional<StringFilter> optionalFieldName() {
        return Optional.ofNullable(fieldName);
    }

    public StringFilter fieldName() {
        if (fieldName == null) {
            setFieldName(new StringFilter());
        }
        return fieldName;
    }

    public void setFieldName(StringFilter fieldName) {
        this.fieldName = fieldName;
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

    public StringFilter getSeverity() {
        return severity;
    }

    public Optional<StringFilter> optionalSeverity() {
        return Optional.ofNullable(severity);
    }

    public StringFilter severity() {
        if (severity == null) {
            setSeverity(new StringFilter());
        }
        return severity;
    }

    public void setSeverity(StringFilter severity) {
        this.severity = severity;
    }

    public InstantFilter getOccurredAt() {
        return occurredAt;
    }

    public Optional<InstantFilter> optionalOccurredAt() {
        return Optional.ofNullable(occurredAt);
    }

    public InstantFilter occurredAt() {
        if (occurredAt == null) {
            setOccurredAt(new InstantFilter());
        }
        return occurredAt;
    }

    public void setOccurredAt(InstantFilter occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LongFilter getImportFileItemId() {
        return importFileItemId;
    }

    public Optional<LongFilter> optionalImportFileItemId() {
        return Optional.ofNullable(importFileItemId);
    }

    public LongFilter importFileItemId() {
        if (importFileItemId == null) {
            setImportFileItemId(new LongFilter());
        }
        return importFileItemId;
    }

    public void setImportFileItemId(LongFilter importFileItemId) {
        this.importFileItemId = importFileItemId;
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
        final ImportFileItemErrorCriteria that = (ImportFileItemErrorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(columnIndex, that.columnIndex) &&
            Objects.equals(fieldName, that.fieldName) &&
            Objects.equals(errorCode, that.errorCode) &&
            Objects.equals(message, that.message) &&
            Objects.equals(severity, that.severity) &&
            Objects.equals(occurredAt, that.occurredAt) &&
            Objects.equals(importFileItemId, that.importFileItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, columnIndex, fieldName, errorCode, message, severity, occurredAt, importFileItemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileItemErrorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalColumnIndex().map(f -> "columnIndex=" + f + ", ").orElse("") +
            optionalFieldName().map(f -> "fieldName=" + f + ", ").orElse("") +
            optionalErrorCode().map(f -> "errorCode=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalSeverity().map(f -> "severity=" + f + ", ").orElse("") +
            optionalOccurredAt().map(f -> "occurredAt=" + f + ", ").orElse("") +
            optionalImportFileItemId().map(f -> "importFileItemId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
