package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
import com.asynctide.turnbridge.service.dto.UploadJobItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UploadJobItem} and its DTO {@link UploadJobItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface UploadJobItemMapper extends EntityMapper<UploadJobItemDTO, UploadJobItem> {
    @Mapping(target = "job", source = "job", qualifiedByName = "uploadJobJobId")
    UploadJobItemDTO toDto(UploadJobItem s);

    @Named("uploadJobJobId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "jobId", source = "jobId")
    UploadJobDTO toDtoUploadJobJobId(UploadJob uploadJob);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UploadJobItemDTO toSelect2DTO(UploadJobItem s);

    @Named("datatableDTO")
    @Mapping(target = "job", source = "job", qualifiedByName = "uploadJobJobId")
    UploadJobItemDTO toDatatableDTO(UploadJobItem s);
}
