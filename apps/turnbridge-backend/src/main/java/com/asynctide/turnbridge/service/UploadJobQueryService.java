package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.criteria.UploadJobCriteria;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
import com.asynctide.turnbridge.service.mapper.UploadJobMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UploadJob} entities in the database.
 * The main input is a {@link UploadJobCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link UploadJobDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UploadJobQueryService extends QueryService<UploadJob> {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJobQueryService.class);

    private final UploadJobRepository uploadJobRepository;

    private final UploadJobMapper uploadJobMapper;

    public UploadJobQueryService(UploadJobRepository uploadJobRepository, UploadJobMapper uploadJobMapper) {
        this.uploadJobRepository = uploadJobRepository;
        this.uploadJobMapper = uploadJobMapper;
    }

    /**
     * Return a {@link Page} of {@link UploadJobDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadJobDTO> findByCriteria(UploadJobCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UploadJob> specification = createSpecification(criteria);
        return uploadJobRepository.findAll(specification, page).map(uploadJobMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UploadJobCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UploadJob> specification = createSpecification(criteria);
        return uploadJobRepository.count(specification);
    }

    /**
     * Function to convert {@link UploadJobCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UploadJob> createSpecification(UploadJobCriteria criteria) {
        Specification<UploadJob> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), UploadJob_.id),
                buildStringSpecification(criteria.getJobId(), UploadJob_.jobId),
                buildStringSpecification(criteria.getSellerId(), UploadJob_.sellerId),
                buildStringSpecification(criteria.getSellerName(), UploadJob_.sellerName),
                buildStringSpecification(criteria.getPeriod(), UploadJob_.period),
                buildStringSpecification(criteria.getProfile(), UploadJob_.profile),
                buildStringSpecification(criteria.getSourceFilename(), UploadJob_.sourceFilename),
                buildStringSpecification(criteria.getSourceMediaType(), UploadJob_.sourceMediaType),
                buildSpecification(criteria.getStatus(), UploadJob_.status),
                buildRangeSpecification(criteria.getTotal(), UploadJob_.total),
                buildRangeSpecification(criteria.getAccepted(), UploadJob_.accepted),
                buildRangeSpecification(criteria.getFailed(), UploadJob_.failed),
                buildRangeSpecification(criteria.getSent(), UploadJob_.sent),
                buildStringSpecification(criteria.getRemark(), UploadJob_.remark),
                buildSpecification(criteria.getItemsId(), root -> root.join(UploadJob_.items, JoinType.LEFT).get(UploadJobItem_.id)),
                buildSpecification(criteria.getOriginalFileId(), root ->
                    root.join(UploadJob_.originalFile, JoinType.LEFT).get(StoredObject_.id)
                ),
                buildSpecification(criteria.getResultFileId(), root -> root.join(UploadJob_.resultFile, JoinType.LEFT).get(StoredObject_.id)
                )
            );
        }
        return specification;
    }
}
