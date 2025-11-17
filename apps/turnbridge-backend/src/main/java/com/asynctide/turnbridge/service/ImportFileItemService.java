package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.service.dto.ImportFileItemDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileItemMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.ImportFileItem}.
 */
@Service
@Transactional
public class ImportFileItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileItemService.class);

    private final ImportFileItemRepository importFileItemRepository;

    private final ImportFileItemMapper importFileItemMapper;

    public ImportFileItemService(ImportFileItemRepository importFileItemRepository, ImportFileItemMapper importFileItemMapper) {
        this.importFileItemRepository = importFileItemRepository;
        this.importFileItemMapper = importFileItemMapper;
    }

    /**
     * Save a importFileItem.
     *
     * @param importFileItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileItemDTO save(ImportFileItemDTO importFileItemDTO) {
        LOG.debug("Request to save ImportFileItem : {}", importFileItemDTO);
        ImportFileItem importFileItem = importFileItemMapper.toEntity(importFileItemDTO);
        importFileItem = importFileItemRepository.save(importFileItem);
        return importFileItemMapper.toDto(importFileItem);
    }

    /**
     * Update a importFileItem.
     *
     * @param importFileItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ImportFileItemDTO update(ImportFileItemDTO importFileItemDTO) {
        LOG.debug("Request to update ImportFileItem : {}", importFileItemDTO);
        ImportFileItem importFileItem = importFileItemMapper.toEntity(importFileItemDTO);
        importFileItem = importFileItemRepository.save(importFileItem);
        return importFileItemMapper.toDto(importFileItem);
    }

    /**
     * Partially update a importFileItem.
     *
     * @param importFileItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ImportFileItemDTO> partialUpdate(ImportFileItemDTO importFileItemDTO) {
        LOG.debug("Request to partially update ImportFileItem : {}", importFileItemDTO);

        return importFileItemRepository
            .findById(importFileItemDTO.getId())
            .map(existingImportFileItem -> {
                importFileItemMapper.partialUpdate(existingImportFileItem, importFileItemDTO);

                return existingImportFileItem;
            })
            .map(importFileItemRepository::save)
            .map(importFileItemMapper::toDto);
    }

    /**
     * Get all the importFileItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ImportFileItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return importFileItemRepository.findAllWithEagerRelationships(pageable).map(importFileItemMapper::toDto);
    }

    /**
     * Get one importFileItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ImportFileItemDTO> findOne(Long id) {
        LOG.debug("Request to get ImportFileItem : {}", id);
        return importFileItemRepository.findOneWithEagerRelationships(id).map(importFileItemMapper::toDto);
    }

    /**
     * Delete the importFileItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ImportFileItem : {}", id);
        importFileItemRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 importFileItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get ImportFileItem with conditions: {}", predicate);
        return this.findAll(predicate, importFileItemMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemDTO> findAll(Predicate predicate, Function<? super ImportFileItem, ? extends ImportFileItemDTO> mapper) {
        LOG.debug("Request to get ImportFileItem with conditions: {}", predicate);
        Stream<ImportFileItem> stream = Objects.isNull(predicate)
            ? importFileItemRepository.findAll().stream()
            : StreamSupport.stream(importFileItemRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 importFileItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileItemDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get ImportFileItem with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, importFileItemMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileItemDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super ImportFileItem, ? extends ImportFileItemDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileItem with conditions: {}, page: {}", predicate, pageable);
        Page<ImportFileItem> page = Objects.isNull(predicate)
            ? importFileItemRepository.findAll(pageable)
            : importFileItemRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 importFileItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get ImportFileItem with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, importFileItemMapper::toDto);
    }

    /**
     * 取得過濾後的 importFileItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImportFileItemDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super ImportFileItem, ? extends ImportFileItemDTO> mapper
    ) {
        LOG.debug("Request to get ImportFileItem with conditions: {}, sort: {}", predicate, sort);
        Stream<ImportFileItem> stream = Objects.isNull(predicate)
            ? importFileItemRepository.findAll(sort).stream()
            : StreamSupport.stream(importFileItemRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
