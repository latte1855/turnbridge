package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.service.criteria.ImportFileItemCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileItemDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileItemMapper;
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
 * Service for executing complex queries for {@link ImportFileItem} entities in the database.
 * The main input is a {@link ImportFileItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ImportFileItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImportFileItemQueryService extends QueryService<ImportFileItem> {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileItemQueryService.class);

    private final ImportFileItemRepository importFileItemRepository;

    private final ImportFileItemMapper importFileItemMapper;

    public ImportFileItemQueryService(ImportFileItemRepository importFileItemRepository, ImportFileItemMapper importFileItemMapper) {
        this.importFileItemRepository = importFileItemRepository;
        this.importFileItemMapper = importFileItemMapper;
    }

    /**
     * Return a {@link Page} of {@link ImportFileItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileItemDTO> findByCriteria(ImportFileItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ImportFileItem> specification = createSpecification(criteria);
        return importFileItemRepository.findAll(specification, page).map(importFileItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ImportFileItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ImportFileItem> specification = createSpecification(criteria);
        return importFileItemRepository.count(specification);
    }

    /**
     * Function to convert {@link ImportFileItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ImportFileItem> createSpecification(ImportFileItemCriteria criteria) {
        Specification<ImportFileItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ImportFileItem_.id),
                buildRangeSpecification(criteria.getLineIndex(), ImportFileItem_.lineIndex),
                buildStringSpecification(criteria.getRawHash(), ImportFileItem_.rawHash),
                buildStringSpecification(criteria.getSourceFamily(), ImportFileItem_.sourceFamily),
                buildStringSpecification(criteria.getNormalizedFamily(), ImportFileItem_.normalizedFamily),
                buildSpecification(criteria.getStatus(), ImportFileItem_.status),
                buildStringSpecification(criteria.getErrorCode(), ImportFileItem_.errorCode),
                buildStringSpecification(criteria.getErrorMessage(), ImportFileItem_.errorMessage),
                buildSpecification(criteria.getImportFileId(), root ->
                    root.join(ImportFileItem_.importFile, JoinType.LEFT).get(ImportFile_.id)
                ),
                buildSpecification(criteria.getInvoiceId(), root -> root.join(ImportFileItem_.invoice, JoinType.LEFT).get(Invoice_.id))
            );
        }
        return specification;
    }
}
