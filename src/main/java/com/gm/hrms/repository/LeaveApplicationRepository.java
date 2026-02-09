package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveApplication;
import com.gm.hrms.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository
        extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByEmployeeId(Long employeeId);

    List<LeaveApplication> findByStatus(LeaveStatus status);
}
