package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.TrackRange;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TrackRange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrackRangeRepository
    extends JpaRepository<TrackRange, Long>, JpaSpecificationExecutor<TrackRange>, QuerydslPredicateExecutor<TrackRange> {}
