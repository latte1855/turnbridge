package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.StoredObject;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoredObject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoredObjectRepository
    extends JpaRepository<StoredObject, Long>, JpaSpecificationExecutor<StoredObject>, QuerydslPredicateExecutor<StoredObject> {}
