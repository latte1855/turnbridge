package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.service.criteria.ImportFileItemErrorCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileItemErrorDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileItemErrorMapper;
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
 * Service for executing complex queries for {@link ImportFileItemError} entities in the database.
 * The main input is a {@link ImportFileItemErrorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ImportFileItemErrorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImportFileItemErrorQueryService extends QueryService<ImportFileItemError> {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileItemErrorQueryService.class);

    private final ImportFileItemErrorRepository importFileItemErrorRepository;

    private final ImportFileItemErrorMapper importFileItemErrorMapper;

    public ImportFileItemErrorQueryService(
        ImportFileItemErrorRepository importFileItemErrorRepository,
        ImportFileItemErrorMapper importFileItemErrorMapper
    ) {
        this.importFileItemErrorRepository = importFileItemErrorRepository;
        this.importFileItemErrorMapper = importFileItemErrorMapper;
    }

    /**
     * Return a {@link Page} of {@link ImportFileItemErrorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileItemErrorDTO> findByCriteria(ImportFileItemErrorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ImportFileItemError> specification = createSpecification(criteria);
        return importFileItemErrorRepository.findAll(specification, page).map(importFileItemErrorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ImportFileItemErrorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ImportFileItemError> specification = createSpecification(criteria);
        return importFileItemErrorRepository.count(specification);
    }

    /**
     * Function to convert {@link ImportFileItemErrorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ImportFileItemError> createSpecification(ImportFileItemErrorCriteria criteria) {
        Specification<ImportFileItemError> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ImportFileItemError_.id),
                buildRangeSpecification(criteria.getColumnIndex(), ImportFileItemError_.columnIndex),
                buildStringSpecification(criteria.getFieldName(), ImportFileItemError_.fieldName),
                buildStringSpecification(criteria.getErrorCode(), ImportFileItemError_.errorCode),
                buildStringSpecification(criteria.getMessage(), ImportFileItemError_.message),
                buildStringSpecification(criteria.getSeverity(), ImportFileItemError_.severity),
                buildRangeSpecification(criteria.getOccurredAt(), ImportFileItemError_.occurredAt),
                buildSpecification(criteria.getImportFileItemId(), root ->
                    root.join(ImportFileItemError_.importFileItem, JoinType.LEFT).get(ImportFileItem_.id)
                )
            );
        }
        return specification;
    }
}
