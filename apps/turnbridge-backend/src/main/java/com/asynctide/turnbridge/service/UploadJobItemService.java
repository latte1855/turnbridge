package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.service.dto.UploadJobItemDTO;
import com.asynctide.turnbridge.service.mapper.UploadJobItemMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.UploadJobItem}.
 */
@Service
@Transactional
public class UploadJobItemService {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJobItemService.class);

    private final UploadJobItemRepository uploadJobItemRepository;

    private final UploadJobItemMapper uploadJobItemMapper;

    public UploadJobItemService(UploadJobItemRepository uploadJobItemRepository, UploadJobItemMapper uploadJobItemMapper) {
        this.uploadJobItemRepository = uploadJobItemRepository;
        this.uploadJobItemMapper = uploadJobItemMapper;
    }

    /**
     * Save a uploadJobItem.
     *
     * @param uploadJobItemDTO the entity to save.
     * @return the persisted entity.
     */
    public UploadJobItemDTO save(UploadJobItemDTO uploadJobItemDTO) {
        LOG.debug("Request to save UploadJobItem : {}", uploadJobItemDTO);
        UploadJobItem uploadJobItem = uploadJobItemMapper.toEntity(uploadJobItemDTO);
        uploadJobItem = uploadJobItemRepository.save(uploadJobItem);
        return uploadJobItemMapper.toDto(uploadJobItem);
    }

    /**
     * Update a uploadJobItem.
     *
     * @param uploadJobItemDTO the entity to save.
     * @return the persisted entity.
     */
    public UploadJobItemDTO update(UploadJobItemDTO uploadJobItemDTO) {
        LOG.debug("Request to update UploadJobItem : {}", uploadJobItemDTO);
        UploadJobItem uploadJobItem = uploadJobItemMapper.toEntity(uploadJobItemDTO);
        uploadJobItem = uploadJobItemRepository.save(uploadJobItem);
        return uploadJobItemMapper.toDto(uploadJobItem);
    }

    /**
     * Partially update a uploadJobItem.
     *
     * @param uploadJobItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UploadJobItemDTO> partialUpdate(UploadJobItemDTO uploadJobItemDTO) {
        LOG.debug("Request to partially update UploadJobItem : {}", uploadJobItemDTO);

        return uploadJobItemRepository
            .findById(uploadJobItemDTO.getId())
            .map(existingUploadJobItem -> {
                uploadJobItemMapper.partialUpdate(existingUploadJobItem, uploadJobItemDTO);

                return existingUploadJobItem;
            })
            .map(uploadJobItemRepository::save)
            .map(uploadJobItemMapper::toDto);
    }

    /**
     * Get all the uploadJobItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UploadJobItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return uploadJobItemRepository.findAllWithEagerRelationships(pageable).map(uploadJobItemMapper::toDto);
    }

    /**
     * Get one uploadJobItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UploadJobItemDTO> findOne(Long id) {
        LOG.debug("Request to get UploadJobItem : {}", id);
        return uploadJobItemRepository.findOneWithEagerRelationships(id).map(uploadJobItemMapper::toDto);
    }

    /**
     * Delete the uploadJobItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UploadJobItem : {}", id);
        uploadJobItemRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 uploadJobItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<UploadJobItemDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get UploadJobItem with conditions: {}", predicate);
        return this.findAll(predicate, uploadJobItemMapper::toDto);
    }

    /**
     * 取得過濾後的 uploadJobItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<UploadJobItemDTO> findAll(Predicate predicate, Function<? super UploadJobItem, ? extends UploadJobItemDTO> mapper) {
        LOG.debug("Request to get UploadJobItem with conditions: {}", predicate);
        Stream<UploadJobItem> stream = Objects.isNull(predicate)
            ? uploadJobItemRepository.findAll().stream()
            : StreamSupport.stream(uploadJobItemRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 uploadJobItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadJobItemDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get UploadJobItem with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, uploadJobItemMapper::toDto);
    }

    /**
     * 取得過濾後的 uploadJobItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadJobItemDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super UploadJobItem, ? extends UploadJobItemDTO> mapper
    ) {
        LOG.debug("Request to get UploadJobItem with conditions: {}, page: {}", predicate, pageable);
        Page<UploadJobItem> page = Objects.isNull(predicate)
            ? uploadJobItemRepository.findAll(pageable)
            : uploadJobItemRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 uploadJobItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UploadJobItemDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get UploadJobItem with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, uploadJobItemMapper::toDto);
    }

    /**
     * 取得過濾後的 uploadJobItem 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UploadJobItemDTO> findAll(
        Predicate predicate,
        Sort sort,
        Function<? super UploadJobItem, ? extends UploadJobItemDTO> mapper
    ) {
        LOG.debug("Request to get UploadJobItem with conditions: {}, sort: {}", predicate, sort);
        Stream<UploadJobItem> stream = Objects.isNull(predicate)
            ? uploadJobItemRepository.findAll(sort).stream()
            : StreamSupport.stream(uploadJobItemRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
    
    /** 依數字主鍵 id（UploadJob.id）查詢 */
    @Transactional(readOnly = true)
    public Page<UploadJobItemDTO> findByJobId(Long jobId, Pageable page){
    	return this.uploadJobItemRepository.findByJobId(jobId, page).map(uploadJobItemMapper::toDto);
    }

    /** 依字串 jobId（UploadJob.jobId）查詢 */
    @Transactional(readOnly = true)
    public Page<UploadJobItemDTO> findByJobJobId(String jobId, JobItemStatus status, Pageable pageable) {
        Page<UploadJobItem> page = (status == null)
            ? uploadJobItemRepository.findByJobJobId(jobId, pageable)
            : uploadJobItemRepository.findByJobJobIdAndStatus(jobId, status, pageable);
        return page.map(uploadJobItemMapper::toDto);
    }

	
	/** 依字串 jobId（UploadJob.jobId）把 ERROR 明細改成 QUEUED，回傳影響筆數 */
    public int requeueFailedByJobJobId(String jobId) {
        return uploadJobItemRepository.requeueFailedByJobJobId(jobId);
    }
}
