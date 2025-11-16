package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileLogMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.ImportFileLog}.
 */
@Service
@Transactional
public class ImportFileLogService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileLogService.class);

    private final ImportFileLogRepository importFileLogRepository;

    private final ImportFileLogMapper importFileLogMapper;

    public ImportFileLogService(ImportFileLogRepository importFileLogRepository, ImportFileLogMapper importFileLogMapper) {
        this.importFileLogRepository = importFileLogRepository;
        this.importFileLogMapper = importFileLogMapper;
    }

    /**
     * Save a importFileLog.
     *
     * @param importFileLogDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileLogDTO save(ImportFileLogDTO importFileLogDTO) {
        LOG.debug("Request to save ImportFileLog : {}", importFileLogDTO);
        ImportFileLog importFileLog = importFileLogMapper.toEntity(importFileLogDTO);
        importFileLog = importFileLogRepository.save(importFileLog);
        return importFileLogMapper.toDto(importFileLog);
    }

    /**
     * Update a importFileLog.
     *
     * @param importFileLogDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileLogDTO update(ImportFileLogDTO importFileLogDTO) {
        LOG.debug("Request to update ImportFileLog : {}", importFileLogDTO);
        ImportFileLog importFileLog = importFileLogMapper.toEntity(importFileLogDTO);
        importFileLog = importFileLogRepository.save(importFileLog);
        return importFileLogMapper.toDto(importFileLog);
    }

    /**
     * Partially update a importFileLog.
     *
     * @param importFileLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ImportFileLogDTO> partialUpdate(ImportFileLogDTO importFileLogDTO) {
        LOG.debug("Request to partially update ImportFileLog : {}", importFileLogDTO);

        return importFileLogRepository
            .findById(importFileLogDTO.getId())
            .map(existingImportFileLog -> {
                importFileLogMapper.partialUpdate(existingImportFileLog, importFileLogDTO);

                return existingImportFileLog;
            })
            .map(importFileLogRepository::save)
            .map(importFileLogMapper::toDto);
    }

    /**
     * Get all the importFileLogs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ImportFileLogDTO> findAllWithEagerRelationships(Pageable pageable) {
        return importFileLogRepository.findAllWithEagerRelationships(pageable).map(importFileLogMapper::toDto);
    }

    /**
     * Get one importFileLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ImportFileLogDTO> findOne(Long id) {
        LOG.debug("Request to get ImportFileLog : {}", id);
        return importFileLogRepository.findOneWithEagerRelationships(id).map(importFileLogMapper::toDto);
    }

    /**
     * Delete the importFileLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ImportFileLog : {}", id);
        importFileLogRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 importFileLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileLogDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get ImportFileLog with conditions: {}", predicate);
        return this.findAll(predicate, importFileLogMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileLogDTO> findAll(Predicate predicate, Function<? super ImportFileLog, ? extends ImportFileLogDTO> mapper) {
        LOG.debug("Request to get ImportFileLog with conditions: {}", predicate);
        Stream<ImportFileLog> stream = Objects.isNull(predicate)
            ? importFileLogRepository.findAll().stream()
            : StreamSupport.stream(importFileLogRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 importFileLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileLogDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get ImportFileLog with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, importFileLogMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileLogDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super ImportFileLog, ? extends ImportFileLogDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileLog with conditions: {}, page: {}", predicate, pageable);
        Page<ImportFileLog> page = Objects.isNull(predicate)
            ? importFileLogRepository.findAll(pageable)
            : importFileLogRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 importFileLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileLogDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get ImportFileLog with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, importFileLogMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileLogDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super ImportFileLog, ? extends ImportFileLogDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileLog with conditions: {}, sort: {}", predicate, sort);
        Stream<ImportFileLog> stream = Objects.isNull(predicate)
            ? importFileLogRepository.findAll(sort).stream()
            : StreamSupport.stream(importFileLogRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
