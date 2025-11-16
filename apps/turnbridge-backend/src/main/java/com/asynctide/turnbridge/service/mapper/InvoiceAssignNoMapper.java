package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.InvoiceAssignNo;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.service.dto.InvoiceAssignNoDTO;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InvoiceAssignNo} and its DTO {@link InvoiceAssignNoDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceAssignNoMapper extends EntityMapper<InvoiceAssignNoDTO, InvoiceAssignNo> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    InvoiceAssignNoDTO toDto(InvoiceAssignNo s);

    @Named("tenantName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TenantDTO toDtoTenantName(Tenant tenant);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceAssignNoDTO toSelect2DTO(InvoiceAssignNo s);

    @Named("datatableDTO")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    InvoiceAssignNoDTO toDatatableDTO(InvoiceAssignNo s);
}
