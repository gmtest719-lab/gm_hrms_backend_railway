package com.gm.hrms.repository;

import com.gm.hrms.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    Optional<Department> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    List<Department> findAllByParentId(Long parentId);

    long countByParentId(Long parentId);

    Page<Department> findAllByParentIsNull(Pageable pageable);
}