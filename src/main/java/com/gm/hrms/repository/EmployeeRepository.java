package com.gm.hrms.repository;

import com.gm.hrms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);
}

