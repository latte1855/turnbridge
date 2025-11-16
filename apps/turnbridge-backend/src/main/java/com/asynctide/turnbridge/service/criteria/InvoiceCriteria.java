package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.Invoice} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.InvoiceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /invoices?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MessageFamily
     */
    public static class MessageFamilyFilter extends Filter<MessageFamily> {

        public MessageFamilyFilter() {}

        public MessageFamilyFilter(MessageFamilyFilter filter) {
            super(filter);
        }

        @Override
        public MessageFamilyFilter copy() {
            return new MessageFamilyFilter(this);
        }
    }

    /**
     * Class for filtering InvoiceStatus
     */
    public static class InvoiceStatusFilter extends Filter<InvoiceStatus> {

        public InvoiceStatusFilter() {}

        public InvoiceStatusFilter(InvoiceStatusFilter filter) {
            super(filter);
        }

        @Override
        public InvoiceStatusFilter copy() {
            return new InvoiceStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter invoiceNo;

    private MessageFamilyFilter messageFamily;

    private StringFilter buyerId;

    private StringFilter buyerName;

    private StringFilter sellerId;

    private StringFilter sellerName;

    private BigDecimalFilter salesAmount;

    private BigDecimalFilter taxAmount;

    private BigDecimalFilter totalAmount;

    private StringFilter taxType;

    private InvoiceStatusFilter invoiceStatus;

    private InstantFilter issuedAt;

    private StringFilter legacyType;

    private LongFilter importFileId;

    private LongFilter tenantId;

    private Boolean distinct;

    public InvoiceCriteria() {}

    public InvoiceCriteria(InvoiceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.invoiceNo = other.optionalInvoiceNo().map(StringFilter::copy).orElse(null);
        this.messageFamily = other.optionalMessageFamily().map(MessageFamilyFilter::copy).orElse(null);
        this.buyerId = other.optionalBuyerId().map(StringFilter::copy).orElse(null);
        this.buyerName = other.optionalBuyerName().map(StringFilter::copy).orElse(null);
        this.sellerId = other.optionalSellerId().map(StringFilter::copy).orElse(null);
        this.sellerName = other.optionalSellerName().map(StringFilter::copy).orElse(null);
        this.salesAmount = other.optionalSalesAmount().map(BigDecimalFilter::copy).orElse(null);
        this.taxAmount = other.optionalTaxAmount().map(BigDecimalFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.taxType = other.optionalTaxType().map(StringFilter::copy).orElse(null);
        this.invoiceStatus = other.optionalInvoiceStatus().map(InvoiceStatusFilter::copy).orElse(null);
        this.issuedAt = other.optionalIssuedAt().map(InstantFilter::copy).orElse(null);
        this.legacyType = other.optionalLegacyType().map(StringFilter::copy).orElse(null);
        this.importFileId = other.optionalImportFileId().map(LongFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InvoiceCriteria copy() {
        return new InvoiceCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getInvoiceNo() {
        return invoiceNo;
    }

    public Optional<StringFilter> optionalInvoiceNo() {
        return Optional.ofNullable(invoiceNo);
    }

    public StringFilter invoiceNo() {
        if (invoiceNo == null) {
            setInvoiceNo(new StringFilter());
        }
        return invoiceNo;
    }

    public void setInvoiceNo(StringFilter invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public MessageFamilyFilter getMessageFamily() {
        return messageFamily;
    }

    public Optional<MessageFamilyFilter> optionalMessageFamily() {
        return Optional.ofNullable(messageFamily);
    }

    public MessageFamilyFilter messageFamily() {
        if (messageFamily == null) {
            setMessageFamily(new MessageFamilyFilter());
        }
        return messageFamily;
    }

    public void setMessageFamily(MessageFamilyFilter messageFamily) {
        this.messageFamily = messageFamily;
    }

    public StringFilter getBuyerId() {
        return buyerId;
    }

    public Optional<StringFilter> optionalBuyerId() {
        return Optional.ofNullable(buyerId);
    }

    public StringFilter buyerId() {
        if (buyerId == null) {
            setBuyerId(new StringFilter());
        }
        return buyerId;
    }

    public void setBuyerId(StringFilter buyerId) {
        this.buyerId = buyerId;
    }

    public StringFilter getBuyerName() {
        return buyerName;
    }

    public Optional<StringFilter> optionalBuyerName() {
        return Optional.ofNullable(buyerName);
    }

    public StringFilter buyerName() {
        if (buyerName == null) {
            setBuyerName(new StringFilter());
        }
        return buyerName;
    }

    public void setBuyerName(StringFilter buyerName) {
        this.buyerName = buyerName;
    }

    public StringFilter getSellerId() {
        return sellerId;
    }

    public Optional<StringFilter> optionalSellerId() {
        return Optional.ofNullable(sellerId);
    }

    public StringFilter sellerId() {
        if (sellerId == null) {
            setSellerId(new StringFilter());
        }
        return sellerId;
    }

    public void setSellerId(StringFilter sellerId) {
        this.sellerId = sellerId;
    }

    public StringFilter getSellerName() {
        return sellerName;
    }

    public Optional<StringFilter> optionalSellerName() {
        return Optional.ofNullable(sellerName);
    }

    public StringFilter sellerName() {
        if (sellerName == null) {
            setSellerName(new StringFilter());
        }
        return sellerName;
    }

    public void setSellerName(StringFilter sellerName) {
        this.sellerName = sellerName;
    }

    public BigDecimalFilter getSalesAmount() {
        return salesAmount;
    }

    public Optional<BigDecimalFilter> optionalSalesAmount() {
        return Optional.ofNullable(salesAmount);
    }

    public BigDecimalFilter salesAmount() {
        if (salesAmount == null) {
            setSalesAmount(new BigDecimalFilter());
        }
        return salesAmount;
    }

    public void setSalesAmount(BigDecimalFilter salesAmount) {
        this.salesAmount = salesAmount;
    }

    public BigDecimalFilter getTaxAmount() {
        return taxAmount;
    }

    public Optional<BigDecimalFilter> optionalTaxAmount() {
        return Optional.ofNullable(taxAmount);
    }

    public BigDecimalFilter taxAmount() {
        if (taxAmount == null) {
            setTaxAmount(new BigDecimalFilter());
        }
        return taxAmount;
    }

    public void setTaxAmount(BigDecimalFilter taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public StringFilter getTaxType() {
        return taxType;
    }

    public Optional<StringFilter> optionalTaxType() {
        return Optional.ofNullable(taxType);
    }

    public StringFilter taxType() {
        if (taxType == null) {
            setTaxType(new StringFilter());
        }
        return taxType;
    }

    public void setTaxType(StringFilter taxType) {
        this.taxType = taxType;
    }

    public InvoiceStatusFilter getInvoiceStatus() {
        return invoiceStatus;
    }

    public Optional<InvoiceStatusFilter> optionalInvoiceStatus() {
        return Optional.ofNullable(invoiceStatus);
    }

    public InvoiceStatusFilter invoiceStatus() {
        if (invoiceStatus == null) {
            setInvoiceStatus(new InvoiceStatusFilter());
        }
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatusFilter invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public InstantFilter getIssuedAt() {
        return issuedAt;
    }

    public Optional<InstantFilter> optionalIssuedAt() {
        return Optional.ofNullable(issuedAt);
    }

    public InstantFilter issuedAt() {
        if (issuedAt == null) {
            setIssuedAt(new InstantFilter());
        }
        return issuedAt;
    }

    public void setIssuedAt(InstantFilter issuedAt) {
        this.issuedAt = issuedAt;
    }

    public StringFilter getLegacyType() {
        return legacyType;
    }

    public Optional<StringFilter> optionalLegacyType() {
        return Optional.ofNullable(legacyType);
    }

    public StringFilter legacyType() {
        if (legacyType == null) {
            setLegacyType(new StringFilter());
        }
        return legacyType;
    }

    public void setLegacyType(StringFilter legacyType) {
        this.legacyType = legacyType;
    }

    public LongFilter getImportFileId() {
        return importFileId;
    }

    public Optional<LongFilter> optionalImportFileId() {
        return Optional.ofNullable(importFileId);
    }

    public LongFilter importFileId() {
        if (importFileId == null) {
            setImportFileId(new LongFilter());
        }
        return importFileId;
    }

    public void setImportFileId(LongFilter importFileId) {
        this.importFileId = importFileId;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InvoiceCriteria that = (InvoiceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(invoiceNo, that.invoiceNo) &&
            Objects.equals(messageFamily, that.messageFamily) &&
            Objects.equals(buyerId, that.buyerId) &&
            Objects.equals(buyerName, that.buyerName) &&
            Objects.equals(sellerId, that.sellerId) &&
            Objects.equals(sellerName, that.sellerName) &&
            Objects.equals(salesAmount, that.salesAmount) &&
            Objects.equals(taxAmount, that.taxAmount) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(taxType, that.taxType) &&
            Objects.equals(invoiceStatus, that.invoiceStatus) &&
            Objects.equals(issuedAt, that.issuedAt) &&
            Objects.equals(legacyType, that.legacyType) &&
            Objects.equals(importFileId, that.importFileId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            invoiceNo,
            messageFamily,
            buyerId,
            buyerName,
            sellerId,
            sellerName,
            salesAmount,
            taxAmount,
            totalAmount,
            taxType,
            invoiceStatus,
            issuedAt,
            legacyType,
            importFileId,
            tenantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalInvoiceNo().map(f -> "invoiceNo=" + f + ", ").orElse("") +
            optionalMessageFamily().map(f -> "messageFamily=" + f + ", ").orElse("") +
            optionalBuyerId().map(f -> "buyerId=" + f + ", ").orElse("") +
            optionalBuyerName().map(f -> "buyerName=" + f + ", ").orElse("") +
            optionalSellerId().map(f -> "sellerId=" + f + ", ").orElse("") +
            optionalSellerName().map(f -> "sellerName=" + f + ", ").orElse("") +
            optionalSalesAmount().map(f -> "salesAmount=" + f + ", ").orElse("") +
            optionalTaxAmount().map(f -> "taxAmount=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalTaxType().map(f -> "taxType=" + f + ", ").orElse("") +
            optionalInvoiceStatus().map(f -> "invoiceStatus=" + f + ", ").orElse("") +
            optionalIssuedAt().map(f -> "issuedAt=" + f + ", ").orElse("") +
            optionalLegacyType().map(f -> "legacyType=" + f + ", ").orElse("") +
            optionalImportFileId().map(f -> "importFileId=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
