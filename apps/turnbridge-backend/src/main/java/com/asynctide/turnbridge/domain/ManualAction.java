package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.ApprovalStatus;
import com.asynctide.turnbridge.domain.enumeration.ManualActionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Manual Resend / AssignNo 審核 / 變更記錄
 */
@Entity
@Table(name = "manual_action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ManualAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 操作類型
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ManualActionType actionType;

    /**
     * 原因
     */
    @NotNull
    @Size(max = 1024)
    @Column(name = "reason", length = 1024, nullable = false)
    private String reason;

    /**
     * 審核狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status;

    /**
     * 申請人
     */
    @Size(max = 64)
    @Column(name = "requested_by", length = 64)
    private String requestedBy;

    /**
     * 申請時間
     */
    @Column(name = "requested_at")
    private Instant requestedAt;

    /**
     * 核准人
     */
    @Size(max = 64)
    @Column(name = "approved_by", length = 64)
    private String approvedBy;

    /**
     * 核准時間
     */
    @Column(name = "approved_at")
    private Instant approvedAt;

    /**
     * 結果說明
     */
    @Column(name = "result_message")
    private String resultMessage;

    /**
     * 租戶
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "importFiles", "invoices", "webhookEndpoints" }, allowSetters = true)
    private Tenant tenant;

    /**
     * 發票主檔
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "importFile", "tenant" }, allowSetters = true)
    private Invoice invoice;

    /**
     * 匯入檔主檔
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private ImportFile importFile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ManualAction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManualActionType getActionType() {
        return this.actionType;
    }

    public ManualAction actionType(ManualActionType actionType) {
        this.setActionType(actionType);
        return this;
    }

    public void setActionType(ManualActionType actionType) {
        this.actionType = actionType;
    }

    public String getReason() {
        return this.reason;
    }

    public ManualAction reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApprovalStatus getStatus() {
        return this.status;
    }

    public ManualAction status(ApprovalStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return this.requestedBy;
    }

    public ManualAction requestedBy(String requestedBy) {
        this.setRequestedBy(requestedBy);
        return this;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return this.requestedAt;
    }

    public ManualAction requestedAt(Instant requestedAt) {
        this.setRequestedAt(requestedAt);
        return this;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getApprovedBy() {
        return this.approvedBy;
    }

    public ManualAction approvedBy(String approvedBy) {
        this.setApprovedBy(approvedBy);
        return this;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return this.approvedAt;
    }

    public ManualAction approvedAt(Instant approvedAt) {
        this.setApprovedAt(approvedAt);
        return this;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }

    public ManualAction resultMessage(String resultMessage) {
        this.setResultMessage(resultMessage);
        return this;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public ManualAction tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public ManualAction invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public ImportFile getImportFile() {
        return this.importFile;
    }

    public void setImportFile(ImportFile importFile) {
        this.importFile = importFile;
    }

    public ManualAction importFile(ImportFile importFile) {
        this.setImportFile(importFile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ManualAction)) {
            return false;
        }
        return getId() != null && getId().equals(((ManualAction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManualAction{" +
            "id=" + getId() +
            ", actionType='" + getActionType() + "'" +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", requestedBy='" + getRequestedBy() + "'" +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", approvedBy='" + getApprovedBy() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            ", resultMessage='" + getResultMessage() + "'" +
            "}";
    }
}
