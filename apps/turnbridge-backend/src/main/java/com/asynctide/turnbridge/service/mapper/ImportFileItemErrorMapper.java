package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.service.dto.ImportFileItemDTO;
import com.asynctide.turnbridge.service.dto.ImportFileItemErrorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImportFileItemError} and its DTO {@link ImportFileItemErrorDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImportFileItemErrorMapper extends EntityMapper<ImportFileItemErrorDTO, ImportFileItemError> {
    @Mapping(target = "importFileItem", source = "importFileItem", qualifiedByName = "importFileItemLineIndex")
    ImportFileItemErrorDTO toDto(ImportFileItemError s);

    @Named("importFileItemLineIndex")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "lineIndex", source = "lineIndex")
    ImportFileItemDTO toDtoImportFileItemLineIndex(ImportFileItem importFileItem);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ImportFileItemErrorDTO toSelect2DTO(ImportFileItemError s);

    @Named("datatableDTO")
    @Mapping(target = "importFileItem", source = "importFileItem", qualifiedByName = "importFileItemLineIndex")
    ImportFileItemErrorDTO toDatatableDTO(ImportFileItemError s);
}
