package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileMapper;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.ImportFile}.
 */
@Service
@Transactional
public class ImportFileService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileService.class);

    private final ImportFileRepository importFileRepository;

    private final ImportFileMapper importFileMapper;

    private final InvoiceRepository invoiceRepository;

    public ImportFileService(
        ImportFileRepository importFileRepository,
        ImportFileMapper importFileMapper,
        InvoiceRepository invoiceRepository
    ) {
        this.importFileRepository = importFileRepository;
        this.importFileMapper = importFileMapper;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Save a importFile.
     *
     * @param importFileDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileDTO save(ImportFileDTO importFileDTO) {
        LOG.debug("Request to save ImportFile : {}", importFileDTO);
        ImportFile importFile = importFileMapper.toEntity(importFileDTO);
        importFile = importFileRepository.save(importFile);
        return importFileMapper.toDto(importFile);
    }

    /**
     * Update a importFile.
     *
     * @param importFileDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileDTO update(ImportFileDTO importFileDTO) {
        LOG.debug("Request to update ImportFile : {}", importFileDTO);
        ImportFile importFile = importFileMapper.toEntity(importFileDTO);
        importFile = importFileRepository.save(importFile);
        return importFileMapper.toDto(importFile);
    }

    /**
     * Partially update a importFile.
     *
     * @param importFileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ImportFileDTO> partialUpdate(ImportFileDTO importFileDTO) {
        LOG.debug("Request to partially update ImportFile : {}", importFileDTO);

        return importFileRepository
            .findById(importFileDTO.getId())
            .map(existingImportFile -> {
                importFileMapper.partialUpdate(existingImportFile, importFileDTO);

                return existingImportFile;
            })
            .map(importFileRepository::save)
            .map(importFileMapper::toDto);
    }

    /**
     * Get all the importFiles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ImportFileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return importFileRepository.findAllWithEagerRelationships(pageable).map(importFileMapper::toDto);
    }

    /**
     * Get one importFile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ImportFileDTO> findOne(Long id) {
        LOG.debug("Request to get ImportFile : {}", id);
        return importFileRepository.findOneWithEagerRelationships(id).map(importFileMapper::toDto);
    }

    /**
     * Delete the importFile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ImportFile : {}", id);
        importFileRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 importFile 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get ImportFile with conditions: {}", predicate);
        return this.findAll(predicate, importFileMapper::toDto);
    }

    /**
     * 取得過濾後的 importFile 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileDTO> findAll(Predicate predicate, Function<? super ImportFile, ? extends ImportFileDTO> mapper) {
        LOG.debug("Request to get ImportFile with conditions: {}", predicate);
        Stream<ImportFile> stream = Objects.isNull(predicate)
            ? importFileRepository.findAll().stream()
            : StreamSupport.stream(importFileRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 importFile 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get ImportFile with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, importFileMapper::toDto);
    }

    /**
     * 取得過濾後的 importFile 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super ImportFile, ? extends ImportFileDTO> mapper
    ) {
        LOG.debug("Request to get ImportFile with conditions: {}, page: {}", predicate, pageable);
        Page<ImportFile> page = Objects.isNull(predicate)
            ? importFileRepository.findAll(pageable)
            : importFileRepository.findAll(predicate, pageable);
        return mapPageWithTbSummary(page, mapper);
    }

    /**
     * 取得過濾後的 importFile 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get ImportFile with conditions: {}, sort: {}", predicate, sort);
        Iterable<ImportFile> iterable = Objects.isNull(predicate)
            ? importFileRepository.findAll(sort)
            : importFileRepository.findAll(predicate, sort);
        List<ImportFile> result = StreamSupport.stream(iterable.spliterator(), false).toList();
        return mapListWithTbSummary(result, importFileMapper::toDto);
    }

    /**
     * 取得過濾後的 importFile 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileDTO> findAll(Predicate predicate, Sort sort, Function<? super ImportFile, ? extends ImportFileDTO> mapper) {
        LOG.debug("Request to get ImportFile with conditions: {}, sort: {}", predicate, sort);
        
        Iterable<ImportFile> iterable = Objects.isNull(predicate)
                ? importFileRepository.findAll(sort)
                : importFileRepository.findAll(predicate, sort);
        List<ImportFile> result = StreamSupport.stream(iterable.spliterator(), false).toList();
            
        return mapListWithTbSummary(result, mapper);
    }

    private Page<ImportFileDTO> mapPageWithTbSummary(
        Page<ImportFile> page,
        Function<? super ImportFile, ? extends ImportFileDTO> mapper
    ) {
        List<Long> ids = page.stream().map(ImportFile::getId).filter(Objects::nonNull).toList();
        Map<Long, String> summaries = buildTbSummaryMap(ids);
        return page.map(importFile -> {
            ImportFileDTO dto = mapper.apply(importFile);
            if (dto != null) {
                String summary = summaries.get(importFile.getId());
                dto.setTbErrorSummary(summary);
                dto.setHasTbError(summary != null);
            }
            return dto;
        });
    }

    private List<ImportFileDTO> mapListWithTbSummary(
        List<ImportFile> list,
        Function<? super ImportFile, ? extends ImportFileDTO> mapper
    ) {
        List<Long> ids = list.stream().map(ImportFile::getId).filter(Objects::nonNull).toList();
        Map<Long, String> summaries = buildTbSummaryMap(ids);
        return list.stream()
            .map(importFile -> {
                ImportFileDTO dto = mapper.apply(importFile);
                if (dto != null) {
                    String summary = summaries.get(importFile.getId());
                    dto.setTbErrorSummary(summary);
                    dto.setHasTbError(summary != null);
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    private Map<Long, String> buildTbSummaryMap(List<Long> importFileIds) {
        if (importFileIds == null || importFileIds.isEmpty()) {
            return Map.of();
        }
        return invoiceRepository.findTbSummaryByImportFileIds(importFileIds).stream()
            .map(row -> new TbSummaryRow(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).longValue()
            ))
            .collect(
                Collectors.groupingBy(
                    TbSummaryRow::importFileId,
                    Collectors.mapping(
                        row -> String.format("%s (%s)×%d", row.tbCode(), row.tbCategory() == null ? "?" : row.tbCategory(), row.count()),
                        Collectors.joining("; ")
                    )
                )
            );
    }

    private record TbSummaryRow(Long importFileId, String tbCode, String tbCategory, Long count) {}
}
