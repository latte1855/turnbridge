package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
import com.asynctide.turnbridge.service.mapper.UploadJobMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.UploadJob}.
 */
@Service
@Transactional
public class UploadJobService {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJobService.class);

    private final UploadJobRepository uploadJobRepository;

    private final UploadJobMapper uploadJobMapper;

    public UploadJobService(UploadJobRepository uploadJobRepository, UploadJobMapper uploadJobMapper) {
        this.uploadJobRepository = uploadJobRepository;
        this.uploadJobMapper = uploadJobMapper;
    }

    /**
     * Save a uploadJob.
     *
     * @param uploadJobDTO the entity to save.
     * @return the persisted entity.
     */
    public UploadJobDTO save(UploadJobDTO uploadJobDTO) {
        LOG.debug("Request to save UploadJob : {}", uploadJobDTO);
        UploadJob uploadJob = uploadJobMapper.toEntity(uploadJobDTO);
        uploadJob = uploadJobRepository.save(uploadJob);
        return uploadJobMapper.toDto(uploadJob);
    }

    /**
     * Update a uploadJob.
     *
     * @param uploadJobDTO the entity to save.
     * @return the persisted entity.
     */
    public UploadJobDTO update(UploadJobDTO uploadJobDTO) {
        LOG.debug("Request to update UploadJob : {}", uploadJobDTO);
        UploadJob uploadJob = uploadJobMapper.toEntity(uploadJobDTO);
        uploadJob = uploadJobRepository.save(uploadJob);
        return uploadJobMapper.toDto(uploadJob);
    }

    /**
     * Partially update a uploadJob.
     *
     * @param uploadJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UploadJobDTO> partialUpdate(UploadJobDTO uploadJobDTO) {
        LOG.debug("Request to partially update UploadJob : {}", uploadJobDTO);

        return uploadJobRepository
            .findById(uploadJobDTO.getId())
            .map(existingUploadJob -> {
                uploadJobMapper.partialUpdate(existingUploadJob, uploadJobDTO);

                return existingUploadJob;
            })
            .map(uploadJobRepository::save)
            .map(uploadJobMapper::toDto);
    }

    /**
     * Get one uploadJob by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UploadJobDTO> findOne(Long id) {
        LOG.debug("Request to get UploadJob : {}", id);
        return uploadJobRepository.findById(id).map(uploadJobMapper::toDto);
    }

    /**
     * Delete the uploadJob by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UploadJob : {}", id);
        uploadJobRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 uploadJob 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<UploadJobDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get UploadJob with conditions: {}", predicate);
        return this.findAll(predicate, uploadJobMapper::toDto);
    }

    /**
     * 取得過濾後的 uploadJob 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<UploadJobDTO> findAll(Predicate predicate, Function<? super UploadJob, ? extends UploadJobDTO> mapper) {
        LOG.debug("Request to get UploadJob with conditions: {}", predicate);
        Stream<UploadJob> stream = Objects.isNull(predicate)
            ? uploadJobRepository.findAll().stream()
            : StreamSupport.stream(uploadJobRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 uploadJob 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadJobDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get UploadJob with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, uploadJobMapper::toDto);
    }

    /**
     * 取得過濾後的 uploadJob 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadJobDTO> findAll(Predicate predicate, Pageable pageable, Function<? super UploadJob, ? extends UploadJobDTO> mapper) {
        LOG.debug("Request to get UploadJob with conditions: {}, page: {}", predicate, pageable);
        Page<UploadJob> page = Objects.isNull(predicate)
            ? uploadJobRepository.findAll(pageable)
            : uploadJobRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 uploadJob 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UploadJobDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get UploadJob with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, uploadJobMapper::toDto);
    }

    /**
     * 取得過濾後的 uploadJob 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UploadJobDTO> findAll(Predicate predicate, Sort sort, Function<? super UploadJob, ? extends UploadJobDTO> mapper) {
        LOG.debug("Request to get UploadJob with conditions: {}, sort: {}", predicate, sort);
        Stream<UploadJob> stream = Objects.isNull(predicate)
            ? uploadJobRepository.findAll(sort).stream()
            : StreamSupport.stream(uploadJobRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
	public Optional<UploadJobDTO> findOneByJobId(String jobId) {
		LOG.debug("Request to get UploadJob by JobId(String) : {}", jobId);
		return uploadJobRepository.findOneByJobId(jobId).map(uploadJobMapper::toDto);
	}
}
