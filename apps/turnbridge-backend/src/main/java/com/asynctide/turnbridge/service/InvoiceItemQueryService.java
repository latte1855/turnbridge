package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.service.criteria.InvoiceItemCriteria;
import com.asynctide.turnbridge.service.dto.InvoiceItemDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceItemMapper;
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
 * Service for executing complex queries for {@link InvoiceItem} entities in the database.
 * The main input is a {@link InvoiceItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link InvoiceItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InvoiceItemQueryService extends QueryService<InvoiceItem> {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceItemQueryService.class);

    private final InvoiceItemRepository invoiceItemRepository;

    private final InvoiceItemMapper invoiceItemMapper;

    public InvoiceItemQueryService(InvoiceItemRepository invoiceItemRepository, InvoiceItemMapper invoiceItemMapper) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.invoiceItemMapper = invoiceItemMapper;
    }

    /**
     * Return a {@link Page} of {@link InvoiceItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InvoiceItemDTO> findByCriteria(InvoiceItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InvoiceItem> specification = createSpecification(criteria);
        return invoiceItemRepository.findAll(specification, page).map(invoiceItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InvoiceItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<InvoiceItem> specification = createSpecification(criteria);
        return invoiceItemRepository.count(specification);
    }

    /**
     * Function to convert {@link InvoiceItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InvoiceItem> createSpecification(InvoiceItemCriteria criteria) {
        Specification<InvoiceItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), InvoiceItem_.id),
                buildStringSpecification(criteria.getDescription(), InvoiceItem_.description),
                buildRangeSpecification(criteria.getQuantity(), InvoiceItem_.quantity),
                buildRangeSpecification(criteria.getUnitPrice(), InvoiceItem_.unitPrice),
                buildRangeSpecification(criteria.getAmount(), InvoiceItem_.amount),
                buildRangeSpecification(criteria.getSequence(), InvoiceItem_.sequence),
                buildSpecification(criteria.getInvoiceId(), root -> root.join(InvoiceItem_.invoice, JoinType.LEFT).get(Invoice_.id))
            );
        }
        return specification;
    }
}
