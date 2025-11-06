package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.service.criteria.UploadJobItemCriteria;
import com.asynctide.turnbridge.service.dto.UploadJobItemDTO;
import com.asynctide.turnbridge.service.mapper.UploadJobItemMapper;
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
 * Service for executing complex queries for {@link UploadJobItem} entities in the database.
 * The main input is a {@link UploadJobItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link UploadJobItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UploadJobItemQueryService extends QueryService<UploadJobItem> {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJobItemQueryService.class);

    private final UploadJobItemRepository uploadJobItemRepository;

    private final UploadJobItemMapper uploadJobItemMapper;

    public UploadJobItemQueryService(UploadJobItemRepository uploadJobItemRepository, UploadJobItemMapper uploadJobItemMapper) {
        this.uploadJobItemRepository = uploadJobItemRepository;
        this.uploadJobItemMapper = uploadJobItemMapper;
    }

    /**
     * Return a {@link Page} of {@link UploadJobItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadJobItemDTO> findByCriteria(UploadJobItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UploadJobItem> specification = createSpecification(criteria);
        return uploadJobItemRepository.findAll(specification, page).map(uploadJobItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UploadJobItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UploadJobItem> specification = createSpecification(criteria);
        return uploadJobItemRepository.count(specification);
    }

    /**
     * Function to convert {@link UploadJobItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UploadJobItem> createSpecification(UploadJobItemCriteria criteria) {
        Specification<UploadJobItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), UploadJobItem_.id),
                buildRangeSpecification(criteria.getLineNo(), UploadJobItem_.lineNo),
                buildStringSpecification(criteria.getTraceId(), UploadJobItem_.traceId),
                buildSpecification(criteria.getStatus(), UploadJobItem_.status),
                buildStringSpecification(criteria.getResultCode(), UploadJobItem_.resultCode),
                buildStringSpecification(criteria.getResultMsg(), UploadJobItem_.resultMsg),
                buildStringSpecification(criteria.getBuyerId(), UploadJobItem_.buyerId),
                buildStringSpecification(criteria.getBuyerName(), UploadJobItem_.buyerName),
                buildStringSpecification(criteria.getCurrency(), UploadJobItem_.currency),
                buildRangeSpecification(criteria.getAmountExcl(), UploadJobItem_.amountExcl),
                buildRangeSpecification(criteria.getTaxAmount(), UploadJobItem_.taxAmount),
                buildRangeSpecification(criteria.getAmountIncl(), UploadJobItem_.amountIncl),
                buildSpecification(criteria.getTaxType(), UploadJobItem_.taxType),
                buildRangeSpecification(criteria.getInvoiceDate(), UploadJobItem_.invoiceDate),
                buildStringSpecification(criteria.getInvoiceNo(), UploadJobItem_.invoiceNo),
                buildStringSpecification(criteria.getAssignedPrefix(), UploadJobItem_.assignedPrefix),
                buildStringSpecification(criteria.getRawHash(), UploadJobItem_.rawHash),
                buildStringSpecification(criteria.getProfileDetected(), UploadJobItem_.profileDetected),
                buildSpecification(criteria.getJobId(), root -> root.join(UploadJobItem_.job, JoinType.LEFT).get(UploadJob_.id))
            );
        }
        return specification;
    }
}
