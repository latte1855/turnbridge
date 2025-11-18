package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.Tenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tenant entity.
 */
@Repository
public interface TenantRepository
    extends JpaRepository<Tenant, Long>, JpaSpecificationExecutor<Tenant>, QuerydslPredicateExecutor<Tenant> {
    java.util.Optional<Tenant> findOneByCode(String code);
}
