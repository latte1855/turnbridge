package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.service.dto.InvoiceDTO;
import com.asynctide.turnbridge.service.dto.InvoiceItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InvoiceItem} and its DTO {@link InvoiceItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceItemMapper extends EntityMapper<InvoiceItemDTO, InvoiceItem> {
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    InvoiceItemDTO toDto(InvoiceItem s);

    @Named("invoiceInvoiceNo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "invoiceNo", source = "invoiceNo")
    InvoiceDTO toDtoInvoiceInvoiceNo(Invoice invoice);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceItemDTO toSelect2DTO(InvoiceItem s);

    @Named("datatableDTO")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    InvoiceItemDTO toDatatableDTO(InvoiceItem s);
}
