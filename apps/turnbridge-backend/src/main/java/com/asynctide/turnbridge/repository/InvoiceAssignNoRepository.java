package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.InvoiceAssignNo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InvoiceAssignNo entity.
 */
@Repository
public interface InvoiceAssignNoRepository
    extends JpaRepository<InvoiceAssignNo, Long>, JpaSpecificationExecutor<InvoiceAssignNo>, QuerydslPredicateExecutor<InvoiceAssignNo> {
    default Optional<InvoiceAssignNo> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InvoiceAssignNo> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InvoiceAssignNo> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select invoiceAssignNo from InvoiceAssignNo invoiceAssignNo left join fetch invoiceAssignNo.tenant",
        countQuery = "select count(invoiceAssignNo) from InvoiceAssignNo invoiceAssignNo"
    )
    Page<InvoiceAssignNo> findAllWithToOneRelationships(Pageable pageable);

    @Query("select invoiceAssignNo from InvoiceAssignNo invoiceAssignNo left join fetch invoiceAssignNo.tenant")
    List<InvoiceAssignNo> findAllWithToOneRelationships();

    @Query(
        "select invoiceAssignNo from InvoiceAssignNo invoiceAssignNo left join fetch invoiceAssignNo.tenant where invoiceAssignNo.id =:id"
    )
    Optional<InvoiceAssignNo> findOneWithToOneRelationships(@Param("id") Long id);
}
