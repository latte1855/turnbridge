package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.repository.WebhookEndpointRepository;
import com.asynctide.turnbridge.service.dto.WebhookEndpointDTO;
import com.asynctide.turnbridge.service.mapper.WebhookEndpointMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.WebhookEndpoint}.
 */
@Service
@Transactional
public class WebhookEndpointService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookEndpointService.class);

    private final WebhookEndpointRepository webhookEndpointRepository;

    private final WebhookEndpointMapper webhookEndpointMapper;

    public WebhookEndpointService(WebhookEndpointRepository webhookEndpointRepository, WebhookEndpointMapper webhookEndpointMapper) {
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.webhookEndpointMapper = webhookEndpointMapper;
    }

    /**
     * Save a webhookEndpoint.
     *
     * @param webhookEndpointDTO the entity to save.
     * @return the persisted entity.
     */
    public WebhookEndpointDTO save(WebhookEndpointDTO webhookEndpointDTO) {
        LOG.debug("Request to save WebhookEndpoint : {}", webhookEndpointDTO);
        WebhookEndpoint webhookEndpoint = webhookEndpointMapper.toEntity(webhookEndpointDTO);
        webhookEndpoint = webhookEndpointRepository.save(webhookEndpoint);
        return webhookEndpointMapper.toDto(webhookEndpoint);
    }

    /**
     * Update a webhookEndpoint.
     *
     * @param webhookEndpointDTO the entity to save.
     * @return the persisted entity.
     */
    public WebhookEndpointDTO update(WebhookEndpointDTO webhookEndpointDTO) {
        LOG.debug("Request to update WebhookEndpoint : {}", webhookEndpointDTO);
        WebhookEndpoint webhookEndpoint = webhookEndpointMapper.toEntity(webhookEndpointDTO);
        webhookEndpoint = webhookEndpointRepository.save(webhookEndpoint);
        return webhookEndpointMapper.toDto(webhookEndpoint);
    }

    /**
     * Partially update a webhookEndpoint.
     *
     * @param webhookEndpointDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WebhookEndpointDTO> partialUpdate(WebhookEndpointDTO webhookEndpointDTO) {
        LOG.debug("Request to partially update WebhookEndpoint : {}", webhookEndpointDTO);

        return webhookEndpointRepository
            .findById(webhookEndpointDTO.getId())
            .map(existingWebhookEndpoint -> {
                webhookEndpointMapper.partialUpdate(existingWebhookEndpoint, webhookEndpointDTO);

                return existingWebhookEndpoint;
            })
            .map(webhookEndpointRepository::save)
            .map(webhookEndpointMapper::toDto);
    }

    /**
     * Get all the webhookEndpoints with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WebhookEndpointDTO> findAllWithEagerRelationships(Pageable pageable) {
        return webhookEndpointRepository.findAllWithEagerRelationships(pageable).map(webhookEndpointMapper::toDto);
    }

    /**
     * Get one webhookEndpoint by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WebhookEndpointDTO> findOne(Long id) {
        LOG.debug("Request to get WebhookEndpoint : {}", id);
        return webhookEndpointRepository.findOneWithEagerRelationships(id).map(webhookEndpointMapper::toDto);
    }

    /**
     * Delete the webhookEndpoint by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WebhookEndpoint : {}", id);
        webhookEndpointRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 webhookEndpoint 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<WebhookEndpointDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get WebhookEndpoint with conditions: {}", predicate);
        return this.findAll(predicate, webhookEndpointMapper::toDto);
    }

    /**
     * 取得過濾後的 webhookEndpoint 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<WebhookEndpointDTO> findAll(Predicate predicate, Function<? super WebhookEndpoint, ? extends WebhookEndpointDTO> mapper) {
        LOG.debug("Request to get WebhookEndpoint with conditions: {}", predicate);
        Stream<WebhookEndpoint> stream = Objects.isNull(predicate)
            ? webhookEndpointRepository.findAll().stream()
            : StreamSupport.stream(webhookEndpointRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 webhookEndpoint 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookEndpointDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get WebhookEndpoint with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, webhookEndpointMapper::toDto);
    }

    /**
     * 取得過濾後的 webhookEndpoint 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookEndpointDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super WebhookEndpoint, ? extends WebhookEndpointDTO> mapper
    ) {
        LOG.debug("Request to get WebhookEndpoint with conditions: {}, page: {}", predicate, pageable);
        Page<WebhookEndpoint> page = Objects.isNull(predicate)
            ? webhookEndpointRepository.findAll(pageable)
            : webhookEndpointRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 webhookEndpoint 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WebhookEndpointDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get WebhookEndpoint with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, webhookEndpointMapper::toDto);
    }

    /**
     * 取得過濾後的 webhookEndpoint 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WebhookEndpointDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super WebhookEndpoint, ? extends WebhookEndpointDTO> mapper
    ) {
        LOG.debug("Request to get WebhookEndpoint with conditions: {}, sort: {}", predicate, sort);
        Stream<WebhookEndpoint> stream = Objects.isNull(predicate)
            ? webhookEndpointRepository.findAll(sort).stream()
            : StreamSupport.stream(webhookEndpointRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
