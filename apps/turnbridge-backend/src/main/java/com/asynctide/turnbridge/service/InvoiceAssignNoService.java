package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.InvoiceAssignNo;
import com.asynctide.turnbridge.repository.InvoiceAssignNoRepository;
import com.asynctide.turnbridge.service.dto.InvoiceAssignNoDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceAssignNoMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.InvoiceAssignNo}.
 */
@Service
@Transactional
public class InvoiceAssignNoService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceAssignNoService.class);

    private final InvoiceAssignNoRepository invoiceAssignNoRepository;

    private final InvoiceAssignNoMapper invoiceAssignNoMapper;

    public InvoiceAssignNoService(InvoiceAssignNoRepository invoiceAssignNoRepository, InvoiceAssignNoMapper invoiceAssignNoMapper) {
        this.invoiceAssignNoRepository = invoiceAssignNoRepository;
        this.invoiceAssignNoMapper = invoiceAssignNoMapper;
    }

    /**
     * Save a invoiceAssignNo.
     *
     * @param invoiceAssignNoDTO the entity to save.
     * @return the persisted entity.
     */
    public InvoiceAssignNoDTO save(InvoiceAssignNoDTO invoiceAssignNoDTO) {
        LOG.debug("Request to save InvoiceAssignNo : {}", invoiceAssignNoDTO);
        InvoiceAssignNo invoiceAssignNo = invoiceAssignNoMapper.toEntity(invoiceAssignNoDTO);
        invoiceAssignNo = invoiceAssignNoRepository.save(invoiceAssignNo);
        return invoiceAssignNoMapper.toDto(invoiceAssignNo);
    }

    /**
     * Update a invoiceAssignNo.
     *
     * @param invoiceAssignNoDTO the entity to save.
     * @return the persisted entity.
     */
    public InvoiceAssignNoDTO update(InvoiceAssignNoDTO invoiceAssignNoDTO) {
        LOG.debug("Request to update InvoiceAssignNo : {}", invoiceAssignNoDTO);
        InvoiceAssignNo invoiceAssignNo = invoiceAssignNoMapper.toEntity(invoiceAssignNoDTO);
        invoiceAssignNo = invoiceAssignNoRepository.save(invoiceAssignNo);
        return invoiceAssignNoMapper.toDto(invoiceAssignNo);
    }

    /**
     * Partially update a invoiceAssignNo.
     *
     * @param invoiceAssignNoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InvoiceAssignNoDTO> partialUpdate(InvoiceAssignNoDTO invoiceAssignNoDTO) {
        LOG.debug("Request to partially update InvoiceAssignNo : {}", invoiceAssignNoDTO);

        return invoiceAssignNoRepository
            .findById(invoiceAssignNoDTO.getId())
            .map(existingInvoiceAssignNo -> {
                invoiceAssignNoMapper.partialUpdate(existingInvoiceAssignNo, invoiceAssignNoDTO);

                return existingInvoiceAssignNo;
            })
            .map(invoiceAssignNoRepository::save)
            .map(invoiceAssignNoMapper::toDto);
    }

    /**
     * Get all the invoiceAssignNos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InvoiceAssignNoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return invoiceAssignNoRepository.findAllWithEagerRelationships(pageable).map(invoiceAssignNoMapper::toDto);
    }

    /**
     * Get one invoiceAssignNo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InvoiceAssignNoDTO> findOne(Long id) {
        LOG.debug("Request to get InvoiceAssignNo : {}", id);
        return invoiceAssignNoRepository.findOneWithEagerRelationships(id).map(invoiceAssignNoMapper::toDto);
    }

    /**
     * Delete the invoiceAssignNo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete InvoiceAssignNo : {}", id);
        invoiceAssignNoRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 invoiceAssignNo 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<InvoiceAssignNoDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get InvoiceAssignNo with conditions: {}", predicate);
        return this.findAll(predicate, invoiceAssignNoMapper::toDto);
    }

    /**
     * 取得過濾後的 invoiceAssignNo 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<InvoiceAssignNoDTO> findAll(Predicate predicate, Function<? super InvoiceAssignNo, ? extends InvoiceAssignNoDTO> mapper) {
        LOG.debug("Request to get InvoiceAssignNo with conditions: {}", predicate);
        Stream<InvoiceAssignNo> stream = Objects.isNull(predicate)
            ? invoiceAssignNoRepository.findAll().stream()
            : StreamSupport.stream(invoiceAssignNoRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 invoiceAssignNo 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InvoiceAssignNoDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get InvoiceAssignNo with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, invoiceAssignNoMapper::toDto);
    }

    /**
     * 取得過濾後的 invoiceAssignNo 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InvoiceAssignNoDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super InvoiceAssignNo, ? extends InvoiceAssignNoDTO> mapper
    ) {
        LOG.debug("Request to get InvoiceAssignNo with conditions: {}, page: {}", predicate, pageable);
        Page<InvoiceAssignNo> page = Objects.isNull(predicate)
            ? invoiceAssignNoRepository.findAll(pageable)
            : invoiceAssignNoRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 invoiceAssignNo 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceAssignNoDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get InvoiceAssignNo with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, invoiceAssignNoMapper::toDto);
    }

    /**
     * 取得過濾後的 invoiceAssignNo 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceAssignNoDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super InvoiceAssignNo, ? extends InvoiceAssignNoDTO> mapper
    ) {
        LOG.debug("Request to get InvoiceAssignNo with conditions: {}, sort: {}", predicate, sort);
        Stream<InvoiceAssignNo> stream = Objects.isNull(predicate)
            ? invoiceAssignNoRepository.findAll(sort).stream()
            : StreamSupport.stream(invoiceAssignNoRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
