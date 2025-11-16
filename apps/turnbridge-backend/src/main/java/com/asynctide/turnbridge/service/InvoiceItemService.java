package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.service.dto.InvoiceItemDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceItemMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.InvoiceItem}.
 */
@Service
@Transactional
public class InvoiceItemService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceItemService.class);

    private final InvoiceItemRepository invoiceItemRepository;

    private final InvoiceItemMapper invoiceItemMapper;

    public InvoiceItemService(InvoiceItemRepository invoiceItemRepository, InvoiceItemMapper invoiceItemMapper) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.invoiceItemMapper = invoiceItemMapper;
    }

    /**
     * Save a invoiceItem.
     *
     * @param invoiceItemDTO the entity to save.
     * @return the persisted entity.
     */
    public InvoiceItemDTO save(InvoiceItemDTO invoiceItemDTO) {
        LOG.debug("Request to save InvoiceItem : {}", invoiceItemDTO);
        InvoiceItem invoiceItem = invoiceItemMapper.toEntity(invoiceItemDTO);
        invoiceItem = invoiceItemRepository.save(invoiceItem);
        return invoiceItemMapper.toDto(invoiceItem);
    }

    /**
     * Update a invoiceItem.
     *
     * @param invoiceItemDTO the entity to save.
     * @return the persisted entity.
     */
    public InvoiceItemDTO update(InvoiceItemDTO invoiceItemDTO) {
        LOG.debug("Request to update InvoiceItem : {}", invoiceItemDTO);
        InvoiceItem invoiceItem = invoiceItemMapper.toEntity(invoiceItemDTO);
        invoiceItem = invoiceItemRepository.save(invoiceItem);
        return invoiceItemMapper.toDto(invoiceItem);
    }

    /**
     * Partially update a invoiceItem.
     *
     * @param invoiceItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InvoiceItemDTO> partialUpdate(InvoiceItemDTO invoiceItemDTO) {
        LOG.debug("Request to partially update InvoiceItem : {}", invoiceItemDTO);

        return invoiceItemRepository
            .findById(invoiceItemDTO.getId())
            .map(existingInvoiceItem -> {
                invoiceItemMapper.partialUpdate(existingInvoiceItem, invoiceItemDTO);

                return existingInvoiceItem;
            })
            .map(invoiceItemRepository::save)
            .map(invoiceItemMapper::toDto);
    }

    /**
     * Get all the invoiceItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InvoiceItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return invoiceItemRepository.findAllWithEagerRelationships(pageable).map(invoiceItemMapper::toDto);
    }

    /**
     * Get one invoiceItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InvoiceItemDTO> findOne(Long id) {
        LOG.debug("Request to get InvoiceItem : {}", id);
        return invoiceItemRepository.findOneWithEagerRelationships(id).map(invoiceItemMapper::toDto);
    }

    /**
     * Delete the invoiceItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete InvoiceItem : {}", id);
        invoiceItemRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 invoiceItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<InvoiceItemDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get InvoiceItem with conditions: {}", predicate);
        return this.findAll(predicate, invoiceItemMapper::toDto);
    }

    /**
     * 取得過濾後的 invoiceItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<InvoiceItemDTO> findAll(Predicate predicate, Function<? super InvoiceItem, ? extends InvoiceItemDTO> mapper) {
        LOG.debug("Request to get InvoiceItem with conditions: {}", predicate);
        Stream<InvoiceItem> stream = Objects.isNull(predicate)
            ? invoiceItemRepository.findAll().stream()
            : StreamSupport.stream(invoiceItemRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 invoiceItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InvoiceItemDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get InvoiceItem with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, invoiceItemMapper::toDto);
    }

    /**
     * 取得過濾後的 invoiceItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InvoiceItemDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super InvoiceItem, ? extends InvoiceItemDTO> mapper
    ) {
        LOG.debug("Request to get InvoiceItem with conditions: {}, page: {}", predicate, pageable);
        Page<InvoiceItem> page = Objects.isNull(predicate)
            ? invoiceItemRepository.findAll(pageable)
            : invoiceItemRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 invoiceItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceItemDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get InvoiceItem with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, invoiceItemMapper::toDto);
    }

    /**
     * 取得過濾後的 invoiceItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceItemDTO> findAll(Predicate predicate, Sort sort, Function<? super InvoiceItem, ? extends InvoiceItemDTO> mapper) {
        LOG.debug("Request to get InvoiceItem with conditions: {}, sort: {}", predicate, sort);
        Stream<InvoiceItem> stream = Objects.isNull(predicate)
            ? invoiceItemRepository.findAll(sort).stream()
            : StreamSupport.stream(invoiceItemRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
