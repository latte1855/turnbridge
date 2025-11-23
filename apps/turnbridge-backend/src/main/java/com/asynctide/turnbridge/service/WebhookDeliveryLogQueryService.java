package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import com.asynctide.turnbridge.service.criteria.WebhookDeliveryLogCriteria;
import com.asynctide.turnbridge.service.dto.WebhookDeliveryLogDTO;
import com.asynctide.turnbridge.service.mapper.WebhookDeliveryLogMapper;
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
 * Service for executing complex queries for {@link WebhookDeliveryLog} entities in the database.
 * The main input is a {@link WebhookDeliveryLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WebhookDeliveryLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WebhookDeliveryLogQueryService extends QueryService<WebhookDeliveryLog> {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDeliveryLogQueryService.class);

    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;

    private final WebhookDeliveryLogMapper webhookDeliveryLogMapper;

    public WebhookDeliveryLogQueryService(
        WebhookDeliveryLogRepository webhookDeliveryLogRepository,
        WebhookDeliveryLogMapper webhookDeliveryLogMapper
    ) {
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.webhookDeliveryLogMapper = webhookDeliveryLogMapper;
    }

    /**
     * Return a {@link Page} of {@link WebhookDeliveryLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookDeliveryLogDTO> findByCriteria(WebhookDeliveryLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WebhookDeliveryLog> specification = createSpecification(criteria);
        return webhookDeliveryLogRepository.findAll(specification, page).map(webhookDeliveryLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WebhookDeliveryLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WebhookDeliveryLog> specification = createSpecification(criteria);
        return webhookDeliveryLogRepository.count(specification);
    }

    /**
     * Function to convert {@link WebhookDeliveryLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WebhookDeliveryLog> createSpecification(WebhookDeliveryLogCriteria criteria) {
        Specification<WebhookDeliveryLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), WebhookDeliveryLog_.id),
                buildStringSpecification(criteria.getDeliveryId(), WebhookDeliveryLog_.deliveryId),
                buildStringSpecification(criteria.getEvent(), WebhookDeliveryLog_.event),
                buildSpecification(criteria.getStatus(), WebhookDeliveryLog_.status),
                buildRangeSpecification(criteria.getHttpStatus(), WebhookDeliveryLog_.httpStatus),
                buildRangeSpecification(criteria.getAttempts(), WebhookDeliveryLog_.attempts),
                buildStringSpecification(criteria.getLastError(), WebhookDeliveryLog_.lastError),
                buildRangeSpecification(criteria.getDeliveredAt(), WebhookDeliveryLog_.deliveredAt),
                buildRangeSpecification(criteria.getNextAttemptAt(), WebhookDeliveryLog_.nextAttemptAt),
                buildRangeSpecification(criteria.getLockedAt(), WebhookDeliveryLog_.lockedAt),
                buildStringSpecification(criteria.getDlqReason(), WebhookDeliveryLog_.dlqReason),
                buildSpecification(criteria.getWebhookEndpointId(), root ->
                    root.join(WebhookDeliveryLog_.webhookEndpoint, JoinType.LEFT).get(WebhookEndpoint_.id)
                )
            );
        }
        return specification;
    }
}
