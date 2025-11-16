package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.TurnkeyMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TurnkeyMessage entity.
 */
@Repository
public interface TurnkeyMessageRepository
    extends JpaRepository<TurnkeyMessage, Long>, JpaSpecificationExecutor<TurnkeyMessage>, QuerydslPredicateExecutor<TurnkeyMessage> {
    default Optional<TurnkeyMessage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TurnkeyMessage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TurnkeyMessage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select turnkeyMessage from TurnkeyMessage turnkeyMessage left join fetch turnkeyMessage.invoice",
        countQuery = "select count(turnkeyMessage) from TurnkeyMessage turnkeyMessage"
    )
    Page<TurnkeyMessage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select turnkeyMessage from TurnkeyMessage turnkeyMessage left join fetch turnkeyMessage.invoice")
    List<TurnkeyMessage> findAllWithToOneRelationships();

    @Query("select turnkeyMessage from TurnkeyMessage turnkeyMessage left join fetch turnkeyMessage.invoice where turnkeyMessage.id =:id")
    Optional<TurnkeyMessage> findOneWithToOneRelationships(@Param("id") Long id);
}
