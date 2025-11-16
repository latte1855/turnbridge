package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.InvoiceAssignNo} entity.
 */
@Schema(description = "號段配號管理（E0401）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceAssignNoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 2)
    @Schema(description = "字軌", requiredMode = Schema.RequiredMode.REQUIRED)
    private String track;

    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    @Schema(description = "期別", requiredMode = Schema.RequiredMode.REQUIRED)
    private String period;

    @NotNull
    @Size(max = 10)
    @Schema(description = "起號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fromNo;

    @NotNull
    @Size(max = 10)
    @Schema(description = "迄號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String toNo;

    @Min(value = 0)
    @Schema(description = "已用數")
    private Integer usedCount;

    @Min(value = 0)
    @Schema(description = "卷大小")
    private Integer rollSize;

    @Size(max = 32)
    @Schema(description = "狀態")
    private String status;

    @NotNull
    @Schema(description = "租戶")
    private TenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getFromNo() {
        return fromNo;
    }

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Integer getRollSize() {
        return rollSize;
    }

    public void setRollSize(Integer rollSize) {
        this.rollSize = rollSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!(o instanceof InvoiceAssignNoDTO)) {
            return false;
        }

        InvoiceAssignNoDTO invoiceAssignNoDTO = (InvoiceAssignNoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceAssignNoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceAssignNoDTO{" +
            "id=" + getId() +
            ", track='" + getTrack() + "'" +
            ", period='" + getPeriod() + "'" +
            ", fromNo='" + getFromNo() + "'" +
            ", toNo='" + getToNo() + "'" +
            ", usedCount=" + getUsedCount() +
            ", rollSize=" + getRollSize() +
            ", status='" + getStatus() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
