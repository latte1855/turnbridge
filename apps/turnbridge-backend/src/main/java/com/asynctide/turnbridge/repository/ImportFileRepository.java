package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.ImportFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportFile entity.
 */
@Repository
public interface ImportFileRepository
    extends JpaRepository<ImportFile, Long>, JpaSpecificationExecutor<ImportFile>, QuerydslPredicateExecutor<ImportFile> {
    default Optional<ImportFile> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ImportFile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ImportFile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select importFile from ImportFile importFile left join fetch importFile.tenant",
        countQuery = "select count(importFile) from ImportFile importFile"
    )
    Page<ImportFile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select importFile from ImportFile importFile left join fetch importFile.tenant")
    List<ImportFile> findAllWithToOneRelationships();

    @Query("select importFile from ImportFile importFile left join fetch importFile.tenant where importFile.id =:id")
    Optional<ImportFile> findOneWithToOneRelationships(@Param("id") Long id);
}
