package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.TurnkeyMessage;
import com.asynctide.turnbridge.repository.TurnkeyMessageRepository;
import com.asynctide.turnbridge.service.dto.TurnkeyMessageDTO;
import com.asynctide.turnbridge.service.mapper.TurnkeyMessageMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.TurnkeyMessage}.
 */
@Service
@Transactional
public class TurnkeyMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyMessageService.class);

    private final TurnkeyMessageRepository turnkeyMessageRepository;

    private final TurnkeyMessageMapper turnkeyMessageMapper;

    public TurnkeyMessageService(TurnkeyMessageRepository turnkeyMessageRepository, TurnkeyMessageMapper turnkeyMessageMapper) {
        this.turnkeyMessageRepository = turnkeyMessageRepository;
        this.turnkeyMessageMapper = turnkeyMessageMapper;
    }

    /**
     * Save a turnkeyMessage.
     *
     * @param turnkeyMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public TurnkeyMessageDTO save(TurnkeyMessageDTO turnkeyMessageDTO) {
        LOG.debug("Request to save TurnkeyMessage : {}", turnkeyMessageDTO);
        TurnkeyMessage turnkeyMessage = turnkeyMessageMapper.toEntity(turnkeyMessageDTO);
        turnkeyMessage = turnkeyMessageRepository.save(turnkeyMessage);
        return turnkeyMessageMapper.toDto(turnkeyMessage);
    }

    /**
     * Update a turnkeyMessage.
     *
     * @param turnkeyMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public TurnkeyMessageDTO update(TurnkeyMessageDTO turnkeyMessageDTO) {
        LOG.debug("Request to update TurnkeyMessage : {}", turnkeyMessageDTO);
        TurnkeyMessage turnkeyMessage = turnkeyMessageMapper.toEntity(turnkeyMessageDTO);
        turnkeyMessage = turnkeyMessageRepository.save(turnkeyMessage);
        return turnkeyMessageMapper.toDto(turnkeyMessage);
    }

    /**
     * Partially update a turnkeyMessage.
     *
     * @param turnkeyMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TurnkeyMessageDTO> partialUpdate(TurnkeyMessageDTO turnkeyMessageDTO) {
        LOG.debug("Request to partially update TurnkeyMessage : {}", turnkeyMessageDTO);

        return turnkeyMessageRepository
            .findById(turnkeyMessageDTO.getId())
            .map(existingTurnkeyMessage -> {
                turnkeyMessageMapper.partialUpdate(existingTurnkeyMessage, turnkeyMessageDTO);

                return existingTurnkeyMessage;
            })
            .map(turnkeyMessageRepository::save)
            .map(turnkeyMessageMapper::toDto);
    }

    /**
     * Get all the turnkeyMessages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TurnkeyMessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return turnkeyMessageRepository.findAllWithEagerRelationships(pageable).map(turnkeyMessageMapper::toDto);
    }

    /**
     * Get one turnkeyMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TurnkeyMessageDTO> findOne(Long id) {
        LOG.debug("Request to get TurnkeyMessage : {}", id);
        return turnkeyMessageRepository.findOneWithEagerRelationships(id).map(turnkeyMessageMapper::toDto);
    }

    /**
     * Delete the turnkeyMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TurnkeyMessage : {}", id);
        turnkeyMessageRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 turnkeyMessage 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<TurnkeyMessageDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get TurnkeyMessage with conditions: {}", predicate);
        return this.findAll(predicate, turnkeyMessageMapper::toDto);
    }

    /**
     * 取得過濾後的 turnkeyMessage 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<TurnkeyMessageDTO> findAll(Predicate predicate, Function<? super TurnkeyMessage, ? extends TurnkeyMessageDTO> mapper) {
        LOG.debug("Request to get TurnkeyMessage with conditions: {}", predicate);
        Stream<TurnkeyMessage> stream = Objects.isNull(predicate)
            ? turnkeyMessageRepository.findAll().stream()
            : StreamSupport.stream(turnkeyMessageRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 turnkeyMessage 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TurnkeyMessageDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get TurnkeyMessage with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, turnkeyMessageMapper::toDto);
    }

    /**
     * 取得過濾後的 turnkeyMessage 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TurnkeyMessageDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super TurnkeyMessage, ? extends TurnkeyMessageDTO> mapper
    ) {
        LOG.debug("Request to get TurnkeyMessage with conditions: {}, page: {}", predicate, pageable);
        Page<TurnkeyMessage> page = Objects.isNull(predicate)
            ? turnkeyMessageRepository.findAll(pageable)
            : turnkeyMessageRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 turnkeyMessage 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TurnkeyMessageDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get TurnkeyMessage with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, turnkeyMessageMapper::toDto);
    }

    /**
     * 取得過濾後的 turnkeyMessage 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TurnkeyMessageDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super TurnkeyMessage, ? extends TurnkeyMessageDTO> mapper
    ) {
        LOG.debug("Request to get TurnkeyMessage with conditions: {}, sort: {}", predicate, sort);
        Stream<TurnkeyMessage> stream = Objects.isNull(predicate)
            ? turnkeyMessageRepository.findAll(sort).stream()
            : StreamSupport.stream(turnkeyMessageRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
