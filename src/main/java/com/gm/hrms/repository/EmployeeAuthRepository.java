package com.gm.hrms.repository;

import com.gm.hrms.entity.EmployeeAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeAuthRepository extends JpaRepository<EmployeeAuth, Integer> {

    Optional<EmployeeAuth> findByUsername(String username);
}
