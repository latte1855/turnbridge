package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.service.criteria.ImportFileCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileMapper;
import com.asynctide.turnbridge.security.AuthoritiesConstants;
import com.asynctide.turnbridge.security.SecurityUtils;
import com.asynctide.turnbridge.tenant.TenantContextHolder;
import tech.jhipster.service.filter.LongFilter;
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
 * Service for executing complex queries for {@link ImportFile} entities in the database.
 * The main input is a {@link ImportFileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ImportFileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImportFileQueryService extends QueryService<ImportFile> {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileQueryService.class);

    private final ImportFileRepository importFileRepository;

    private final ImportFileMapper importFileMapper;

    public ImportFileQueryService(ImportFileRepository importFileRepository, ImportFileMapper importFileMapper) {
        this.importFileRepository = importFileRepository;
        this.importFileMapper = importFileMapper;
    }

    /**
     * Return a {@link Page} of {@link ImportFileDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportFileDTO> findByCriteria(ImportFileCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        ImportFileCriteria resolvedCriteria = applyTenantScope(criteria);
        final Specification<ImportFile> specification = createSpecification(resolvedCriteria);
        return importFileRepository.findAll(specification, page).map(importFileMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ImportFileCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        ImportFileCriteria resolvedCriteria = applyTenantScope(criteria);
        final Specification<ImportFile> specification = createSpecification(resolvedCriteria);
        return importFileRepository.count(specification);
    }

    /**
     * Function to convert {@link ImportFileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ImportFile> createSpecification(ImportFileCriteria criteria) {
        Specification<ImportFile> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ImportFile_.id),
                buildSpecification(criteria.getImportType(), ImportFile_.importType),
                buildStringSpecification(criteria.getOriginalFilename(), ImportFile_.originalFilename),
                buildStringSpecification(criteria.getSha256(), ImportFile_.sha256),
                buildRangeSpecification(criteria.getTotalCount(), ImportFile_.totalCount),
                buildRangeSpecification(criteria.getSuccessCount(), ImportFile_.successCount),
                buildRangeSpecification(criteria.getErrorCount(), ImportFile_.errorCount),
                buildSpecification(criteria.getStatus(), ImportFile_.status),
                buildStringSpecification(criteria.getLegacyType(), ImportFile_.legacyType),
                buildSpecification(criteria.getTenantId(), root -> root.join(ImportFile_.tenant, JoinType.LEFT).get(Tenant_.id))
            );
        }
        return specification;
    }

    private ImportFileCriteria applyTenantScope(ImportFileCriteria criteria) {
        ImportFileCriteria resolved = criteria != null ? criteria : new ImportFileCriteria();
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);
        TenantContextHolder
            .get()
            .ifPresent(ctx -> {
                if (!isAdmin || ctx.hasTenant()) {
                    LongFilter filter = new LongFilter();
                    filter.setEquals(ctx.tenantId());
                    resolved.setTenantId(filter);
                }
            });
        return resolved;
    }
}
