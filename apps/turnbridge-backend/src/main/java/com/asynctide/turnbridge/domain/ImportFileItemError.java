package com.asynctide.turnbridge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 匯入檔明細欄位錯誤（供 UI 詳細檢視）
 */
@Entity
@Table(name = "import_file_item_error")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileItemError implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 欄位號（1-based）
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "column_index", nullable = false)
    private Integer columnIndex;

    /**
     * 欄位名稱（如 BuyerId/Amount）
     */
    @NotNull
    @Size(max = 128)
    @Column(name = "field_name", length = 128, nullable = false)
    private String fieldName;

    /**
     * 錯誤碼（LENGTH_INVALID、NUMBER_FORMAT 等）
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "error_code", length = 64, nullable = false)
    private String errorCode;

    /**
     * 錯誤訊息
     */
    @Size(max = 1024)
    @Column(name = "message", length = 1024)
    private String message;

    /**
     * 嚴重度（INFO/WARN/ERROR）
     */
    @Size(max = 16)
    @Column(name = "severity", length = 16)
    private String severity;

    /**
     * 發生時間（可選）
     */
    @Column(name = "occurred_at")
    private Instant occurredAt;

    /**
     * 匯入檔明細
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "importFile", "invoice" }, allowSetters = true)
    private ImportFileItem importFileItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ImportFileItemError id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getColumnIndex() {
        return this.columnIndex;
    }

    public ImportFileItemError columnIndex(Integer columnIndex) {
        this.setColumnIndex(columnIndex);
        return this;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public ImportFileItemError fieldName(String fieldName) {
        this.setFieldName(fieldName);
        return this;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public ImportFileItemError errorCode(String errorCode) {
        this.setErrorCode(errorCode);
        return this;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public ImportFileItemError message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return this.severity;
    }

    public ImportFileItemError severity(String severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public ImportFileItemError occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public ImportFileItem getImportFileItem() {
        return this.importFileItem;
    }

    public void setImportFileItem(ImportFileItem importFileItem) {
        this.importFileItem = importFileItem;
    }

    public ImportFileItemError importFileItem(ImportFileItem importFileItem) {
        this.setImportFileItem(importFileItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileItemError)) {
            return false;
        }
        return getId() != null && getId().equals(((ImportFileItemError) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileItemError{" +
            "id=" + getId() +
            ", columnIndex=" + getColumnIndex() +
            ", fieldName='" + getFieldName() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            "}";
    }
}
