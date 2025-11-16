package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.ManualAction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ManualAction entity.
 */
@Repository
public interface ManualActionRepository
    extends JpaRepository<ManualAction, Long>, JpaSpecificationExecutor<ManualAction>, QuerydslPredicateExecutor<ManualAction> {
    default Optional<ManualAction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ManualAction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ManualAction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select manualAction from ManualAction manualAction left join fetch manualAction.tenant left join fetch manualAction.invoice left join fetch manualAction.importFile",
        countQuery = "select count(manualAction) from ManualAction manualAction"
    )
    Page<ManualAction> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select manualAction from ManualAction manualAction left join fetch manualAction.tenant left join fetch manualAction.invoice left join fetch manualAction.importFile"
    )
    List<ManualAction> findAllWithToOneRelationships();

    @Query(
        "select manualAction from ManualAction manualAction left join fetch manualAction.tenant left join fetch manualAction.invoice left join fetch manualAction.importFile where manualAction.id =:id"
    )
    Optional<ManualAction> findOneWithToOneRelationships(@Param("id") Long id);
}
