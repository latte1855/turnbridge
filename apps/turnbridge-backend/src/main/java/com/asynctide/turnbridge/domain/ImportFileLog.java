package com.asynctide.turnbridge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 匯入批次事件紀錄（非逐行）
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
     * 事件代碼（如 PARSE_ERROR/NORMALIZE_SUMMARY）
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "event_code", length = 64, nullable = false)
    private String eventCode;

    /**
     * 層級（INFO/WARN/ERROR）
     */
    @NotNull
    @Size(max = 16)
    @Column(name = "level", length = 16, nullable = false)
    private String level;

    /**
     * 訊息摘要
     */
    @Size(max = 1024)
    @Column(name = "message", length = 1024)
    private String message;

    /**
     * 詳細內容（JSON 或堆疊）
     */
    @Column(name = "detail")
    private String detail;

    /**
     * 發生時間
     */
    @Column(name = "occurred_at")
    private Instant occurredAt;

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

    public String getEventCode() {
        return this.eventCode;
    }

    public ImportFileLog eventCode(String eventCode) {
        this.setEventCode(eventCode);
        return this;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getLevel() {
        return this.level;
    }

    public ImportFileLog level(String level) {
        this.setLevel(level);
        return this;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public String getDetail() {
        return this.detail;
    }

    public ImportFileLog detail(String detail) {
        this.setDetail(detail);
        return this;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public ImportFileLog occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
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
            ", eventCode='" + getEventCode() + "'" +
            ", level='" + getLevel() + "'" +
            ", message='" + getMessage() + "'" +
            ", detail='" + getDetail() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            "}";
    }
}
