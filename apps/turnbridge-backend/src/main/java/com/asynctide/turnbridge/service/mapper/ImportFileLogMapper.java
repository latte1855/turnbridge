package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImportFileLog} and its DTO {@link ImportFileLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImportFileLogMapper extends EntityMapper<ImportFileLogDTO, ImportFileLog> {
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    ImportFileLogDTO toDto(ImportFileLog s);

    @Named("importFileOriginalFilename")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "originalFilename", source = "originalFilename")
    ImportFileDTO toDtoImportFileOriginalFilename(ImportFile importFile);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ImportFileLogDTO toSelect2DTO(ImportFileLog s);

    @Named("datatableDTO")
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    ImportFileLogDTO toDatatableDTO(ImportFileLog s);
}
