package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WebhookDeliveryLog entity.
 */
@Repository
public interface WebhookDeliveryLogRepository
    extends
        JpaRepository<WebhookDeliveryLog, Long>,
        JpaSpecificationExecutor<WebhookDeliveryLog>,
        QuerydslPredicateExecutor<WebhookDeliveryLog> {
    default Optional<WebhookDeliveryLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WebhookDeliveryLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WebhookDeliveryLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select webhookDeliveryLog from WebhookDeliveryLog webhookDeliveryLog left join fetch webhookDeliveryLog.webhookEndpoint",
        countQuery = "select count(webhookDeliveryLog) from WebhookDeliveryLog webhookDeliveryLog"
    )
    Page<WebhookDeliveryLog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select webhookDeliveryLog from WebhookDeliveryLog webhookDeliveryLog left join fetch webhookDeliveryLog.webhookEndpoint")
    List<WebhookDeliveryLog> findAllWithToOneRelationships();

    @Query(
        "select webhookDeliveryLog from WebhookDeliveryLog webhookDeliveryLog left join fetch webhookDeliveryLog.webhookEndpoint where webhookDeliveryLog.id =:id"
    )
    Optional<WebhookDeliveryLog> findOneWithToOneRelationships(@Param("id") Long id);

    List<WebhookDeliveryLog> findByStatusAndNextAttemptAtLessThanEqual(DeliveryResult status, Instant threshold);

    Page<WebhookDeliveryLog> findByStatus(DeliveryResult status, Pageable pageable);
}
