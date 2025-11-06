package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StoredObject} and its DTO {@link StoredObjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface StoredObjectMapper extends EntityMapper<StoredObjectDTO, StoredObject> {
    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StoredObjectDTO toSelect2DTO(StoredObject s);

    @Named("datatableDTO")
    StoredObjectDTO toDatatableDTO(StoredObject s);
}
