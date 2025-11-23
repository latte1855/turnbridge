package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.Invoice} entity.
 */
@Schema(description = "發票/折讓資料主檔（Normalized）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    @Schema(description = "發票號碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String invoiceNo;

    @NotNull
    @Schema(description = "訊息家族", requiredMode = Schema.RequiredMode.REQUIRED)
    private MessageFamily messageFamily;

    @Size(max = 64)
    @Schema(description = "買方識別碼（統編或載具）")
    private String buyerId;

    @Size(max = 128)
    @Schema(description = "買方名稱")
    private String buyerName;

    @Size(max = 64)
    @Schema(description = "賣方統編")
    private String sellerId;

    @Size(max = 128)
    @Schema(description = "賣方名稱")
    private String sellerName;

    @Schema(description = "銷售金額")
    private BigDecimal salesAmount;

    @Schema(description = "稅額")
    private BigDecimal taxAmount;

    @Schema(description = "含稅金額")
    private BigDecimal totalAmount;

    @Size(max = 16)
    @Schema(description = "稅別")
    private String taxType;

    @Schema(description = "正規化 JSON")
    @Lob
    private String normalizedJson;

    @NotNull
    @Schema(description = "發票狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private InvoiceStatus invoiceStatus;

    @Schema(description = "開立時間")
    private Instant issuedAt;

    @Size(max = 16)
    @Schema(description = "原訊息別")
    private String legacyType;

    @Size(max = 32)
    @Schema(description = "Turnkey TB-xxxx 錯誤碼")
    private String tbCode;

    @Size(max = 128)
    @Schema(description = "TB 錯誤分類（例：PLATFORM.DATA_AMOUNT_MISMATCH）")
    private String tbCategory;

    @Schema(description = "是否允許系統自動重送")
    private Boolean tbCanAutoRetry;

    @Size(max = 64)
    @Schema(description = "建議營運處置（FIX_DATA / FIX_LIFECYCLE_FLOW / CHECK_PLATFORM）")
    private String tbRecommendedAction;

    @Size(max = 64)
    @Schema(description = "平台原始錯誤碼（ProcessResult ErrorCode）")
    private String tbSourceCode;

    @Size(max = 1024)
    @Schema(description = "平台錯誤訊息")
    private String tbSourceMessage;

    @Size(max = 16)
    @Schema(description = "ProcessResult ResultCode（0=成功）")
    private String tbResultCode;

    @NotNull
    @Schema(description = "匯入檔主檔")
    private ImportFileDTO importFile;

    @Schema(description = "租戶")
    private TenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public MessageFamily getMessageFamily() {
        return messageFamily;
    }

    public void setMessageFamily(MessageFamily messageFamily) {
        this.messageFamily = messageFamily;
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

    public BigDecimal getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getNormalizedJson() {
        return normalizedJson;
    }

    public void setNormalizedJson(String normalizedJson) {
        this.normalizedJson = normalizedJson;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getLegacyType() {
        return legacyType;
    }

    public void setLegacyType(String legacyType) {
        this.legacyType = legacyType;
    }

    public String getTbCode() {
        return tbCode;
    }

    public void setTbCode(String tbCode) {
        this.tbCode = tbCode;
    }

    public String getTbCategory() {
        return tbCategory;
    }

    public void setTbCategory(String tbCategory) {
        this.tbCategory = tbCategory;
    }

    public Boolean getTbCanAutoRetry() {
        return tbCanAutoRetry;
    }

    public void setTbCanAutoRetry(Boolean tbCanAutoRetry) {
        this.tbCanAutoRetry = tbCanAutoRetry;
    }

    public String getTbRecommendedAction() {
        return tbRecommendedAction;
    }

    public void setTbRecommendedAction(String tbRecommendedAction) {
        this.tbRecommendedAction = tbRecommendedAction;
    }

    public String getTbSourceCode() {
        return tbSourceCode;
    }

    public void setTbSourceCode(String tbSourceCode) {
        this.tbSourceCode = tbSourceCode;
    }

    public String getTbSourceMessage() {
        return tbSourceMessage;
    }

    public void setTbSourceMessage(String tbSourceMessage) {
        this.tbSourceMessage = tbSourceMessage;
    }

    public String getTbResultCode() {
        return tbResultCode;
    }

    public void setTbResultCode(String tbResultCode) {
        this.tbResultCode = tbResultCode;
    }

    public ImportFileDTO getImportFile() {
        return importFile;
    }

    public void setImportFile(ImportFileDTO importFile) {
        this.importFile = importFile;
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
        if (!(o instanceof InvoiceDTO)) {
            return false;
        }

        InvoiceDTO invoiceDTO = (InvoiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceDTO{" +
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
            ", importFile=" + getImportFile() +
            ", tenant=" + getTenant() +
            "}";
    }
}
