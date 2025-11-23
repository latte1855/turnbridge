package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 發票/折讓資料主檔（Normalized）
 */
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 發票號碼
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "invoice_no", length = 20, nullable = false)
    private String invoiceNo;

    /**
     * 訊息家族
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "message_family", nullable = false)
    private MessageFamily messageFamily;

    /**
     * 買方識別碼（統編或載具）
     */
    @Size(max = 64)
    @Column(name = "buyer_id", length = 64)
    private String buyerId;

    /**
     * 買方名稱
     */
    @Size(max = 128)
    @Column(name = "buyer_name", length = 128)
    private String buyerName;

    /**
     * 賣方統編
     */
    @Size(max = 64)
    @Column(name = "seller_id", length = 64)
    private String sellerId;

    /**
     * 賣方名稱
     */
    @Size(max = 128)
    @Column(name = "seller_name", length = 128)
    private String sellerName;

    /**
     * 銷售金額
     */
    @Column(name = "sales_amount", precision = 21, scale = 2)
    private BigDecimal salesAmount;

    /**
     * 稅額
     */
    @Column(name = "tax_amount", precision = 21, scale = 2)
    private BigDecimal taxAmount;

    /**
     * 含稅金額
     */
    @Column(name = "total_amount", precision = 21, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 稅別
     */
    @Size(max = 16)
    @Column(name = "tax_type", length = 16)
    private String taxType;

    /**
     * 正規化 JSON
     */
    @Column(name = "normalized_json")
    private String normalizedJson;

    /**
     * 發票狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false)
    private InvoiceStatus invoiceStatus;

    /**
     * 開立時間
     */
    @Column(name = "issued_at")
    private Instant issuedAt;

    /**
     * 原訊息別
     */
    @Size(max = 16)
    @Column(name = "legacy_type", length = 16)
    private String legacyType;

    /**
     * Turnkey TB-xxxx 錯誤碼
     */
    @Size(max = 32)
    @Column(name = "tb_code", length = 32)
    private String tbCode;

    /**
     * TB 錯誤分類（例：PLATFORM.DATA_AMOUNT_MISMATCH）
     */
    @Size(max = 128)
    @Column(name = "tb_category", length = 128)
    private String tbCategory;

    /**
     * 是否允許系統自動重送
     */
    @Column(name = "tb_can_auto_retry")
    private Boolean tbCanAutoRetry;

    /**
     * 建議營運處置（FIX_DATA / FIX_LIFECYCLE_FLOW / CHECK_PLATFORM）
     */
    @Size(max = 64)
    @Column(name = "tb_recommended_action", length = 64)
    private String tbRecommendedAction;

    /**
     * 平台原始錯誤碼（ProcessResult ErrorCode）
     */
    @Size(max = 64)
    @Column(name = "tb_source_code", length = 64)
    private String tbSourceCode;

    /**
     * 平台錯誤訊息
     */
    @Size(max = 1024)
    @Column(name = "tb_source_message", length = 1024)
    private String tbSourceMessage;

    /**
     * ProcessResult ResultCode（0=成功）
     */
    @Size(max = 16)
    @Column(name = "tb_result_code", length = 16)
    private String tbResultCode;

    /**
     * 匯入檔主檔
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private ImportFile importFile;

    /**
     * 租戶
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "importFiles", "invoices", "webhookEndpoints" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Invoice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNo() {
        return this.invoiceNo;
    }

    public Invoice invoiceNo(String invoiceNo) {
        this.setInvoiceNo(invoiceNo);
        return this;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public MessageFamily getMessageFamily() {
        return this.messageFamily;
    }

    public Invoice messageFamily(MessageFamily messageFamily) {
        this.setMessageFamily(messageFamily);
        return this;
    }

    public void setMessageFamily(MessageFamily messageFamily) {
        this.messageFamily = messageFamily;
    }

    public String getBuyerId() {
        return this.buyerId;
    }

    public Invoice buyerId(String buyerId) {
        this.setBuyerId(buyerId);
        return this;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return this.buyerName;
    }

    public Invoice buyerName(String buyerName) {
        this.setBuyerName(buyerName);
        return this;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerId() {
        return this.sellerId;
    }

    public Invoice sellerId(String sellerId) {
        this.setSellerId(sellerId);
        return this;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    public Invoice sellerName(String sellerName) {
        this.setSellerName(sellerName);
        return this;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public BigDecimal getSalesAmount() {
        return this.salesAmount;
    }

    public Invoice salesAmount(BigDecimal salesAmount) {
        this.setSalesAmount(salesAmount);
        return this;
    }

    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    public BigDecimal getTaxAmount() {
        return this.taxAmount;
    }

    public Invoice taxAmount(BigDecimal taxAmount) {
        this.setTaxAmount(taxAmount);
        return this;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Invoice totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTaxType() {
        return this.taxType;
    }

    public Invoice taxType(String taxType) {
        this.setTaxType(taxType);
        return this;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getNormalizedJson() {
        return this.normalizedJson;
    }

    public Invoice normalizedJson(String normalizedJson) {
        this.setNormalizedJson(normalizedJson);
        return this;
    }

    public void setNormalizedJson(String normalizedJson) {
        this.normalizedJson = normalizedJson;
    }

    public InvoiceStatus getInvoiceStatus() {
        return this.invoiceStatus;
    }

    public Invoice invoiceStatus(InvoiceStatus invoiceStatus) {
        this.setInvoiceStatus(invoiceStatus);
        return this;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    public Invoice issuedAt(Instant issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getLegacyType() {
        return this.legacyType;
    }

    public Invoice legacyType(String legacyType) {
        this.setLegacyType(legacyType);
        return this;
    }

    public void setLegacyType(String legacyType) {
        this.legacyType = legacyType;
    }

    public String getTbCode() {
        return this.tbCode;
    }

    public Invoice tbCode(String tbCode) {
        this.setTbCode(tbCode);
        return this;
    }

    public void setTbCode(String tbCode) {
        this.tbCode = tbCode;
    }

    public String getTbCategory() {
        return this.tbCategory;
    }

    public Invoice tbCategory(String tbCategory) {
        this.setTbCategory(tbCategory);
        return this;
    }

    public void setTbCategory(String tbCategory) {
        this.tbCategory = tbCategory;
    }

    public Boolean getTbCanAutoRetry() {
        return this.tbCanAutoRetry;
    }

    public Invoice tbCanAutoRetry(Boolean tbCanAutoRetry) {
        this.setTbCanAutoRetry(tbCanAutoRetry);
        return this;
    }

    public void setTbCanAutoRetry(Boolean tbCanAutoRetry) {
        this.tbCanAutoRetry = tbCanAutoRetry;
    }

    public String getTbRecommendedAction() {
        return this.tbRecommendedAction;
    }

    public Invoice tbRecommendedAction(String tbRecommendedAction) {
        this.setTbRecommendedAction(tbRecommendedAction);
        return this;
    }

    public void setTbRecommendedAction(String tbRecommendedAction) {
        this.tbRecommendedAction = tbRecommendedAction;
    }

    public String getTbSourceCode() {
        return this.tbSourceCode;
    }

    public Invoice tbSourceCode(String tbSourceCode) {
        this.setTbSourceCode(tbSourceCode);
        return this;
    }

    public void setTbSourceCode(String tbSourceCode) {
        this.tbSourceCode = tbSourceCode;
    }

    public String getTbSourceMessage() {
        return this.tbSourceMessage;
    }

    public Invoice tbSourceMessage(String tbSourceMessage) {
        this.setTbSourceMessage(tbSourceMessage);
        return this;
    }

    public void setTbSourceMessage(String tbSourceMessage) {
        this.tbSourceMessage = tbSourceMessage;
    }

    public String getTbResultCode() {
        return this.tbResultCode;
    }

    public Invoice tbResultCode(String tbResultCode) {
        this.setTbResultCode(tbResultCode);
        return this;
    }

    public void setTbResultCode(String tbResultCode) {
        this.tbResultCode = tbResultCode;
    }

    public ImportFile getImportFile() {
        return this.importFile;
    }

    public void setImportFile(ImportFile importFile) {
        this.importFile = importFile;
    }

    public Invoice importFile(ImportFile importFile) {
        this.setImportFile(importFile);
        return this;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Invoice tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return getId() != null && getId().equals(((Invoice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", invoiceNo='" + getInvoiceNo() + "'" +
            ", messageFamily='" + getMessageFamily() + "'" +
            ", buyerId='" + getBuyerId() + "'" +
            ", buyerName='" + getBuyerName() + "'" +
            ", sellerId='" + getSellerId() + "'" +
            ", sellerName='" + getSellerName() + "'" +
            ", salesAmount=" + getSalesAmount() +
            ", taxAmount=" + getTaxAmount() +
            ", totalAmount=" + getTotalAmount() +
            ", taxType='" + getTaxType() + "'" +
            ", normalizedJson='" + getNormalizedJson() + "'" +
            ", invoiceStatus='" + getInvoiceStatus() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", legacyType='" + getLegacyType() + "'" +
            ", tbCode='" + getTbCode() + "'" +
            ", tbCategory='" + getTbCategory() + "'" +
            ", tbCanAutoRetry='" + getTbCanAutoRetry() + "'" +
            ", tbRecommendedAction='" + getTbRecommendedAction() + "'" +
            ", tbSourceCode='" + getTbSourceCode() + "'" +
            ", tbSourceMessage='" + getTbSourceMessage() + "'" +
            ", tbResultCode='" + getTbResultCode() + "'" +
            "}";
    }
}
