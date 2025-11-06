package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.UploadJob;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UploadJob entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadJobRepository
    extends JpaRepository<UploadJob, Long>, JpaSpecificationExecutor<UploadJob>, QuerydslPredicateExecutor<UploadJob> {}
