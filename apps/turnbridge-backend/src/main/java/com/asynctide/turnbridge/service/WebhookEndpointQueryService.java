package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.repository.WebhookEndpointRepository;
import com.asynctide.turnbridge.service.criteria.WebhookEndpointCriteria;
import com.asynctide.turnbridge.service.dto.WebhookEndpointDTO;
import com.asynctide.turnbridge.service.mapper.WebhookEndpointMapper;
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
 * Service for executing complex queries for {@link WebhookEndpoint} entities in the database.
 * The main input is a {@link WebhookEndpointCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WebhookEndpointDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WebhookEndpointQueryService extends QueryService<WebhookEndpoint> {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookEndpointQueryService.class);

    private final WebhookEndpointRepository webhookEndpointRepository;

    private final WebhookEndpointMapper webhookEndpointMapper;

    public WebhookEndpointQueryService(WebhookEndpointRepository webhookEndpointRepository, WebhookEndpointMapper webhookEndpointMapper) {
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.webhookEndpointMapper = webhookEndpointMapper;
    }

    /**
     * Return a {@link Page} of {@link WebhookEndpointDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookEndpointDTO> findByCriteria(WebhookEndpointCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WebhookEndpoint> specification = createSpecification(criteria);
        return webhookEndpointRepository.findAll(specification, page).map(webhookEndpointMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WebhookEndpointCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WebhookEndpoint> specification = createSpecification(criteria);
        return webhookEndpointRepository.count(specification);
    }

    /**
     * Function to convert {@link WebhookEndpointCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WebhookEndpoint> createSpecification(WebhookEndpointCriteria criteria) {
        Specification<WebhookEndpoint> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), WebhookEndpoint_.id),
                buildStringSpecification(criteria.getName(), WebhookEndpoint_.name),
                buildStringSpecification(criteria.getTargetUrl(), WebhookEndpoint_.targetUrl),
                buildStringSpecification(criteria.getSecret(), WebhookEndpoint_.secret),
                buildStringSpecification(criteria.getEvents(), WebhookEndpoint_.events),
                buildSpecification(criteria.getStatus(), WebhookEndpoint_.status),
                buildSpecification(criteria.getTenantId(), root -> root.join(WebhookEndpoint_.tenant, JoinType.LEFT).get(Tenant_.id))
            );
        }
        return specification;
    }
}
