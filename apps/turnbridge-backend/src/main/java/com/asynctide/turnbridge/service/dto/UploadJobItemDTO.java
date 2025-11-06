package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.domain.enumeration.TaxType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.UploadJobItem} entity.
 */
@Schema(description = "上傳批次明細（UploadJobItem）\n對應原始 CSV 每一行；保留處理結果、關鍵欄位與原始 payload（利於重送/追蹤）。")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadJobItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Schema(description = "原始檔行號（1-based）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lineNo;

    @NotNull
    @Size(min = 8, max = 64)
    @Schema(description = "全流程追蹤 ID（系統生成；jobId 衍生）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String traceId;

    @NotNull
    @Schema(description = "處理狀態（QUEUED/OK/ERROR）", requiredMode = Schema.RequiredMode.REQUIRED)
    private JobItemStatus status;

    @Size(min = 2, max = 16)
    @Schema(description = "錯誤碼（如 0402 欄位缺漏、0510 金額不平…）")
    private String resultCode;

    @Size(max = 2048)
    @Schema(description = "錯誤訊息（可讀）")
    private String resultMsg;

    @Size(max = 64)
    @Schema(description = "買方統編/識別碼（B2C 可留空）")
    private String buyerId;

    @Size(max = 128)
    @Schema(description = "買方名稱（B2C 常用）")
    private String buyerName;

    @Size(max = 3)
    @Schema(description = "幣別（ISO 4217；預設 TWD）")
    private String currency;

    @Schema(description = "未稅金額（小數 2~4 位；實際精度依 DB 設定）")
    private BigDecimal amountExcl;

    @Schema(description = "稅額")
    private BigDecimal taxAmount;

    @Schema(description = "含稅金額")
    private BigDecimal amountIncl;

    @Schema(description = "稅別（應稅/零稅/免稅）")
    private TaxType taxType;

    @Schema(description = "發票日期（來源資料或預計開立日期）")
    private LocalDate invoiceDate;

    @Size(max = 16)
    @Schema(description = "指定或已分配的發票號碼（可為空）")
    private String invoiceNo;

    @Pattern(regexp = "[A-Z]{2}")
    @Schema(description = "已分配字軌前綴（2 碼；如 AB）")
    private String assignedPrefix;

    @Schema(description = "原始欄位 JSON（保留上傳原貌，利於重送/核對）")
    @Lob
    private String rawPayload;

    @Size(min = 64, max = 64)
    @Schema(description = "原始資料 SHA-256（重送冪等等用途）")
    private String rawHash;

    @Size(max = 64)
    @Schema(description = "系統自動判定之 Profile（如未指定時）")
    private String profileDetected;

    @NotNull
    private UploadJobDTO job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public JobItemStatus getStatus() {
        return status;
    }

    public void setStatus(JobItemStatus status) {
        this.status = status;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmountExcl() {
        return amountExcl;
    }

    public void setAmountExcl(BigDecimal amountExcl) {
        this.amountExcl = amountExcl;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getAmountIncl() {
        return amountIncl;
    }

    public void setAmountIncl(BigDecimal amountIncl) {
        this.amountIncl = amountIncl;
    }

    public TaxType getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getAssignedPrefix() {
        return assignedPrefix;
    }

    public void setAssignedPrefix(String assignedPrefix) {
        this.assignedPrefix = assignedPrefix;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getRawHash() {
        return rawHash;
    }

    public void setRawHash(String rawHash) {
        this.rawHash = rawHash;
    }

    public String getProfileDetected() {
        return profileDetected;
    }

    public void setProfileDetected(String profileDetected) {
        this.profileDetected = profileDetected;
    }

    public UploadJobDTO getJob() {
        return job;
    }

    public void setJob(UploadJobDTO job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadJobItemDTO)) {
            return false;
        }

        UploadJobItemDTO uploadJobItemDTO = (UploadJobItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, uploadJobItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadJobItemDTO{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", traceId='" + getTraceId() + "'" +
            ", status='" + getStatus() + "'" +
            ", resultCode='" + getResultCode() + "'" +
            ", resultMsg='" + getResultMsg() + "'" +
            ", buyerId='" + getBuyerId() + "'" +
            ", buyerName='" + getBuyerName() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", amountExcl=" + getAmountExcl() +
            ", taxAmount=" + getTaxAmount() +
            ", amountIncl=" + getAmountIncl() +
            ", taxType='" + getTaxType() + "'" +
            ", invoiceDate='" + getInvoiceDate() + "'" +
            ", invoiceNo='" + getInvoiceNo() + "'" +
            ", assignedPrefix='" + getAssignedPrefix() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", rawHash='" + getRawHash() + "'" +
            ", profileDetected='" + getProfileDetected() + "'" +
            ", job=" + getJob() +
            "}";
    }
}
