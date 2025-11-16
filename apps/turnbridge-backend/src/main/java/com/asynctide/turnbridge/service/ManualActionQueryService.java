package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.ManualAction;
import com.asynctide.turnbridge.repository.ManualActionRepository;
import com.asynctide.turnbridge.service.criteria.ManualActionCriteria;
import com.asynctide.turnbridge.service.dto.ManualActionDTO;
import com.asynctide.turnbridge.service.mapper.ManualActionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ManualAction} entities in the database.
 * The main input is a {@link ManualActionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ManualActionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ManualActionQueryService extends QueryService<ManualAction> {

    private static final Logger LOG = LoggerFactory.getLogger(ManualActionQueryService.class);

    private final ManualActionRepository manualActionRepository;

    private final ManualActionMapper manualActionMapper;

    public ManualActionQueryService(ManualActionRepository manualActionRepository, ManualActionMapper manualActionMapper) {
        this.manualActionRepository = manualActionRepository;
        this.manualActionMapper = manualActionMapper;
    }

    /**
     * Return a {@link List} of {@link ManualActionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ManualActionDTO> findByCriteria(ManualActionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ManualAction> specification = createSpecification(criteria);
        return manualActionMapper.toDto(manualActionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ManualActionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ManualAction> specification = createSpecification(criteria);
        return manualActionRepository.count(specification);
    }

    /**
     * Function to convert {@link ManualActionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ManualAction> createSpecification(ManualActionCriteria criteria) {
        Specification<ManualAction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ManualAction_.id),
                buildSpecification(criteria.getActionType(), ManualAction_.actionType),
                buildStringSpecification(criteria.getReason(), ManualAction_.reason),
                buildSpecification(criteria.getStatus(), ManualAction_.status),
                buildStringSpecification(criteria.getRequestedBy(), ManualAction_.requestedBy),
                buildRangeSpecification(criteria.getRequestedAt(), ManualAction_.requestedAt),
                buildStringSpecification(criteria.getApprovedBy(), ManualAction_.approvedBy),
                buildRangeSpecification(criteria.getApprovedAt(), ManualAction_.approvedAt),
                buildSpecification(criteria.getTenantId(), root -> root.join(ManualAction_.tenant, JoinType.LEFT).get(Tenant_.id)),
                buildSpecification(criteria.getInvoiceId(), root -> root.join(ManualAction_.invoice, JoinType.LEFT).get(Invoice_.id)),
                buildSpecification(criteria.getImportFileId(), root ->
                    root.join(ManualAction_.importFile, JoinType.LEFT).get(ImportFile_.id)
                )
            );
        }
        return specification;
    }
}
