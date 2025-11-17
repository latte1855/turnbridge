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

    private StringFilter eventCode;

    private StringFilter level;

    private StringFilter message;

    private InstantFilter occurredAt;

    private LongFilter importFileId;

    private Boolean distinct;

    public ImportFileLogCriteria() {}

    public ImportFileLogCriteria(ImportFileLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.eventCode = other.optionalEventCode().map(StringFilter::copy).orElse(null);
        this.level = other.optionalLevel().map(StringFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.occurredAt = other.optionalOccurredAt().map(InstantFilter::copy).orElse(null);
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

    public StringFilter getEventCode() {
        return eventCode;
    }

    public Optional<StringFilter> optionalEventCode() {
        return Optional.ofNullable(eventCode);
    }

    public StringFilter eventCode() {
        if (eventCode == null) {
            setEventCode(new StringFilter());
        }
        return eventCode;
    }

    public void setEventCode(StringFilter eventCode) {
        this.eventCode = eventCode;
    }

    public StringFilter getLevel() {
        return level;
    }

    public Optional<StringFilter> optionalLevel() {
        return Optional.ofNullable(level);
    }

    public StringFilter level() {
        if (level == null) {
            setLevel(new StringFilter());
        }
        return level;
    }

    public void setLevel(StringFilter level) {
        this.level = level;
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
            Objects.equals(eventCode, that.eventCode) &&
            Objects.equals(level, that.level) &&
            Objects.equals(message, that.message) &&
            Objects.equals(occurredAt, that.occurredAt) &&
            Objects.equals(importFileId, that.importFileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventCode, level, message, occurredAt, importFileId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEventCode().map(f -> "eventCode=" + f + ", ").orElse("") +
            optionalLevel().map(f -> "level=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalOccurredAt().map(f -> "occurredAt=" + f + ", ").orElse("") +
            optionalImportFileId().map(f -> "importFileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
