package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.ApprovalStatus;
import com.asynctide.turnbridge.domain.enumeration.ManualActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.ManualAction} entity.
 */
@Schema(description = "Manual Resend / AssignNo 審核 / 變更記錄")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ManualActionDTO implements Serializable {

    private Long id;

    @NotNull
    @Schema(description = "操作類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private ManualActionType actionType;

    @NotNull
    @Size(max = 1024)
    @Schema(description = "原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @NotNull
    @Schema(description = "審核狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private ApprovalStatus status;

    @Size(max = 64)
    @Schema(description = "申請人")
    private String requestedBy;

    @Schema(description = "申請時間")
    private Instant requestedAt;

    @Size(max = 64)
    @Schema(description = "核准人")
    private String approvedBy;

    @Schema(description = "核准時間")
    private Instant approvedAt;

    @Schema(description = "結果說明")
    @Lob
    private String resultMessage;

    @NotNull
    @Schema(description = "租戶")
    private TenantDTO tenant;

    @Schema(description = "發票主檔")
    private InvoiceDTO invoice;

    @Schema(description = "匯入檔主檔")
    private ImportFileDTO importFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManualActionType getActionType() {
        return actionType;
    }

    public void setActionType(ManualActionType actionType) {
        this.actionType = actionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public TenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(TenantDTO tenant) {
        this.tenant = tenant;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    public ImportFileDTO getImportFile() {
        return importFile;
    }

    public void setImportFile(ImportFileDTO importFile) {
        this.importFile = importFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ManualActionDTO)) {
            return false;
        }

        ManualActionDTO manualActionDTO = (ManualActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, manualActionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManualActionDTO{" +
            "id=" + getId() +
            ", actionType='" + getActionType() + "'" +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", requestedBy='" + getRequestedBy() + "'" +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", approvedBy='" + getApprovedBy() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            ", resultMessage='" + getResultMessage() + "'" +
            ", tenant=" + getTenant() +
            ", invoice=" + getInvoice() +
            ", importFile=" + getImportFile() +
            "}";
    }
}
