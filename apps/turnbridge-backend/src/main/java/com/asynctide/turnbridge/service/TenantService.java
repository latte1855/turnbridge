package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.service.dto.TenantDTO;
import com.asynctide.turnbridge.service.mapper.TenantMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.Tenant}.
 */
@Service
@Transactional
public class TenantService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;

    private final TenantMapper tenantMapper;

    public TenantService(TenantRepository tenantRepository, TenantMapper tenantMapper) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
    }

    /**
     * Save a tenant.
     *
     * @param tenantDTO the entity to save.
     * @return the persisted entity.
     */
    public TenantDTO save(TenantDTO tenantDTO) {
        LOG.debug("Request to save Tenant : {}", tenantDTO);
        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        tenant = tenantRepository.save(tenant);
        return tenantMapper.toDto(tenant);
    }

    /**
     * Update a tenant.
     *
     * @param tenantDTO the entity to save.
     * @return the persisted entity.
     */
    public TenantDTO update(TenantDTO tenantDTO) {
        LOG.debug("Request to update Tenant : {}", tenantDTO);
        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        tenant = tenantRepository.save(tenant);
        return tenantMapper.toDto(tenant);
    }

    /**
     * Partially update a tenant.
     *
     * @param tenantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TenantDTO> partialUpdate(TenantDTO tenantDTO) {
        LOG.debug("Request to partially update Tenant : {}", tenantDTO);

        return tenantRepository
            .findById(tenantDTO.getId())
            .map(existingTenant -> {
                tenantMapper.partialUpdate(existingTenant, tenantDTO);

                return existingTenant;
            })
            .map(tenantRepository::save)
            .map(tenantMapper::toDto);
    }

    /**
     * Get one tenant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TenantDTO> findOne(Long id) {
        LOG.debug("Request to get Tenant : {}", id);
        return tenantRepository.findById(id).map(tenantMapper::toDto);
    }

    /**
     * Delete the tenant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Tenant : {}", id);
        tenantRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 tenant 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get Tenant with conditions: {}", predicate);
        return this.findAll(predicate, tenantMapper::toDto);
    }

    /**
     * 取得過濾後的 tenant 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll(Predicate predicate, Function<? super Tenant, ? extends TenantDTO> mapper) {
        LOG.debug("Request to get Tenant with conditions: {}", predicate);
        Stream<Tenant> stream = Objects.isNull(predicate)
            ? tenantRepository.findAll().stream()
            : StreamSupport.stream(tenantRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 tenant 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get Tenant with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, tenantMapper::toDto);
    }

    /**
     * 取得過濾後的 tenant 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantDTO> findAll(Predicate predicate, Pageable pageable, Function<? super Tenant, ? extends TenantDTO> mapper) {
        LOG.debug("Request to get Tenant with conditions: {}, page: {}", predicate, pageable);
        Page<Tenant> page = Objects.isNull(predicate) ? tenantRepository.findAll(pageable) : tenantRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 tenant 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get Tenant with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, tenantMapper::toDto);
    }

    /**
     * 取得過濾後的 tenant 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll(Predicate predicate, Sort sort, Function<? super Tenant, ? extends TenantDTO> mapper) {
        LOG.debug("Request to get Tenant with conditions: {}, sort: {}", predicate, sort);
        Stream<Tenant> stream = Objects.isNull(predicate)
            ? tenantRepository.findAll(sort).stream()
            : StreamSupport.stream(tenantRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
