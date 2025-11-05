package com.asynctide.turnbridge.repository;

import com.asynctide.turnbridge.domain.Authority;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String>, QuerydslPredicateExecutor<Authority> {}
