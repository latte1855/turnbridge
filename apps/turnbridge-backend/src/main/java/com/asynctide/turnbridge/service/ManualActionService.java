package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.ManualAction;
import com.asynctide.turnbridge.repository.ManualActionRepository;
import com.asynctide.turnbridge.service.dto.ManualActionDTO;
import com.asynctide.turnbridge.service.mapper.ManualActionMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.ManualAction}.
 */
@Service
@Transactional
public class ManualActionService {

    private static final Logger LOG = LoggerFactory.getLogger(ManualActionService.class);

    private final ManualActionRepository manualActionRepository;

    private final ManualActionMapper manualActionMapper;

    public ManualActionService(ManualActionRepository manualActionRepository, ManualActionMapper manualActionMapper) {
        this.manualActionRepository = manualActionRepository;
        this.manualActionMapper = manualActionMapper;
    }

    /**
     * Save a manualAction.
     *
     * @param manualActionDTO the entity to save.
     * @return the persisted entity.
     */
    public ManualActionDTO save(ManualActionDTO manualActionDTO) {
        LOG.debug("Request to save ManualAction : {}", manualActionDTO);
        ManualAction manualAction = manualActionMapper.toEntity(manualActionDTO);
        manualAction = manualActionRepository.save(manualAction);
        return manualActionMapper.toDto(manualAction);
    }

    /**
     * Update a manualAction.
     *
     * @param manualActionDTO the entity to save.
     * @return the persisted entity.
     */
    public ManualActionDTO update(ManualActionDTO manualActionDTO) {
        LOG.debug("Request to update ManualAction : {}", manualActionDTO);
        ManualAction manualAction = manualActionMapper.toEntity(manualActionDTO);
        manualAction = manualActionRepository.save(manualAction);
        return manualActionMapper.toDto(manualAction);
    }

    /**
     * Partially update a manualAction.
     *
     * @param manualActionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ManualActionDTO> partialUpdate(ManualActionDTO manualActionDTO) {
        LOG.debug("Request to partially update ManualAction : {}", manualActionDTO);

        return manualActionRepository
            .findById(manualActionDTO.getId())
            .map(existingManualAction -> {
                manualActionMapper.partialUpdate(existingManualAction, manualActionDTO);

                return existingManualAction;
            })
            .map(manualActionRepository::save)
            .map(manualActionMapper::toDto);
    }

    /**
     * Get all the manualActions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ManualActionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return manualActionRepository.findAllWithEagerRelationships(pageable).map(manualActionMapper::toDto);
    }

    /**
     * Get one manualAction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ManualActionDTO> findOne(Long id) {
        LOG.debug("Request to get ManualAction : {}", id);
        return manualActionRepository.findOneWithEagerRelationships(id).map(manualActionMapper::toDto);
    }

    /**
     * Delete the manualAction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ManualAction : {}", id);
        manualActionRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 manualAction 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ManualActionDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get ManualAction with conditions: {}", predicate);
        return this.findAll(predicate, manualActionMapper::toDto);
    }

    /**
     * 取得過濾後的 manualAction 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ManualActionDTO> findAll(Predicate predicate, Function<? super ManualAction, ? extends ManualActionDTO> mapper) {
        LOG.debug("Request to get ManualAction with conditions: {}", predicate);
        Stream<ManualAction> stream = Objects.isNull(predicate)
            ? manualActionRepository.findAll().stream()
            : StreamSupport.stream(manualActionRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 manualAction 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ManualActionDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get ManualAction with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, manualActionMapper::toDto);
    }

    /**
     * 取得過濾後的 manualAction 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ManualActionDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super ManualAction, ? extends ManualActionDTO> mapper
    ) {
        LOG.debug("Request to get ManualAction with conditions: {}, page: {}", predicate, pageable);
        Page<ManualAction> page = Objects.isNull(predicate)
            ? manualActionRepository.findAll(pageable)
            : manualActionRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 manualAction 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ManualActionDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get ManualAction with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, manualActionMapper::toDto);
    }

    /**
     * 取得過濾後的 manualAction 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ManualActionDTO> findAll(Predicate predicate, Sort sort, Function<? super ManualAction, ? extends ManualActionDTO> mapper) {
        LOG.debug("Request to get ManualAction with conditions: {}, sort: {}", predicate, sort);
        Stream<ManualAction> stream = Objects.isNull(predicate)
            ? manualActionRepository.findAll(sort).stream()
            : StreamSupport.stream(manualActionRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
