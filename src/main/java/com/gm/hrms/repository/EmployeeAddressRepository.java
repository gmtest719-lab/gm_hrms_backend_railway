package com.gm.hrms.repository;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeAddressRepository extends JpaRepository<EmployeeAddress, Long> {

    Optional<EmployeeAddress> findByEmployee(Employee employee);

}
