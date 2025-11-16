package com.asynctide.turnbridge.service;

import com.asynctide.turnbridge.domain.*; // for static metamodels
import com.asynctide.turnbridge.domain.TurnkeyMessage;
import com.asynctide.turnbridge.repository.TurnkeyMessageRepository;
import com.asynctide.turnbridge.service.criteria.TurnkeyMessageCriteria;
import com.asynctide.turnbridge.service.dto.TurnkeyMessageDTO;
import com.asynctide.turnbridge.service.mapper.TurnkeyMessageMapper;
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
 * Service for executing complex queries for {@link TurnkeyMessage} entities in the database.
 * The main input is a {@link TurnkeyMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TurnkeyMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TurnkeyMessageQueryService extends QueryService<TurnkeyMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyMessageQueryService.class);

    private final TurnkeyMessageRepository turnkeyMessageRepository;

    private final TurnkeyMessageMapper turnkeyMessageMapper;

    public TurnkeyMessageQueryService(TurnkeyMessageRepository turnkeyMessageRepository, TurnkeyMessageMapper turnkeyMessageMapper) {
        this.turnkeyMessageRepository = turnkeyMessageRepository;
        this.turnkeyMessageMapper = turnkeyMessageMapper;
    }

    /**
     * Return a {@link Page} of {@link TurnkeyMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TurnkeyMessageDTO> findByCriteria(TurnkeyMessageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TurnkeyMessage> specification = createSpecification(criteria);
        return turnkeyMessageRepository.findAll(specification, page).map(turnkeyMessageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TurnkeyMessageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TurnkeyMessage> specification = createSpecification(criteria);
        return turnkeyMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link TurnkeyMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TurnkeyMessage> createSpecification(TurnkeyMessageCriteria criteria) {
        Specification<TurnkeyMessage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TurnkeyMessage_.id),
                buildStringSpecification(criteria.getMessageId(), TurnkeyMessage_.messageId),
                buildSpecification(criteria.getMessageFamily(), TurnkeyMessage_.messageFamily),
                buildStringSpecification(criteria.getType(), TurnkeyMessage_.type),
                buildStringSpecification(criteria.getCode(), TurnkeyMessage_.code),
                buildStringSpecification(criteria.getPayloadPath(), TurnkeyMessage_.payloadPath),
                buildRangeSpecification(criteria.getReceivedAt(), TurnkeyMessage_.receivedAt),
                buildSpecification(criteria.getInvoiceId(), root -> root.join(TurnkeyMessage_.invoice, JoinType.LEFT).get(Invoice_.id))
            );
        }
        return specification;
    }
}
