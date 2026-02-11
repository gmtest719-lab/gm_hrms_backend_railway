package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveApplication;
import com.gm.hrms.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveApplicationRepository
        extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByEmployeeId(Long employeeId);

    List<LeaveApplication> findByStatus(LeaveStatus status);

    // Date Range
    List<LeaveApplication> findByStartDateBetween(
            LocalDate start,
            LocalDate end
    );

    // Employee Date Range
    List<LeaveApplication> findByEmployeeIdAndStartDateBetween(
            Long empId,
            LocalDate start,
            LocalDate end
    );

    // Department Date Range
    @Query("""
SELECT l FROM LeaveApplication l
JOIN l.employee e
JOIN e.department d
WHERE d.id=:deptId
AND l.startDate BETWEEN :start AND :end
""")
    List<LeaveApplication> findByDepartment(
            Long deptId,
            LocalDate start,
            LocalDate end
    );

    // Status
    List<LeaveApplication> findByStatusAndStartDateBetween(
            LeaveStatus status,
            LocalDate start,
            LocalDate end
    );

    // Type
    List<LeaveApplication> findByLeaveTypeIdAndStartDateBetween(
            Long leaveTypeId,
            LocalDate start,
            LocalDate end
    );

    // Today
    List<LeaveApplication> findByStartDate(LocalDate date);

    // Monthly
    @Query("""
SELECT l FROM LeaveApplication l
WHERE MONTH(l.startDate)=:month
AND YEAR(l.startDate)=:year
""")
    List<LeaveApplication> findMonthly(int month, int year);

}
