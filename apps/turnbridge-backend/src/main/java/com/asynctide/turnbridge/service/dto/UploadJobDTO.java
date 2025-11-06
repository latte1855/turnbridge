package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.UploadJob} entity.
 */
@Schema(description = "上傳批次主檔（UploadJob）\n追蹤每次上傳的狀態、統計、來源檔資訊與回饋檔關聯。")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadJobDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 8, max = 64)
    @Schema(description = "批次識別碼（系統產生，對外曝光）；唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobId;

    @NotNull
    @Size(min = 3, max = 32)
    @Schema(description = "賣方統編或客戶識別碼（上傳端提供）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sellerId;

    @Size(max = 128)
    @Schema(description = "賣方名稱（可選；來自客戶資料或 CSV 抬頭）")
    private String sellerName;

    @Pattern(regexp = "[0-9]{6}")
    @Schema(description = "期別 YYYYMM（雙月分區用；可選）")
    private String period;

    @Size(min = 3, max = 64)
    @Schema(description = "解析 Profile 名稱（Legacy/Canonical 或其他）")
    private String profile;

    @Size(max = 255)
    @Schema(description = "原始上傳檔案名稱（僅中繼資訊，實體位於 StoredObject）")
    private String sourceFilename;

    @Size(max = 128)
    @Schema(description = "原始上傳檔案 MIME 類型（text/csv、application/zip…）")
    private String sourceMediaType;

    @NotNull
    @Schema(description = "批次處理狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private UploadJobStatus status;

    @NotNull
    @Min(value = 0)
    @Schema(description = "批次總筆數", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer total;

    @NotNull
    @Min(value = 0)
    @Schema(description = "驗證通過數", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer accepted;

    @NotNull
    @Min(value = 0)
    @Schema(description = "驗證失敗數", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer failed;

    @NotNull
    @Min(value = 0)
    @Schema(description = "已送 Turnkey 數", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sent;

    @Size(max = 1024)
    @Schema(description = "備註（營運人員補充或系統說明）")
    private String remark;

    @NotNull
    private StoredObjectDTO originalFile;

    private StoredObjectDTO resultFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public String getSourceMediaType() {
        return sourceMediaType;
    }

    public void setSourceMediaType(String sourceMediaType) {
        this.sourceMediaType = sourceMediaType;
    }

    public UploadJobStatus getStatus() {
        return status;
    }

    public void setStatus(UploadJobStatus status) {
        this.status = status;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getAccepted() {
        return accepted;
    }

    public void setAccepted(Integer accepted) {
        this.accepted = accepted;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getSent() {
        return sent;
    }

    public void setSent(Integer sent) {
        this.sent = sent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public StoredObjectDTO getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(StoredObjectDTO originalFile) {
        this.originalFile = originalFile;
    }

    public StoredObjectDTO getResultFile() {
        return resultFile;
    }

    public void setResultFile(StoredObjectDTO resultFile) {
        this.resultFile = resultFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadJobDTO)) {
            return false;
        }

        UploadJobDTO uploadJobDTO = (UploadJobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, uploadJobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadJobDTO{" +
            "id=" + getId() +
            ", jobId='" + getJobId() + "'" +
            ", sellerId='" + getSellerId() + "'" +
            ", sellerName='" + getSellerName() + "'" +
            ", period='" + getPeriod() + "'" +
            ", profile='" + getProfile() + "'" +
            ", sourceFilename='" + getSourceFilename() + "'" +
            ", sourceMediaType='" + getSourceMediaType() + "'" +
            ", status='" + getStatus() + "'" +
            ", total=" + getTotal() +
            ", accepted=" + getAccepted() +
            ", failed=" + getFailed() +
            ", sent=" + getSent() +
            ", remark='" + getRemark() + "'" +
            ", originalFile=" + getOriginalFile() +
            ", resultFile=" + getResultFile() +
            "}";
    }
}
