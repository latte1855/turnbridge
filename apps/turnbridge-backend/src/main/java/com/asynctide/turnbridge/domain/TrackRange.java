package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.TrackRangeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 字軌配號區間（TrackRange）
 * 來源自 E0401；配號併發控制用 version 與（未來）Redis 鎖。
 */
@Entity
@Table(name = "track_range")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackRange extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 賣方識別（統編/客戶 ID）
     */
    @NotNull
    @Size(min = 3, max = 32)
    @Column(name = "seller_id", length = 32, nullable = false)
    private String sellerId;

    /**
     * 期別 YYYYMM（雙月）
     */
    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    @Column(name = "period", nullable = false)
    private String period;

    /**
     * 字軌前綴（2 碼）
     */
    @NotNull
    @Pattern(regexp = "[A-Z]{2}")
    @Column(name = "prefix", nullable = false)
    private String prefix;

    /**
     * 起號（含）
     */
    @NotNull
    @Min(value = 1L)
    @Column(name = "start_no", nullable = false)
    private Long startNo;

    /**
     * 迄號（含）
     */
    @NotNull
    @Min(value = 1L)
    @Column(name = "end_no", nullable = false)
    private Long endNo;

    /**
     * 目前已用到的號碼（0 表尚未使用）
     */
    @NotNull
    @Min(value = 0L)
    @Column(name = "current_no", nullable = false)
    private Long currentNo;

    /**
     * 區間狀態（ACTIVE/EXHAUSTED/CLOSED）
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrackRangeStatus status;

    /**
     * 樂觀鎖版本（每次取號遞增）
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * 併發鎖擁有者（節點 ID 或執行緒記號；可選）
     */
    @Size(max = 64)
    @Column(name = "lock_owner", length = 64)
    private String lockOwner;

    /**
     * 併發鎖時間（UTC；可選）
     */
    @Column(name = "lock_at")
    private Instant lockAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrackRange id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellerId() {
        return this.sellerId;
    }

    public TrackRange sellerId(String sellerId) {
        this.setSellerId(sellerId);
        return this;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPeriod() {
        return this.period;
    }

    public TrackRange period(String period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public TrackRange prefix(String prefix) {
        this.setPrefix(prefix);
        return this;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getStartNo() {
        return this.startNo;
    }

    public TrackRange startNo(Long startNo) {
        this.setStartNo(startNo);
        return this;
    }

    public void setStartNo(Long startNo) {
        this.startNo = startNo;
    }

    public Long getEndNo() {
        return this.endNo;
    }

    public TrackRange endNo(Long endNo) {
        this.setEndNo(endNo);
        return this;
    }

    public void setEndNo(Long endNo) {
        this.endNo = endNo;
    }

    public Long getCurrentNo() {
        return this.currentNo;
    }

    public TrackRange currentNo(Long currentNo) {
        this.setCurrentNo(currentNo);
        return this;
    }

    public void setCurrentNo(Long currentNo) {
        this.currentNo = currentNo;
    }

    public TrackRangeStatus getStatus() {
        return this.status;
    }

    public TrackRange status(TrackRangeStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TrackRangeStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return this.version;
    }

    public TrackRange version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getLockOwner() {
        return this.lockOwner;
    }

    public TrackRange lockOwner(String lockOwner) {
        this.setLockOwner(lockOwner);
        return this;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public Instant getLockAt() {
        return this.lockAt;
    }

    public TrackRange lockAt(Instant lockAt) {
        this.setLockAt(lockAt);
        return this;
    }

    public void setLockAt(Instant lockAt) {
        this.lockAt = lockAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrackRange)) {
            return false;
        }
        return getId() != null && getId().equals(((TrackRange) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackRange{" +
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
