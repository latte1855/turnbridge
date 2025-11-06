package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.TrackRange;
import com.asynctide.turnbridge.repository.TrackRangeRepository;
import com.asynctide.turnbridge.service.dto.TrackRangeDTO;
import com.asynctide.turnbridge.service.mapper.TrackRangeMapper;
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
 * Service Implementation for managing {@link com.asynctide.turnbridge.domain.TrackRange}.
 */
@Service
@Transactional
public class TrackRangeService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackRangeService.class);

    private final TrackRangeRepository trackRangeRepository;

    private final TrackRangeMapper trackRangeMapper;

    public TrackRangeService(TrackRangeRepository trackRangeRepository, TrackRangeMapper trackRangeMapper) {
        this.trackRangeRepository = trackRangeRepository;
        this.trackRangeMapper = trackRangeMapper;
    }

    /**
     * Save a trackRange.
     *
     * @param trackRangeDTO the entity to save.
     * @return the persisted entity.
     */
    public TrackRangeDTO save(TrackRangeDTO trackRangeDTO) {
        LOG.debug("Request to save TrackRange : {}", trackRangeDTO);
        TrackRange trackRange = trackRangeMapper.toEntity(trackRangeDTO);
        trackRange = trackRangeRepository.save(trackRange);
        return trackRangeMapper.toDto(trackRange);
    }

    /**
     * Update a trackRange.
     *
     * @param trackRangeDTO the entity to save.
     * @return the persisted entity.
     */
    public TrackRangeDTO update(TrackRangeDTO trackRangeDTO) {
        LOG.debug("Request to update TrackRange : {}", trackRangeDTO);
        TrackRange trackRange = trackRangeMapper.toEntity(trackRangeDTO);
        trackRange = trackRangeRepository.save(trackRange);
        return trackRangeMapper.toDto(trackRange);
    }

    /**
     * Partially update a trackRange.
     *
     * @param trackRangeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TrackRangeDTO> partialUpdate(TrackRangeDTO trackRangeDTO) {
        LOG.debug("Request to partially update TrackRange : {}", trackRangeDTO);

        return trackRangeRepository
            .findById(trackRangeDTO.getId())
            .map(existingTrackRange -> {
                trackRangeMapper.partialUpdate(existingTrackRange, trackRangeDTO);

                return existingTrackRange;
            })
            .map(trackRangeRepository::save)
            .map(trackRangeMapper::toDto);
    }

    /**
     * Get one trackRange by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TrackRangeDTO> findOne(Long id) {
        LOG.debug("Request to get TrackRange : {}", id);
        return trackRangeRepository.findById(id).map(trackRangeMapper::toDto);
    }

    /**
     * Delete the trackRange by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TrackRange : {}", id);
        trackRangeRepository.deleteById(id);
    }

    /**
     * 取得過濾後的 trackRange 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<TrackRangeDTO> findAll(Predicate predicate) {
        LOG.debug("Request to get TrackRange with conditions: {}", predicate);
        return this.findAll(predicate, trackRangeMapper::toDto);
    }

    /**
     * 取得過濾後的 trackRange 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param mapper 映射物件，不可為 null
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<TrackRangeDTO> findAll(Predicate predicate, Function<? super TrackRange, ? extends TrackRangeDTO> mapper) {
        LOG.debug("Request to get TrackRange with conditions: {}", predicate);
        Stream<TrackRange> stream = Objects.isNull(predicate)
            ? trackRangeRepository.findAll().stream()
            : StreamSupport.stream(trackRangeRepository.findAll(predicate).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }

    /**
     * 取得過濾後的 trackRange 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TrackRangeDTO> findAll(Predicate predicate, Pageable pageable) {
        LOG.debug("Request to get TrackRange with conditions: {}, page: {}", predicate, pageable);
        return this.findAll(predicate, pageable, trackRangeMapper::toDto);
    }

    /**
     * 取得過濾後的 trackRange 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param pageable 分頁與排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TrackRangeDTO> findAll(
        Predicate predicate,
        Pageable pageable,
        Function<? super TrackRange, ? extends TrackRangeDTO> mapper
    ) {
        LOG.debug("Request to get TrackRange with conditions: {}, page: {}", predicate, pageable);
        Page<TrackRange> page = Objects.isNull(predicate)
            ? trackRangeRepository.findAll(pageable)
            : trackRangeRepository.findAll(predicate, pageable);
        return page.map(mapper);
    }

    /**
     * 取得過濾後的 trackRange 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TrackRangeDTO> findAll(Predicate predicate, Sort sort) {
        LOG.debug("Request to get TrackRange with conditions: {}, sort: {}", predicate, sort);
        return this.findAll(predicate, sort, trackRangeMapper::toDto);
    }

    /**
     * 取得過濾後的 trackRange 資料.
     *
     * @param predicate 過濾條件，NULL 時，回傳整個資料表
     * @param sort 排序條件
     * @param mapper 映射物件，不可為 null
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TrackRangeDTO> findAll(Predicate predicate, Sort sort, Function<? super TrackRange, ? extends TrackRangeDTO> mapper) {
        LOG.debug("Request to get TrackRange with conditions: {}, sort: {}", predicate, sort);
        Stream<TrackRange> stream = Objects.isNull(predicate)
            ? trackRangeRepository.findAll(sort).stream()
            : StreamSupport.stream(trackRangeRepository.findAll(predicate, sort).spliterator(), false);
        return stream.map(mapper).collect(Collectors.toList());
    }
}
