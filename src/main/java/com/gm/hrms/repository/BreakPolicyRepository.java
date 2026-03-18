package com.gm.hrms.repository;

import com.gm.hrms.entity.BreakPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakPolicyRepository extends JpaRepository<BreakPolicy, Long> {

    boolean existsByBreakName(String breakName);
}