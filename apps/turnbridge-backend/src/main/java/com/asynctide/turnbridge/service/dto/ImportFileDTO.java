package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.ImportFile} entity.
 */
@Schema(description = "匯入檔主檔（批次）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileDTO implements Serializable {

    private Long id;

    @NotNull
    @Schema(description = "匯入類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private ImportType importType;

    @NotNull
    @Size(max = 255)
    @Schema(description = "原始檔名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String originalFilename;

    @NotNull
    @Size(min = 64, max = 64)
    @Schema(description = "檔案摘要（SHA-256）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sha256;

    @NotNull
    @Min(value = 0)
    @Schema(description = "總筆數", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Min(value = 0)
    @Schema(description = "成功筆數")
    private Integer successCount;

    @Min(value = 0)
    @Schema(description = "失敗筆數")
    private Integer errorCount;

    @NotNull
    @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private ImportStatus status;

    @Size(max = 16)
    @Schema(description = "原訊息別")
    private String legacyType;

    @Schema(description = "租戶")
    private TenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImportType getImportType() {
        return importType;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public String getLegacyType() {
        return legacyType;
    }

    public void setLegacyType(String legacyType) {
        this.legacyType = legacyType;
    }

    public TenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(TenantDTO tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileDTO)) {
            return false;
        }

        ImportFileDTO importFileDTO = (ImportFileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, importFileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileDTO{" +
            "id=" + getId() +
            ", importType='" + getImportType() + "'" +
            ", originalFilename='" + getOriginalFilename() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", totalCount=" + getTotalCount() +
            ", successCount=" + getSuccessCount() +
            ", errorCount=" + getErrorCount() +
            ", status='" + getStatus() + "'" +
            ", legacyType='" + getLegacyType() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
