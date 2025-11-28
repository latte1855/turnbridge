package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 匯入檔主檔（批次）
 */
@Entity
@Table(name = "import_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFile extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 匯入類型
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "import_type", nullable = false)
    private ImportType importType;

    /**
     * 原始檔名
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "original_filename", length = 255, nullable = false)
    private String originalFilename;

    /**
     * 檔案摘要（SHA-256）
     */
    @NotNull
    @Size(min = 64, max = 64)
    @Column(name = "sha_256", length = 64, nullable = false)
    private String sha256;

    /**
     * 總筆數
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    /**
     * 成功筆數
     */
    @Min(value = 0)
    @Column(name = "success_count")
    private Integer successCount;

    /**
     * 失敗筆數
     */
    @Min(value = 0)
    @Column(name = "error_count")
    private Integer errorCount;

    /**
     * 狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImportStatus status;

    /**
     * 原訊息別
     */
    @Size(max = 16)
    @Column(name = "legacy_type", length = 16)
    private String legacyType;

    /**
     * 租戶
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "importFiles", "invoices", "webhookEndpoints" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ImportFile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImportType getImportType() {
        return this.importType;
    }

    public ImportFile importType(ImportType importType) {
        this.setImportType(importType);
        return this;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }

    public String getOriginalFilename() {
        return this.originalFilename;
    }

    public ImportFile originalFilename(String originalFilename) {
        this.setOriginalFilename(originalFilename);
        return this;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getSha256() {
        return this.sha256;
    }

    public ImportFile sha256(String sha256) {
        this.setSha256(sha256);
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Integer getTotalCount() {
        return this.totalCount;
    }

    public ImportFile totalCount(Integer totalCount) {
        this.setTotalCount(totalCount);
        return this;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return this.successCount;
    }

    public ImportFile successCount(Integer successCount) {
        this.setSuccessCount(successCount);
        return this;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getErrorCount() {
        return this.errorCount;
    }

    public ImportFile errorCount(Integer errorCount) {
        this.setErrorCount(errorCount);
        return this;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public ImportStatus getStatus() {
        return this.status;
    }

    public ImportFile status(ImportStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public String getLegacyType() {
        return this.legacyType;
    }

    public ImportFile legacyType(String legacyType) {
        this.setLegacyType(legacyType);
        return this;
    }

    public void setLegacyType(String legacyType) {
        this.legacyType = legacyType;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public ImportFile tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFile)) {
            return false;
        }
        return getId() != null && getId().equals(((ImportFile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFile{" +
            "id=" + getId() +
            ", importType='" + getImportType() + "'" +
            ", originalFilename='" + getOriginalFilename() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", totalCount=" + getTotalCount() +
            ", successCount=" + getSuccessCount() +
            ", errorCount=" + getErrorCount() +
            ", status='" + getStatus() + "'" +
            ", legacyType='" + getLegacyType() + "'" +
            "}";
    }
}
