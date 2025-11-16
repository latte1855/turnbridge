package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.dto.InvoiceDTO;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    InvoiceDTO toDto(Invoice s);

    @Named("importFileOriginalFilename")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "originalFilename", source = "originalFilename")
    ImportFileDTO toDtoImportFileOriginalFilename(ImportFile importFile);

    @Named("tenantName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TenantDTO toDtoTenantName(Tenant tenant);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceDTO toSelect2DTO(Invoice s);

    @Named("datatableDTO")
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    InvoiceDTO toDatatableDTO(Invoice s);
}
