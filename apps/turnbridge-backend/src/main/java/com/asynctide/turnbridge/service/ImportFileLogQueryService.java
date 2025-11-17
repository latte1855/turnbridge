package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.service.criteria.ImportFileLogCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileLogMapper;
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
 * Service for executing complex queries for {@link ImportFileLog} entities in the database.
 * The main input is a {@link ImportFileLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ImportFileLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImportFileLogQueryService extends QueryService<ImportFileLog> {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileLogQueryService.class);

    private final ImportFileLogRepository importFileLogRepository;

    private final ImportFileLogMapper importFileLogMapper;

    public ImportFileLogQueryService(ImportFileLogRepository importFileLogRepository, ImportFileLogMapper importFileLogMapper) {
        this.importFileLogRepository = importFileLogRepository;
        this.importFileLogMapper = importFileLogMapper;
    }

    /**
     * Return a {@link Page} of {@link ImportFileLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileLogDTO> findByCriteria(ImportFileLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ImportFileLog> specification = createSpecification(criteria);
        return importFileLogRepository.findAll(specification, page).map(importFileLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ImportFileLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ImportFileLog> specification = createSpecification(criteria);
        return importFileLogRepository.count(specification);
    }

    /**
     * Function to convert {@link ImportFileLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ImportFileLog> createSpecification(ImportFileLogCriteria criteria) {
        Specification<ImportFileLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ImportFileLog_.id),
                buildStringSpecification(criteria.getEventCode(), ImportFileLog_.eventCode),
                buildStringSpecification(criteria.getLevel(), ImportFileLog_.level),
                buildStringSpecification(criteria.getMessage(), ImportFileLog_.message),
                buildRangeSpecification(criteria.getOccurredAt(), ImportFileLog_.occurredAt),
                buildSpecification(criteria.getImportFileId(), root ->
                    root.join(ImportFileLog_.importFile, JoinType.LEFT).get(ImportFile_.id)
                )
            );
        }
        return specification;
    }
}
