package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import com.asynctide.turnbridge.service.dto.WebhookDeliveryLogDTO;
import com.asynctide.turnbridge.service.mapper.WebhookDeliveryLogMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.WebhookDeliveryLog}.
 */
@Service
@Transactional
public class WebhookDeliveryLogService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDeliveryLogService.class);

    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;

    private final WebhookDeliveryLogMapper webhookDeliveryLogMapper;

    public WebhookDeliveryLogService(
        WebhookDeliveryLogRepository webhookDeliveryLogRepository,
        WebhookDeliveryLogMapper webhookDeliveryLogMapper
    ) {
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.webhookDeliveryLogMapper = webhookDeliveryLogMapper;
    }

    /**
     * Save a webhookDeliveryLog.
     *
     * @param webhookDeliveryLogDTO the entity to save.
     * @return the persisted entity.
     */
    public WebhookDeliveryLogDTO save(WebhookDeliveryLogDTO webhookDeliveryLogDTO) {
        LOG.debug("Request to save WebhookDeliveryLog : {}", webhookDeliveryLogDTO);
        WebhookDeliveryLog webhookDeliveryLog = webhookDeliveryLogMapper.toEntity(webhookDeliveryLogDTO);
        webhookDeliveryLog = webhookDeliveryLogRepository.save(webhookDeliveryLog);
        return webhookDeliveryLogMapper.toDto(webhookDeliveryLog);
    }

    /**
     * Update a webhookDeliveryLog.
     *
     * @param webhookDeliveryLogDTO the entity to save.
     * @return the persisted entity.
     */
    public WebhookDeliveryLogDTO update(WebhookDeliveryLogDTO webhookDeliveryLogDTO) {
        LOG.debug("Request to update WebhookDeliveryLog : {}", webhookDeliveryLogDTO);
        WebhookDeliveryLog webhookDeliveryLog = webhookDeliveryLogMapper.toEntity(webhookDeliveryLogDTO);
        webhookDeliveryLog = webhookDeliveryLogRepository.save(webhookDeliveryLog);
        return webhookDeliveryLogMapper.toDto(webhookDeliveryLog);
    }

    /**
     * Partially update a webhookDeliveryLog.
     *
     * @param webhookDeliveryLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WebhookDeliveryLogDTO> partialUpdate(WebhookDeliveryLogDTO webhookDeliveryLogDTO) {
        LOG.debug("Request to partially update WebhookDeliveryLog : {}", webhookDeliveryLogDTO);

        return webhookDeliveryLogRepository
            .findById(webhookDeliveryLogDTO.getId())
            .map(existingWebhookDeliveryLog -> {
                webhookDeliveryLogMapper.partialUpdate(existingWebhookDeliveryLog, webhookDeliveryLogDTO);

                return existingWebhookDeliveryLog;
            })
            .map(webhookDeliveryLogRepository::save)
            .map(webhookDeliveryLogMapper::toDto);
    }

    /**
     * Get all the webhookDeliveryLogs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WebhookDeliveryLogDTO> findAllWithEagerRelationships(Pageable pageable) {
        return webhookDeliveryLogRepository.findAllWithEagerRelationships(pageable).map(webhookDeliveryLogMapper::toDto);
    }

    /**
     * Get one webhookDeliveryLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WebhookDeliveryLogDTO> findOne(Long id) {
        LOG.debug("Request to get WebhookDeliveryLog : {}", id);
        return webhookDeliveryLogRepository.findOneWithEagerRelationships(id).map(webhookDeliveryLogMapper::toDto);
    }

    /**
     * Delete the webhookDeliveryLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WebhookDeliveryLog : {}", id);
        webhookDeliveryLogRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 webhookDeliveryLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<WebhookDeliveryLogDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get WebhookDeliveryLog with conditions: {}", predicate);
        return this.findAll(predicate, webhookDeliveryLogMapper::toDto);
    }

    /**
     * 取得過濾後的 webhookDeliveryLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<WebhookDeliveryLogDTO> findAll(
        Predicate predicate,
        Function<? super WebhookDeliveryLog, ? extends WebhookDeliveryLogDTO> mapper
    ) {
        LOG.debug("Request to get WebhookDeliveryLog with conditions: {}", predicate);
        Stream<WebhookDeliveryLog> stream = Objects.isNull(predicate)
            ? webhookDeliveryLogRepository.findAll().stream()
            : StreamSupport.stream(webhookDeliveryLogRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 webhookDeliveryLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookDeliveryLogDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get WebhookDeliveryLog with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, webhookDeliveryLogMapper::toDto);
    }

    /**
     * 取得過濾後的 webhookDeliveryLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookDeliveryLogDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super WebhookDeliveryLog, ? extends WebhookDeliveryLogDTO> mapper
    ) {
        LOG.debug("Request to get WebhookDeliveryLog with conditions: {}, page: {}", predicate, pageable);
        Page<WebhookDeliveryLog> page = Objects.isNull(predicate)
            ? webhookDeliveryLogRepository.findAll(pageable)
            : webhookDeliveryLogRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 webhookDeliveryLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WebhookDeliveryLogDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get WebhookDeliveryLog with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, webhookDeliveryLogMapper::toDto);
    }

    /**
     * 取得過濾後的 webhookDeliveryLog 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WebhookDeliveryLogDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super WebhookDeliveryLog, ? extends WebhookDeliveryLogDTO> mapper
    ) {
        LOG.debug("Request to get WebhookDeliveryLog with conditions: {}, sort: {}", predicate, sort);
        Stream<WebhookDeliveryLog> stream = Objects.isNull(predicate)
            ? webhookDeliveryLogRepository.findAll(sort).stream()
            : StreamSupport.stream(webhookDeliveryLogRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
