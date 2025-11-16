package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.TurnkeyMessage;
import com.asynctide.turnbridge.service.dto.InvoiceDTO;
import com.asynctide.turnbridge.service.dto.TurnkeyMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TurnkeyMessage} and its DTO {@link TurnkeyMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface TurnkeyMessageMapper extends EntityMapper<TurnkeyMessageDTO, TurnkeyMessage> {
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    TurnkeyMessageDTO toDto(TurnkeyMessage s);

    @Named("invoiceInvoiceNo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "invoiceNo", source = "invoiceNo")
    InvoiceDTO toDtoInvoiceInvoiceNo(Invoice invoice);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TurnkeyMessageDTO toSelect2DTO(TurnkeyMessage s);

    @Named("datatableDTO")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    TurnkeyMessageDTO toDatatableDTO(TurnkeyMessage s);
}
