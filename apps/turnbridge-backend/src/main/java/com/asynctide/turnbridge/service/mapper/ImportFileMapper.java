package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImportFile} and its DTO {@link ImportFileDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImportFileMapper extends EntityMapper<ImportFileDTO, ImportFile> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    ImportFileDTO toDto(ImportFile s);

    @Named("tenantName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TenantDTO toDtoTenantName(Tenant tenant);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ImportFileDTO toSelect2DTO(ImportFile s);

    @Named("datatableDTO")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    ImportFileDTO toDatatableDTO(ImportFile s);
}
