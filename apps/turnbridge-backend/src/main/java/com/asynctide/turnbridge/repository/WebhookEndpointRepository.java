package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.domain.enumeration.WebhookStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WebhookEndpoint entity.
 */
@Repository
public interface WebhookEndpointRepository
    extends JpaRepository<WebhookEndpoint, Long>, JpaSpecificationExecutor<WebhookEndpoint>, QuerydslPredicateExecutor<WebhookEndpoint> {

    List<WebhookEndpoint> findByTenantIdAndStatus(Long tenantId, WebhookStatus status);

    default Optional<WebhookEndpoint> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WebhookEndpoint> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WebhookEndpoint> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select webhookEndpoint from WebhookEndpoint webhookEndpoint left join fetch webhookEndpoint.tenant",
        countQuery = "select count(webhookEndpoint) from WebhookEndpoint webhookEndpoint"
    )
    Page<WebhookEndpoint> findAllWithToOneRelationships(Pageable pageable);

    @Query("select webhookEndpoint from WebhookEndpoint webhookEndpoint left join fetch webhookEndpoint.tenant")
    List<WebhookEndpoint> findAllWithToOneRelationships();

    @Query(
        "select webhookEndpoint from WebhookEndpoint webhookEndpoint left join fetch webhookEndpoint.tenant where webhookEndpoint.id =:id"
    )
    Optional<WebhookEndpoint> findOneWithToOneRelationships(@Param("id") Long id);
}
