package com.asynctide.turnbridge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 匯入錯誤/訊息紀錄
 */
@Entity
@Table(name = "import_file_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 行號
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "line_index", nullable = false)
    private Integer lineIndex;

    /**
     * 欄位名稱
     */
    @Size(max = 64)
    @Column(name = "field", length = 64)
    private String field;

    /**
     * 錯誤碼
     */
    @NotNull
    @Size(max = 32)
    @Column(name = "error_code", length = 32, nullable = false)
    private String errorCode;

    /**
     * 錯誤訊息
     */
    @Size(max = 1024)
    @Column(name = "message", length = 1024)
    private String message;

    /**
     * 原始行
     */
    @Column(name = "raw_line")
    private String rawLine;

    /**
     * 原訊息別
     */
    @Size(max = 16)
    @Column(name = "source_family", length = 16)
    private String sourceFamily;

    /**
     * 正規化訊息別
     */
    @Size(max = 16)
    @Column(name = "normalized_family", length = 16)
    private String normalizedFamily;

    /**
     * 匯入檔主檔
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private ImportFile importFile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ImportFileLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineIndex() {
        return this.lineIndex;
    }

    public ImportFileLog lineIndex(Integer lineIndex) {
        this.setLineIndex(lineIndex);
        return this;
    }

    public void setLineIndex(Integer lineIndex) {
        this.lineIndex = lineIndex;
    }

    public String getField() {
        return this.field;
    }

    public ImportFileLog field(String field) {
        this.setField(field);
        return this;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public ImportFileLog errorCode(String errorCode) {
        this.setErrorCode(errorCode);
        return this;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public ImportFileLog message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRawLine() {
        return this.rawLine;
    }

    public ImportFileLog rawLine(String rawLine) {
        this.setRawLine(rawLine);
        return this;
    }

    public void setRawLine(String rawLine) {
        this.rawLine = rawLine;
    }

    public String getSourceFamily() {
        return this.sourceFamily;
    }

    public ImportFileLog sourceFamily(String sourceFamily) {
        this.setSourceFamily(sourceFamily);
        return this;
    }

    public void setSourceFamily(String sourceFamily) {
        this.sourceFamily = sourceFamily;
    }

    public String getNormalizedFamily() {
        return this.normalizedFamily;
    }

    public ImportFileLog normalizedFamily(String normalizedFamily) {
        this.setNormalizedFamily(normalizedFamily);
        return this;
    }

    public void setNormalizedFamily(String normalizedFamily) {
        this.normalizedFamily = normalizedFamily;
    }

    public ImportFile getImportFile() {
        return this.importFile;
    }

    public void setImportFile(ImportFile importFile) {
        this.importFile = importFile;
    }

    public ImportFileLog importFile(ImportFile importFile) {
        this.setImportFile(importFile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileLog)) {
            return false;
        }
        return getId() != null && getId().equals(((ImportFileLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileLog{" +
            "id=" + getId() +
            ", lineIndex=" + getLineIndex() +
            ", field='" + getField() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", rawLine='" + getRawLine() + "'" +
            ", sourceFamily='" + getSourceFamily() + "'" +
            ", normalizedFamily='" + getNormalizedFamily() + "'" +
            "}";
    }
}
