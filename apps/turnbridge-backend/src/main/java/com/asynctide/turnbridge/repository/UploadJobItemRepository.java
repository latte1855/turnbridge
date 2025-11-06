package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.UploadJobItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UploadJobItem entity.
 */
@Repository
public interface UploadJobItemRepository
    extends JpaRepository<UploadJobItem, Long>, JpaSpecificationExecutor<UploadJobItem>, QuerydslPredicateExecutor<UploadJobItem> {
    default Optional<UploadJobItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UploadJobItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UploadJobItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select uploadJobItem from UploadJobItem uploadJobItem left join fetch uploadJobItem.job",
        countQuery = "select count(uploadJobItem) from UploadJobItem uploadJobItem"
    )
    Page<UploadJobItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select uploadJobItem from UploadJobItem uploadJobItem left join fetch uploadJobItem.job")
    List<UploadJobItem> findAllWithToOneRelationships();

    @Query("select uploadJobItem from UploadJobItem uploadJobItem left join fetch uploadJobItem.job where uploadJobItem.id =:id")
    Optional<UploadJobItem> findOneWithToOneRelationships(@Param("id") Long id);
}
