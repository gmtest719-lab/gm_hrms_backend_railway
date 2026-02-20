package com.gm.hrms.repository;

import com.gm.hrms.entity.EmployeeAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeAuthRepository extends JpaRepository<EmployeeAuth, Integer> {

    Optional<EmployeeAuth> findByUsername(String username);

    @Query("""
    SELECT a
    FROM EmployeeAuth a
    JOIN FETCH a.employee e
    WHERE a.username = :username
""")
    Optional<EmployeeAuth> findByUsernameWithEmployee(String username);

}
