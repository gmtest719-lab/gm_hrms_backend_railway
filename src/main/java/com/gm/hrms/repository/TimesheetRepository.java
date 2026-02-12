package com.gm.hrms.repository;

import com.gm.hrms.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TimesheetRepository
        extends JpaRepository<Timesheet, Long> {

    List<Timesheet> findByEmployeeId(Long employeeId);

    // Date Range
    List<Timesheet> findByWorkDateBetween(
            LocalDate start,
            LocalDate end
    );

    // Employee + Date
    List<Timesheet> findByEmployeeIdAndWorkDateBetween(
            Long empId,
            LocalDate start,
            LocalDate end
    );

    // Project + Date
    List<Timesheet> findByProjectIdAndWorkDateBetween(
            Long projectId,
            LocalDate start,
            LocalDate end
    );

    // Status
    List<Timesheet> findByStatusAndWorkDateBetween(
            String status,
            LocalDate start,
            LocalDate end
    );

    // Today
    List<Timesheet> findByWorkDate(LocalDate date);

    // Monthly
    @Query("""
SELECT t FROM Timesheet t
WHERE MONTH(t.workDate)=:month
AND YEAR(t.workDate)=:year
""")
    List<Timesheet> findMonthly(int month, int year);


}

