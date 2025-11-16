package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.ManualAction;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.dto.InvoiceDTO;
import com.asynctide.turnbridge.service.dto.ManualActionDTO;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ManualAction} and its DTO {@link ManualActionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ManualActionMapper extends EntityMapper<ManualActionDTO, ManualAction> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    ManualActionDTO toDto(ManualAction s);

    @Named("tenantName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TenantDTO toDtoTenantName(Tenant tenant);

    @Named("invoiceInvoiceNo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "invoiceNo", source = "invoiceNo")
    InvoiceDTO toDtoInvoiceInvoiceNo(Invoice invoice);

    @Named("importFileOriginalFilename")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "originalFilename", source = "originalFilename")
    ImportFileDTO toDtoImportFileOriginalFilename(ImportFile importFile);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ManualActionDTO toSelect2DTO(ManualAction s);

    @Named("datatableDTO")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    ManualActionDTO toDatatableDTO(ManualAction s);
}
