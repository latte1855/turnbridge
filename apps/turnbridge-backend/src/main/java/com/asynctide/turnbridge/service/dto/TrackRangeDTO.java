package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.TrackRangeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.TrackRange} entity.
 */
@Schema(description = "字軌配號區間（TrackRange）\n來源自 E0401；配號併發控制用 version 與（未來）Redis 鎖。")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackRangeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 32)
    @Schema(description = "賣方識別（統編/客戶 ID）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sellerId;

    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    @Schema(description = "期別 YYYYMM（雙月）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String period;

    @NotNull
    @Pattern(regexp = "[A-Z]{2}")
    @Schema(description = "字軌前綴（2 碼）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String prefix;

    @NotNull
    @Min(value = 1L)
    @Schema(description = "起號（含）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long startNo;

    @NotNull
    @Min(value = 1L)
    @Schema(description = "迄號（含）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long endNo;

    @NotNull
    @Min(value = 0L)
    @Schema(description = "目前已用到的號碼（0 表尚未使用）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long currentNo;

    @NotNull
    @Schema(description = "區間狀態（ACTIVE/EXHAUSTED/CLOSED）", requiredMode = Schema.RequiredMode.REQUIRED)
    private TrackRangeStatus status;

    @NotNull
    @Min(value = 0)
    @Schema(description = "樂觀鎖版本（每次取號遞增）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer version;

    @Size(max = 64)
    @Schema(description = "併發鎖擁有者（節點 ID 或執行緒記號；可選）")
    private String lockOwner;

    @Schema(description = "併發鎖時間（UTC；可選）")
    private Instant lockAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getStartNo() {
        return startNo;
    }

    public void setStartNo(Long startNo) {
        this.startNo = startNo;
    }

    public Long getEndNo() {
        return endNo;
    }

    public void setEndNo(Long endNo) {
        this.endNo = endNo;
    }

    public Long getCurrentNo() {
        return currentNo;
    }

    public void setCurrentNo(Long currentNo) {
        this.currentNo = currentNo;
    }

    public TrackRangeStatus getStatus() {
        return status;
    }

    public void setStatus(TrackRangeStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public Instant getLockAt() {
        return lockAt;
    }

    public void setLockAt(Instant lockAt) {
        this.lockAt = lockAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrackRangeDTO)) {
            return false;
        }

        TrackRangeDTO trackRangeDTO = (TrackRangeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trackRangeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackRangeDTO{" +
            "id=" + getId() +
            ", sellerId='" + getSellerId() + "'" +
            ", period='" + getPeriod() + "'" +
            ", prefix='" + getPrefix() + "'" +
            ", startNo=" + getStartNo() +
            ", endNo=" + getEndNo() +
            ", currentNo=" + getCurrentNo() +
            ", status='" + getStatus() + "'" +
            ", version=" + getVersion() +
            ", lockOwner='" + getLockOwner() + "'" +
            ", lockAt='" + getLockAt() + "'" +
            "}";
    }
}
