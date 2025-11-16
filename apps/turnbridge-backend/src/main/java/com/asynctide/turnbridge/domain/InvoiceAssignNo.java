package com.asynctide.turnbridge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 號段配號管理（E0401）
 */
@Entity
@Table(name = "invoice_assign_no")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceAssignNo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 字軌
     */
    @NotNull
    @Size(max = 2)
    @Column(name = "track", length = 2, nullable = false)
    private String track;

    /**
     * 期別
     */
    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    @Column(name = "period", nullable = false)
    private String period;

    /**
     * 起號
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "from_no", length = 10, nullable = false)
    private String fromNo;

    /**
     * 迄號
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "to_no", length = 10, nullable = false)
    private String toNo;

    /**
     * 已用數
     */
    @Min(value = 0)
    @Column(name = "used_count")
    private Integer usedCount;

    /**
     * 卷大小
     */
    @Min(value = 0)
    @Column(name = "roll_size")
    private Integer rollSize;

    /**
     * 狀態
     */
    @Size(max = 32)
    @Column(name = "status", length = 32)
    private String status;

    /**
     * 租戶
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "importFiles", "invoices", "webhookEndpoints" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InvoiceAssignNo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrack() {
        return this.track;
    }

    public InvoiceAssignNo track(String track) {
        this.setTrack(track);
        return this;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getPeriod() {
        return this.period;
    }

    public InvoiceAssignNo period(String period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getFromNo() {
        return this.fromNo;
    }

    public InvoiceAssignNo fromNo(String fromNo) {
        this.setFromNo(fromNo);
        return this;
    }

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public String getToNo() {
        return this.toNo;
    }

    public InvoiceAssignNo toNo(String toNo) {
        this.setToNo(toNo);
        return this;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public Integer getUsedCount() {
        return this.usedCount;
    }

    public InvoiceAssignNo usedCount(Integer usedCount) {
        this.setUsedCount(usedCount);
        return this;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Integer getRollSize() {
        return this.rollSize;
    }

    public InvoiceAssignNo rollSize(Integer rollSize) {
        this.setRollSize(rollSize);
        return this;
    }

    public void setRollSize(Integer rollSize) {
        this.rollSize = rollSize;
    }

    public String getStatus() {
        return this.status;
    }

    public InvoiceAssignNo status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public InvoiceAssignNo tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceAssignNo)) {
            return false;
        }
        return getId() != null && getId().equals(((InvoiceAssignNo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceAssignNo{" +
            "id=" + getId() +
            ", track='" + getTrack() + "'" +
            ", period='" + getPeriod() + "'" +
            ", fromNo='" + getFromNo() + "'" +
            ", toNo='" + getToNo() + "'" +
            ", usedCount=" + getUsedCount() +
            ", rollSize=" + getRollSize() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
