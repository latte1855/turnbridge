package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.InvoiceItem} entity.
 */
@Schema(description = "發票/折讓明細")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 256)
    @Schema(description = "品名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "數量")
    private BigDecimal quantity;

    @Schema(description = "單價")
    private BigDecimal unitPrice;

    @Schema(description = "金額")
    private BigDecimal amount;

    @Schema(description = "序號")
    private Integer sequence;

    @NotNull
    @Schema(description = "發票主檔")
    private InvoiceDTO invoice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceItemDTO)) {
            return false;
        }

        InvoiceItemDTO invoiceItemDTO = (InvoiceItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceItemDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", amount=" + getAmount() +
            ", sequence=" + getSequence() +
            ", invoice=" + getInvoice() +
            "}";
    }
}
