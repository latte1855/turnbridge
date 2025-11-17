package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.service.dto.ImportFileItemErrorDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileItemErrorMapper;
import com.querydsl.core.types.Predicate;
import java.util.List;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.ImportFileItemError}.
 */
@Service
@Transactional
public class ImportFileItemErrorService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileItemErrorService.class);

    private final ImportFileItemErrorRepository importFileItemErrorRepository;

    private final ImportFileItemErrorMapper importFileItemErrorMapper;

    public ImportFileItemErrorService(
        ImportFileItemErrorRepository importFileItemErrorRepository,
        ImportFileItemErrorMapper importFileItemErrorMapper
    ) {
        this.importFileItemErrorRepository = importFileItemErrorRepository;
        this.importFileItemErrorMapper = importFileItemErrorMapper;
    }

    /**
     * Save a importFileItemError.
     *
     * @param importFileItemErrorDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileItemErrorDTO save(ImportFileItemErrorDTO importFileItemErrorDTO) {
        LOG.debug("Request to save ImportFileItemError : {}", importFileItemErrorDTO);
        ImportFileItemError importFileItemError = importFileItemErrorMapper.toEntity(importFileItemErrorDTO);
        importFileItemError = importFileItemErrorRepository.save(importFileItemError);
        return importFileItemErrorMapper.toDto(importFileItemError);
    }

    /**
     * Update a importFileItemError.
     *
     * @param importFileItemErrorDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileItemErrorDTO update(ImportFileItemErrorDTO importFileItemErrorDTO) {
        LOG.debug("Request to update ImportFileItemError : {}", importFileItemErrorDTO);
        ImportFileItemError importFileItemError = importFileItemErrorMapper.toEntity(importFileItemErrorDTO);
        importFileItemError = importFileItemErrorRepository.save(importFileItemError);
        return importFileItemErrorMapper.toDto(importFileItemError);
    }

    /**
     * Partially update a importFileItemError.
     *
     * @param importFileItemErrorDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ImportFileItemErrorDTO> partialUpdate(ImportFileItemErrorDTO importFileItemErrorDTO) {
        LOG.debug("Request to partially update ImportFileItemError : {}", importFileItemErrorDTO);

        return importFileItemErrorRepository
            .findById(importFileItemErrorDTO.getId())
            .map(existingImportFileItemError -> {
                importFileItemErrorMapper.partialUpdate(existingImportFileItemError, importFileItemErrorDTO);

                return existingImportFileItemError;
            })
            .map(importFileItemErrorRepository::save)
            .map(importFileItemErrorMapper::toDto);
    }

    /**
     * Get all the importFileItemErrors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ImportFileItemErrorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return importFileItemErrorRepository.findAllWithEagerRelationships(pageable).map(importFileItemErrorMapper::toDto);
    }

    /**
     * Get one importFileItemError by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ImportFileItemErrorDTO> findOne(Long id) {
        LOG.debug("Request to get ImportFileItemError : {}", id);
        return importFileItemErrorRepository.findOneWithEagerRelationships(id).map(importFileItemErrorMapper::toDto);
    }

    /**
     * Delete the importFileItemError by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ImportFileItemError : {}", id);
        importFileItemErrorRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 importFileItemError 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemErrorDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get ImportFileItemError with conditions: {}", predicate);
        return this.findAll(predicate, importFileItemErrorMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileItemError 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemErrorDTO> findAll(
        Predicate predicate,
        Function<? super ImportFileItemError, ? extends ImportFileItemErrorDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileItemError with conditions: {}", predicate);
        Stream<ImportFileItemError> stream = Objects.isNull(predicate)
            ? importFileItemErrorRepository.findAll().stream()
            : StreamSupport.stream(importFileItemErrorRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 importFileItemError 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileItemErrorDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get ImportFileItemError with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, importFileItemErrorMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileItemError 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileItemErrorDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super ImportFileItemError, ? extends ImportFileItemErrorDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileItemError with conditions: {}, page: {}", predicate, pageable);
        Page<ImportFileItemError> page = Objects.isNull(predicate)
            ? importFileItemErrorRepository.findAll(pageable)
            : importFileItemErrorRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 importFileItemError 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemErrorDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get ImportFileItemError with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, importFileItemErrorMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileItemError 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemErrorDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super ImportFileItemError, ? extends ImportFileItemErrorDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileItemError with conditions: {}, sort: {}", predicate, sort);
        Stream<ImportFileItemError> stream = Objects.isNull(predicate)
            ? importFileItemErrorRepository.findAll(sort).stream()
            : StreamSupport.stream(importFileItemErrorRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
