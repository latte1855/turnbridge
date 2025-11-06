package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.service.criteria.StoredObjectCriteria;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.service.mapper.StoredObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StoredObject} entities in the database.
 * The main input is a {@link StoredObjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StoredObjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StoredObjectQueryService extends QueryService<StoredObject> {

    private static final Logger LOG = LoggerFactory.getLogger(StoredObjectQueryService.class);

    private final StoredObjectRepository storedObjectRepository;

    private final StoredObjectMapper storedObjectMapper;

    public StoredObjectQueryService(StoredObjectRepository storedObjectRepository, StoredObjectMapper storedObjectMapper) {
        this.storedObjectRepository = storedObjectRepository;
        this.storedObjectMapper = storedObjectMapper;
    }

    /**
     * Return a {@link Page} of {@link StoredObjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StoredObjectDTO> findByCriteria(StoredObjectCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StoredObject> specification = createSpecification(criteria);
        return storedObjectRepository.findAll(specification, page).map(storedObjectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StoredObjectCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StoredObject> specification = createSpecification(criteria);
        return storedObjectRepository.count(specification);
    }

    /**
     * Function to convert {@link StoredObjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StoredObject> createSpecification(StoredObjectCriteria criteria) {
        Specification<StoredObject> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), StoredObject_.id),
                buildStringSpecification(criteria.getBucket(), StoredObject_.bucket),
                buildStringSpecification(criteria.getObjectKey(), StoredObject_.objectKey),
                buildStringSpecification(criteria.getMediaType(), StoredObject_.mediaType),
                buildRangeSpecification(criteria.getContentLength(), StoredObject_.contentLength),
                buildStringSpecification(criteria.getSha256(), StoredObject_.sha256),
                buildSpecification(criteria.getPurpose(), StoredObject_.purpose),
                buildStringSpecification(criteria.getFilename(), StoredObject_.filename),
                buildStringSpecification(criteria.getStorageClass(), StoredObject_.storageClass),
                buildStringSpecification(criteria.getEncryption(), StoredObject_.encryption)
            );
        }
        return specification;
    }
}
