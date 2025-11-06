package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.service.mapper.StoredObjectMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.StoredObject}.
 */
@Service
@Transactional
public class StoredObjectService {

    private static final Logger LOG = LoggerFactory.getLogger(StoredObjectService.class);

    private final StoredObjectRepository storedObjectRepository;

    private final StoredObjectMapper storedObjectMapper;

    public StoredObjectService(StoredObjectRepository storedObjectRepository, StoredObjectMapper storedObjectMapper) {
        this.storedObjectRepository = storedObjectRepository;
        this.storedObjectMapper = storedObjectMapper;
    }

    /**
     * Save a storedObject.
     *
     * @param storedObjectDTO the entity to save.
     * @return the persisted entity.
     */
    public StoredObjectDTO save(StoredObjectDTO storedObjectDTO) {
        LOG.debug("Request to save StoredObject : {}", storedObjectDTO);
        StoredObject storedObject = storedObjectMapper.toEntity(storedObjectDTO);
        storedObject = storedObjectRepository.save(storedObject);
        return storedObjectMapper.toDto(storedObject);
    }

    /**
     * Update a storedObject.
     *
     * @param storedObjectDTO the entity to save.
     * @return the persisted entity.
     */
    public StoredObjectDTO update(StoredObjectDTO storedObjectDTO) {
        LOG.debug("Request to update StoredObject : {}", storedObjectDTO);
        StoredObject storedObject = storedObjectMapper.toEntity(storedObjectDTO);
        storedObject = storedObjectRepository.save(storedObject);
        return storedObjectMapper.toDto(storedObject);
    }

    /**
     * Partially update a storedObject.
     *
     * @param storedObjectDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StoredObjectDTO> partialUpdate(StoredObjectDTO storedObjectDTO) {
        LOG.debug("Request to partially update StoredObject : {}", storedObjectDTO);

        return storedObjectRepository
            .findById(storedObjectDTO.getId())
            .map(existingStoredObject -> {
                storedObjectMapper.partialUpdate(existingStoredObject, storedObjectDTO);

                return existingStoredObject;
            })
            .map(storedObjectRepository::save)
            .map(storedObjectMapper::toDto);
    }

    /**
     * Get one storedObject by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StoredObjectDTO> findOne(Long id) {
        LOG.debug("Request to get StoredObject : {}", id);
        return storedObjectRepository.findById(id).map(storedObjectMapper::toDto);
    }

    /**
     * Delete the storedObject by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StoredObject : {}", id);
        storedObjectRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 storedObject 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<StoredObjectDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get StoredObject with conditions: {}", predicate);
        return this.findAll(predicate, storedObjectMapper::toDto);
    }

    /**
     * 取得過濾後的 storedObject 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<StoredObjectDTO> findAll(Predicate predicate, Function<? super StoredObject, ? extends StoredObjectDTO> mapper) {
        LOG.debug("Request to get StoredObject with conditions: {}", predicate);
        Stream<StoredObject> stream = Objects.isNull(predicate)
            ? storedObjectRepository.findAll().stream()
            : StreamSupport.stream(storedObjectRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 storedObject 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StoredObjectDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get StoredObject with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, storedObjectMapper::toDto);
    }

    /**
     * 取得過濾後的 storedObject 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StoredObjectDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super StoredObject, ? extends StoredObjectDTO> mapper
    ) {
        LOG.debug("Request to get StoredObject with conditions: {}, page: {}", predicate, pageable);
        Page<StoredObject> page = Objects.isNull(predicate)
            ? storedObjectRepository.findAll(pageable)
            : storedObjectRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 storedObject 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StoredObjectDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get StoredObject with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, storedObjectMapper::toDto);
    }

    /**
     * 取得過濾後的 storedObject 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StoredObjectDTO> findAll(Predicate predicate, Sort sort, Function<? super StoredObject, ? extends StoredObjectDTO> mapper) {
        LOG.debug("Request to get StoredObject with conditions: {}, sort: {}", predicate, sort);
        Stream<StoredObject> stream = Objects.isNull(predicate)
            ? storedObjectRepository.findAll(sort).stream()
            : StreamSupport.stream(storedObjectRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
