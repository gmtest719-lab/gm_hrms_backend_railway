package com.gm.hrms.repository;

import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankLegalDetailsRepository
        extends JpaRepository<BankLegalDetails, Long> {

    Optional<BankLegalDetails> findByEmployee(Employee employee);
    Optional<BankLegalDetails> findByEmployeeId(Long employeeId);

}