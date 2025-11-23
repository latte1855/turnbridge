package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.Invoice;
import java.util.Optional;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Invoice entity.
 */
@Repository
public interface InvoiceRepository
    extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice>, QuerydslPredicateExecutor<Invoice> {

    Page<Invoice> findByInvoiceStatus(InvoiceStatus status, Pageable pageable);

    Optional<Invoice> findByInvoiceNo(String invoiceNo);

    default Optional<Invoice> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Invoice> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Invoice> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select invoice from Invoice invoice left join fetch invoice.importFile left join fetch invoice.tenant",
        countQuery = "select count(invoice) from Invoice invoice"
    )
    Page<Invoice> findAllWithToOneRelationships(Pageable pageable);

    @Query("select invoice from Invoice invoice left join fetch invoice.importFile left join fetch invoice.tenant")
    List<Invoice> findAllWithToOneRelationships();

    @Query("select invoice from Invoice invoice left join fetch invoice.importFile left join fetch invoice.tenant where invoice.id =:id")
    Optional<Invoice> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select i.importFile.id, i.tbCode, i.tbCategory, count(i) from Invoice i where i.importFile.id in :ids and i.tbCode is not null group by i.importFile.id, i.tbCode, i.tbCategory order by count(i) desc"
    )
    List<Object[]> findTbSummaryByImportFileIds(@Param("ids") List<Long> ids);

    @Query(
        "select i.tbCode, i.tbCategory, count(i), min(i.invoiceNo), max(i.importFile.id), max(i.issuedAt) from Invoice i where i.tbCode is not null group by i.tbCode, i.tbCategory order by count(i) desc"
    )
    List<Object[]> findTbErrorSummary();
}
