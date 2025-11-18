package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.ImportFileItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportFileItem entity.
 */
@Repository
public interface ImportFileItemRepository
    extends JpaRepository<ImportFileItem, Long>, JpaSpecificationExecutor<ImportFileItem>, QuerydslPredicateExecutor<ImportFileItem> {

    List<ImportFileItem> findByImportFileIdOrderByLineIndexAsc(Long importFileId);
    default Optional<ImportFileItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ImportFileItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ImportFileItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select importFileItem from ImportFileItem importFileItem left join fetch importFileItem.importFile left join fetch importFileItem.invoice",
        countQuery = "select count(importFileItem) from ImportFileItem importFileItem"
    )
    Page<ImportFileItem> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select importFileItem from ImportFileItem importFileItem left join fetch importFileItem.importFile left join fetch importFileItem.invoice"
    )
    List<ImportFileItem> findAllWithToOneRelationships();

    @Query(
        "select importFileItem from ImportFileItem importFileItem left join fetch importFileItem.importFile left join fetch importFileItem.invoice where importFileItem.id =:id"
    )
    Optional<ImportFileItem> findOneWithToOneRelationships(@Param("id") Long id);
}
