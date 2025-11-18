package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.dto.ImportFileItemDTO;
import com.asynctide.turnbridge.service.dto.InvoiceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImportFileItem} and its DTO {@link ImportFileItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImportFileItemMapper extends EntityMapper<ImportFileItemDTO, ImportFileItem> {
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    ImportFileItemDTO toDto(ImportFileItem s);

    @Named("importFileOriginalFilename")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "originalFilename", source = "originalFilename")
    ImportFileDTO toDtoImportFileOriginalFilename(ImportFile importFile);

    @Named("invoiceInvoiceNo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "invoiceNo", source = "invoiceNo")
    InvoiceDTO toDtoInvoiceInvoiceNo(Invoice invoice);

    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ImportFileItemDTO toSelect2DTO(ImportFileItem s);

    @Named("datatableDTO")
    @Mapping(target = "importFile", source = "importFile", qualifiedByName = "importFileOriginalFilename")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceInvoiceNo")
    ImportFileItemDTO toDatatableDTO(ImportFileItem s);
}
