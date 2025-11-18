package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 匯入檔明細（逐行資料，儲存原始欄位與檢核結果）
 */
@Entity
@Table(name = "import_file_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 行號（1-based）
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "line_index", nullable = false)
    private Integer lineIndex;

    /**
     * 原始資料（JSON 或原始 CSV 行）
     */
    @NotNull
    @Column(name = "raw_data", nullable = false)
    private String rawData;

    /**
     * 原始資料 SHA-256（利於去重、結果 append）
     */
    @Size(min = 64, max = 64)
    @Column(name = "raw_hash", length = 64)
    private String rawHash;

    /**
     * 來源訊息別（A0401/B0401…）
     */
    @Size(max = 16)
    @Column(name = "source_family", length = 16)
    private String sourceFamily;

    /**
     * 目標訊息別（F/G 系）
     */
    @Size(max = 16)
    @Column(name = "normalized_family", length = 16)
    private String normalizedFamily;

    /**
     * 正規化 JSON（成功時保留）
     */
    @Column(name = "normalized_json")
    private String normalizedJson;

    /**
     * 處理狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImportItemStatus status;

    /**
     * 錯誤碼（失敗時）
     */
    @Size(max = 32)
    @Column(name = "error_code", length = 32)
    private String errorCode;

    /**
     * 錯誤訊息（失敗時）
     */
    @Size(max = 1024)
    @Column(name = "error_message", length = 1024)
    private String errorMessage;

    /**
     * 匯入檔主檔
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private ImportFile importFile;

    /**
     * 發票主檔
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "importFile", "tenant" }, allowSetters = true)
    private Invoice invoice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ImportFileItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineIndex() {
        return this.lineIndex;
    }

    public ImportFileItem lineIndex(Integer lineIndex) {
        this.setLineIndex(lineIndex);
        return this;
    }

    public void setLineIndex(Integer lineIndex) {
        this.lineIndex = lineIndex;
    }

    public String getRawData() {
        return this.rawData;
    }

    public ImportFileItem rawData(String rawData) {
        this.setRawData(rawData);
        return this;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getRawHash() {
        return this.rawHash;
    }

    public ImportFileItem rawHash(String rawHash) {
        this.setRawHash(rawHash);
        return this;
    }

    public void setRawHash(String rawHash) {
        this.rawHash = rawHash;
    }

    public String getSourceFamily() {
        return this.sourceFamily;
    }

    public ImportFileItem sourceFamily(String sourceFamily) {
        this.setSourceFamily(sourceFamily);
        return this;
    }

    public void setSourceFamily(String sourceFamily) {
        this.sourceFamily = sourceFamily;
    }

    public String getNormalizedFamily() {
        return this.normalizedFamily;
    }

    public ImportFileItem normalizedFamily(String normalizedFamily) {
        this.setNormalizedFamily(normalizedFamily);
        return this;
    }

    public void setNormalizedFamily(String normalizedFamily) {
        this.normalizedFamily = normalizedFamily;
    }

    public String getNormalizedJson() {
        return this.normalizedJson;
    }

    public ImportFileItem normalizedJson(String normalizedJson) {
        this.setNormalizedJson(normalizedJson);
        return this;
    }

    public void setNormalizedJson(String normalizedJson) {
        this.normalizedJson = normalizedJson;
    }

    public ImportItemStatus getStatus() {
        return this.status;
    }

    public ImportFileItem status(ImportItemStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ImportItemStatus status) {
        this.status = status;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public ImportFileItem errorCode(String errorCode) {
        this.setErrorCode(errorCode);
        return this;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public ImportFileItem errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ImportFile getImportFile() {
        return this.importFile;
    }

    public void setImportFile(ImportFile importFile) {
        this.importFile = importFile;
    }

    public ImportFileItem importFile(ImportFile importFile) {
        this.setImportFile(importFile);
        return this;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public ImportFileItem invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ImportFileItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileItem{" +
            "id=" + getId() +
            ", lineIndex=" + getLineIndex() +
            ", rawData='" + getRawData() + "'" +
            ", rawHash='" + getRawHash() + "'" +
            ", sourceFamily='" + getSourceFamily() + "'" +
            ", normalizedFamily='" + getNormalizedFamily() + "'" +
            ", normalizedJson='" + getNormalizedJson() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            "}";
    }
}
