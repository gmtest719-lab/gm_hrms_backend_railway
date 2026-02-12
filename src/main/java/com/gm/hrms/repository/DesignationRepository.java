package com.gm.hrms.repository;

import com.gm.hrms.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    boolean existsByName(String name);
}
