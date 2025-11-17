package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.ImportFileItemError;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportFileItemError entity.
 */
@Repository
public interface ImportFileItemErrorRepository
    extends
        JpaRepository<ImportFileItemError, Long>,
        JpaSpecificationExecutor<ImportFileItemError>,
        QuerydslPredicateExecutor<ImportFileItemError> {
    default Optional<ImportFileItemError> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ImportFileItemError> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ImportFileItemError> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select importFileItemError from ImportFileItemError importFileItemError left join fetch importFileItemError.importFileItem",
        countQuery = "select count(importFileItemError) from ImportFileItemError importFileItemError"
    )
    Page<ImportFileItemError> findAllWithToOneRelationships(Pageable pageable);

    @Query("select importFileItemError from ImportFileItemError importFileItemError left join fetch importFileItemError.importFileItem")
    List<ImportFileItemError> findAllWithToOneRelationships();

    @Query(
        "select importFileItemError from ImportFileItemError importFileItemError left join fetch importFileItemError.importFileItem where importFileItemError.id =:id"
    )
    Optional<ImportFileItemError> findOneWithToOneRelationships(@Param("id") Long id);

    List<ImportFileItemError> findByImportFileItemIdIn(List<Long> importFileItemIds);
}
