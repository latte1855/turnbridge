package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.service.dto.WebhookDeliveryLogDTO;
import com.asynctide.turnbridge.service.dto.WebhookEndpointDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WebhookDeliveryLog} and its DTO {@link WebhookDeliveryLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface WebhookDeliveryLogMapper extends EntityMapper<WebhookDeliveryLogDTO, WebhookDeliveryLog> {
    @Mapping(target = "webhookEndpoint", source = "webhookEndpoint", qualifiedByName = "webhookEndpointName")
    WebhookDeliveryLogDTO toDto(WebhookDeliveryLog s);

    @Named("webhookEndpointName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WebhookEndpointDTO toDtoWebhookEndpointName(WebhookEndpoint webhookEndpoint);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WebhookDeliveryLogDTO toSelect2DTO(WebhookDeliveryLog s);

    @Named("datatableDTO")
    @Mapping(target = "webhookEndpoint", source = "webhookEndpoint", qualifiedByName = "webhookEndpointName")
    WebhookDeliveryLogDTO toDatatableDTO(WebhookDeliveryLog s);
}
