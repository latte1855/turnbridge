package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import com.asynctide.turnbridge.service.dto.WebhookEndpointDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WebhookEndpoint} and its DTO {@link WebhookEndpointDTO}.
 */
@Mapper(componentModel = "spring")
public interface WebhookEndpointMapper extends EntityMapper<WebhookEndpointDTO, WebhookEndpoint> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    WebhookEndpointDTO toDto(WebhookEndpoint s);

    @Named("tenantName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TenantDTO toDtoTenantName(Tenant tenant);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WebhookEndpointDTO toSelect2DTO(WebhookEndpoint s);

    @Named("datatableDTO")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantName")
    WebhookEndpointDTO toDatatableDTO(WebhookEndpoint s);
}
