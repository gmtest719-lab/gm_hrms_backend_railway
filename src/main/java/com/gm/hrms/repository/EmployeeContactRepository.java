package com.gm.hrms.repository;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeContactRepository extends JpaRepository<EmployeeContact, Long> {

    Optional<EmployeeContact> findByEmployee(Employee employee);

}
