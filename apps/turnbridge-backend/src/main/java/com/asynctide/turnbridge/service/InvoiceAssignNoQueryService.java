package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.InvoiceAssignNo;
import com.asynctide.turnbridge.repository.InvoiceAssignNoRepository;
import com.asynctide.turnbridge.service.criteria.InvoiceAssignNoCriteria;
import com.asynctide.turnbridge.service.dto.InvoiceAssignNoDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceAssignNoMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InvoiceAssignNo} entities in the database.
 * The main input is a {@link InvoiceAssignNoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InvoiceAssignNoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InvoiceAssignNoQueryService extends QueryService<InvoiceAssignNo> {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceAssignNoQueryService.class);

    private final InvoiceAssignNoRepository invoiceAssignNoRepository;

    private final InvoiceAssignNoMapper invoiceAssignNoMapper;

    public InvoiceAssignNoQueryService(InvoiceAssignNoRepository invoiceAssignNoRepository, InvoiceAssignNoMapper invoiceAssignNoMapper) {
        this.invoiceAssignNoRepository = invoiceAssignNoRepository;
        this.invoiceAssignNoMapper = invoiceAssignNoMapper;
    }

    /**
     * Return a {@link List} of {@link InvoiceAssignNoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceAssignNoDTO> findByCriteria(InvoiceAssignNoCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<InvoiceAssignNo> specification = createSpecification(criteria);
        return invoiceAssignNoMapper.toDto(invoiceAssignNoRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InvoiceAssignNoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<InvoiceAssignNo> specification = createSpecification(criteria);
        return invoiceAssignNoRepository.count(specification);
    }

    /**
     * Function to convert {@link InvoiceAssignNoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InvoiceAssignNo> createSpecification(InvoiceAssignNoCriteria criteria) {
        Specification<InvoiceAssignNo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), InvoiceAssignNo_.id),
                buildStringSpecification(criteria.getTrack(), InvoiceAssignNo_.track),
                buildStringSpecification(criteria.getPeriod(), InvoiceAssignNo_.period),
                buildStringSpecification(criteria.getFromNo(), InvoiceAssignNo_.fromNo),
                buildStringSpecification(criteria.getToNo(), InvoiceAssignNo_.toNo),
                buildRangeSpecification(criteria.getUsedCount(), InvoiceAssignNo_.usedCount),
                buildRangeSpecification(criteria.getRollSize(), InvoiceAssignNo_.rollSize),
                buildStringSpecification(criteria.getStatus(), InvoiceAssignNo_.status),
                buildSpecification(criteria.getTenantId(), root -> root.join(InvoiceAssignNo_.tenant, JoinType.LEFT).get(Tenant_.id))
            );
        }
        return specification;
    }
}
