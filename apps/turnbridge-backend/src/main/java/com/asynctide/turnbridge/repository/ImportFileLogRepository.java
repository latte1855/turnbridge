package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.ImportFileLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportFileLog entity.
 */
@Repository
public interface ImportFileLogRepository
    extends JpaRepository<ImportFileLog, Long>, JpaSpecificationExecutor<ImportFileLog>, QuerydslPredicateExecutor<ImportFileLog> {
    default Optional<ImportFileLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ImportFileLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ImportFileLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select importFileLog from ImportFileLog importFileLog left join fetch importFileLog.importFile",
        countQuery = "select count(importFileLog) from ImportFileLog importFileLog"
    )
    Page<ImportFileLog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select importFileLog from ImportFileLog importFileLog left join fetch importFileLog.importFile")
    List<ImportFileLog> findAllWithToOneRelationships();

    @Query("select importFileLog from ImportFileLog importFileLog left join fetch importFileLog.importFile where importFileLog.id =:id")
    Optional<ImportFileLog> findOneWithToOneRelationships(@Param("id") Long id);

    Page<ImportFileLog> findByImportFileIdOrderByOccurredAtDesc(Long importFileId, Pageable pageable);

    Page<ImportFileLog> findByEventCodeInOrderByOccurredAtDesc(java.util.List<String> eventCodes, Pageable pageable);
}
