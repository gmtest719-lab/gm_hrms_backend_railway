package com.gm.hrms.repository;

import com.gm.hrms.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByBranchCode(String branchCode);

    boolean existsByBranchCode(String branchCode);

    List<Branch> findByParentIsNull();

    List<Branch> findByParentId(Long parentId);

    List<Branch> findByParentIsNullAndActiveTrue();
}