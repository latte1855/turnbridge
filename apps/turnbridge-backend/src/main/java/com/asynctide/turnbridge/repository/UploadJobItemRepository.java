package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;

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


    /** 依數字主鍵 id（UploadJob.id）查詢 */
    Page<UploadJobItem> findByJobId(Long jobId, Pageable pageable);

    /** 依字串 jobId（UploadJob.jobId）查詢 */
    Page<UploadJobItem> findByJobJobId(String jobId, Pageable pageable);

    /** 依字串 jobId（UploadJob.jobId）與狀態 查詢 */
    Page<UploadJobItem> findByJobJobIdAndStatus(String jobId, JobItemStatus status, Pageable pageable);

    boolean existsByJobIdAndLineNo(Long jobId, Integer lineNo);
    
    /** 依數字主鍵 id（UploadJob.id）統計 */
    long countByJobId(Long jobId);
    
    /** 依數字主鍵 id（UploadJob.id）跟明細狀態 統計 */
    long countByJobIdAndStatus(Long jobId, JobItemStatus status);
    
    // 以 jobId(String) 的 join 版本（for 便利）
    /** 依字串 jobId（UploadJob.jobId） 統計 */
    @Query("select count(i) from UploadJobItem i where i.job.jobId = :jobId")
    long countByJobJobId(@Param("jobId") String jobId);

    /** 依字串 jobId（UploadJob.jobId）跟明細狀態 統計 */
    @Query("select count(i) from UploadJobItem i where i.job.jobId = :jobId and i.status = :status")
    long countByJobJobIdAndStatus(@Param("jobId") String jobId, @Param("status") JobItemStatus status);
    
    /** 批次更新：把某個 字串 jobId（UploadJob.jobId）底下 ERROR -> QUEUED */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update UploadJobItem i
           set i.status = com.asynctide.turnbridge.domain.enumeration.JobItemStatus.QUEUED,
               i.resultCode = null,
               i.resultMsg = null
         where i.job.jobId = :jobId
           and i.status = com.asynctide.turnbridge.domain.enumeration.JobItemStatus.ERROR
    """)
    int requeueFailedByJobJobId(@Param("jobId") String jobId);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UploadJobItem i set i.status = :to where i.job.jobId = :jobId and i.status = :from")
    int updateStatusByJobJobIdAndStatus(@Param("jobId") String jobId,
                                        @Param("from") JobItemStatus from,
                                        @Param("to") JobItemStatus to);

}
