package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tenant} and its DTO {@link TenantDTO}.
 */
@Mapper(componentModel = "spring")
public interface TenantMapper extends EntityMapper<TenantDTO, Tenant> {
    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TenantDTO toSelect2DTO(Tenant s);

    @Named("datatableDTO")
    TenantDTO toDatatableDTO(Tenant s);
}
