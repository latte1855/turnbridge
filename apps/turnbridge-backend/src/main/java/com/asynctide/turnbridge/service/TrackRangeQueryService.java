package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.TrackRange;
import com.asynctide.turnbridge.repository.TrackRangeRepository;
import com.asynctide.turnbridge.service.criteria.TrackRangeCriteria;
import com.asynctide.turnbridge.service.dto.TrackRangeDTO;
import com.asynctide.turnbridge.service.mapper.TrackRangeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TrackRange} entities in the database.
 * The main input is a {@link TrackRangeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TrackRangeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TrackRangeQueryService extends QueryService<TrackRange> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackRangeQueryService.class);

    private final TrackRangeRepository trackRangeRepository;

    private final TrackRangeMapper trackRangeMapper;

    public TrackRangeQueryService(TrackRangeRepository trackRangeRepository, TrackRangeMapper trackRangeMapper) {
        this.trackRangeRepository = trackRangeRepository;
        this.trackRangeMapper = trackRangeMapper;
    }

    /**
     * Return a {@link Page} of {@link TrackRangeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TrackRangeDTO> findByCriteria(TrackRangeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TrackRange> specification = createSpecification(criteria);
        return trackRangeRepository.findAll(specification, page).map(trackRangeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TrackRangeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TrackRange> specification = createSpecification(criteria);
        return trackRangeRepository.count(specification);
    }

    /**
     * Function to convert {@link TrackRangeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TrackRange> createSpecification(TrackRangeCriteria criteria) {
        Specification<TrackRange> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TrackRange_.id),
                buildStringSpecification(criteria.getSellerId(), TrackRange_.sellerId),
                buildStringSpecification(criteria.getPeriod(), TrackRange_.period),
                buildStringSpecification(criteria.getPrefix(), TrackRange_.prefix),
                buildRangeSpecification(criteria.getStartNo(), TrackRange_.startNo),
                buildRangeSpecification(criteria.getEndNo(), TrackRange_.endNo),
                buildRangeSpecification(criteria.getCurrentNo(), TrackRange_.currentNo),
                buildSpecification(criteria.getStatus(), TrackRange_.status),
                buildRangeSpecification(criteria.getVersion(), TrackRange_.version),
                buildStringSpecification(criteria.getLockOwner(), TrackRange_.lockOwner),
                buildRangeSpecification(criteria.getLockAt(), TrackRange_.lockAt)
            );
        }
        return specification;
    }
}
