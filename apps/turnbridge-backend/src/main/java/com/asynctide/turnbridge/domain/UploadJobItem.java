package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.domain.enumeration.TaxType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 上傳批次明細（UploadJobItem）
 * 對應原始 CSV 每一行；保留處理結果、關鍵欄位與原始 payload（利於重送/追蹤）。
 */
@Entity
@Table(name = "upload_job_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadJobItem extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 原始檔行號（1-based）
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "line_no", nullable = false)
    private Integer lineNo;

    /**
     * 全流程追蹤 ID（系統生成；jobId 衍生）
     */
    @NotNull
    @Size(min = 8, max = 64)
    @Column(name = "trace_id", length = 64, nullable = false)
    private String traceId;

    /**
     * 處理狀態（QUEUED/OK/ERROR）
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobItemStatus status;

    /**
     * 錯誤碼（如 0402 欄位缺漏、0510 金額不平…）
     */
    @Size(min = 2, max = 16)
    @Column(name = "result_code", length = 16)
    private String resultCode;

    /**
     * 錯誤訊息（可讀）
     */
    @Size(max = 2048)
    @Column(name = "result_msg", length = 2048)
    private String resultMsg;

    /**
     * 買方統編/識別碼（B2C 可留空）
     */
    @Size(max = 64)
    @Column(name = "buyer_id", length = 64)
    private String buyerId;

    /**
     * 買方名稱（B2C 常用）
     */
    @Size(max = 128)
    @Column(name = "buyer_name", length = 128)
    private String buyerName;

    /**
     * 幣別（ISO 4217；預設 TWD）
     */
    @Size(max = 3)
    @Column(name = "currency", length = 3)
    private String currency;

    /**
     * 未稅金額（小數 2~4 位；實際精度依 DB 設定）
     */
    @Column(name = "amount_excl", precision = 21, scale = 2)
    private BigDecimal amountExcl;

    /**
     * 稅額
     */
    @Column(name = "tax_amount", precision = 21, scale = 2)
    private BigDecimal taxAmount;

    /**
     * 含稅金額
     */
    @Column(name = "amount_incl", precision = 21, scale = 2)
    private BigDecimal amountIncl;

    /**
     * 稅別（應稅/零稅/免稅）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type")
    private TaxType taxType;

    /**
     * 發票日期（來源資料或預計開立日期）
     */
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    /**
     * 指定或已分配的發票號碼（可為空）
     */
    @Size(max = 16)
    @Column(name = "invoice_no", length = 16)
    private String invoiceNo;

    /**
     * 已分配字軌前綴（2 碼；如 AB）
     */
    @Pattern(regexp = "[A-Z]{2}")
    @Column(name = "assigned_prefix")
    private String assignedPrefix;

    /**
     * 原始欄位 JSON（保留上傳原貌，利於重送/核對）
     */
    // PostgreSQL 沒有 CLOB，避免 @Lob 走 getClob 路
    //@Lob
    @Column(name = "raw_payload")
    private String rawPayload;

    /**
     * 原始資料 SHA-256（重送冪等等用途）
     */
    @Size(min = 64, max = 64)
    @Column(name = "raw_hash", length = 64)
    private String rawHash;

    /**
     * 系統自動判定之 Profile（如未指定時）
     */
    @Size(max = 64)
    @Column(name = "profile_detected", length = 64)
    private String profileDetected;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "items", "originalFile", "resultFile" }, allowSetters = true)
    private UploadJob job;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UploadJobItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return this.lineNo;
    }

    public UploadJobItem lineNo(Integer lineNo) {
        this.setLineNo(lineNo);
        return this;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public UploadJobItem traceId(String traceId) {
        this.setTraceId(traceId);
        return this;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public JobItemStatus getStatus() {
        return this.status;
    }

    public UploadJobItem status(JobItemStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(JobItemStatus status) {
        this.status = status;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public UploadJobItem resultCode(String resultCode) {
        this.setResultCode(resultCode);
        return this;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return this.resultMsg;
    }

    public UploadJobItem resultMsg(String resultMsg) {
        this.setResultMsg(resultMsg);
        return this;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getBuyerId() {
        return this.buyerId;
    }

    public UploadJobItem buyerId(String buyerId) {
        this.setBuyerId(buyerId);
        return this;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return this.buyerName;
    }

    public UploadJobItem buyerName(String buyerName) {
        this.setBuyerName(buyerName);
        return this;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getCurrency() {
        return this.currency;
    }

    public UploadJobItem currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmountExcl() {
        return this.amountExcl;
    }

    public UploadJobItem amountExcl(BigDecimal amountExcl) {
        this.setAmountExcl(amountExcl);
        return this;
    }

    public void setAmountExcl(BigDecimal amountExcl) {
        this.amountExcl = amountExcl;
    }

    public BigDecimal getTaxAmount() {
        return this.taxAmount;
    }

    public UploadJobItem taxAmount(BigDecimal taxAmount) {
        this.setTaxAmount(taxAmount);
        return this;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getAmountIncl() {
        return this.amountIncl;
    }

    public UploadJobItem amountIncl(BigDecimal amountIncl) {
        this.setAmountIncl(amountIncl);
        return this;
    }

    public void setAmountIncl(BigDecimal amountIncl) {
        this.amountIncl = amountIncl;
    }

    public TaxType getTaxType() {
        return this.taxType;
    }

    public UploadJobItem taxType(TaxType taxType) {
        this.setTaxType(taxType);
        return this;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    public LocalDate getInvoiceDate() {
        return this.invoiceDate;
    }

    public UploadJobItem invoiceDate(LocalDate invoiceDate) {
        this.setInvoiceDate(invoiceDate);
        return this;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNo() {
        return this.invoiceNo;
    }

    public UploadJobItem invoiceNo(String invoiceNo) {
        this.setInvoiceNo(invoiceNo);
        return this;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getAssignedPrefix() {
        return this.assignedPrefix;
    }

    public UploadJobItem assignedPrefix(String assignedPrefix) {
        this.setAssignedPrefix(assignedPrefix);
        return this;
    }

    public void setAssignedPrefix(String assignedPrefix) {
        this.assignedPrefix = assignedPrefix;
    }

    public String getRawPayload() {
        return this.rawPayload;
    }

    public UploadJobItem rawPayload(String rawPayload) {
        this.setRawPayload(rawPayload);
        return this;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getRawHash() {
        return this.rawHash;
    }

    public UploadJobItem rawHash(String rawHash) {
        this.setRawHash(rawHash);
        return this;
    }

    public void setRawHash(String rawHash) {
        this.rawHash = rawHash;
    }

    public String getProfileDetected() {
        return this.profileDetected;
    }

    public UploadJobItem profileDetected(String profileDetected) {
        this.setProfileDetected(profileDetected);
        return this;
    }

    public void setProfileDetected(String profileDetected) {
        this.profileDetected = profileDetected;
    }

    public UploadJob getJob() {
        return this.job;
    }

    public void setJob(UploadJob uploadJob) {
        this.job = uploadJob;
    }

    public UploadJobItem job(UploadJob uploadJob) {
        this.setJob(uploadJob);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadJobItem)) {
            return false;
        }
        return getId() != null && getId().equals(((UploadJobItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadJobItem{" +
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
            "}";
    }
}
