package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UploadJob} and its DTO {@link UploadJobDTO}.
 */
@Mapper(componentModel = "spring")
public interface UploadJobMapper extends EntityMapper<UploadJobDTO, UploadJob> {
    @Mapping(target = "originalFile", source = "originalFile", qualifiedByName = "storedObjectId")
    @Mapping(target = "resultFile", source = "resultFile", qualifiedByName = "storedObjectId")
    UploadJobDTO toDto(UploadJob s);

    @Named("storedObjectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StoredObjectDTO toDtoStoredObjectId(StoredObject storedObject);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UploadJobDTO toSelect2DTO(UploadJob s);

    @Named("datatableDTO")
    @Mapping(target = "originalFile", source = "originalFile", qualifiedByName = "storedObjectId")
    @Mapping(target = "resultFile", source = "resultFile", qualifiedByName = "storedObjectId")
    UploadJobDTO toDatatableDTO(UploadJob s);
}
