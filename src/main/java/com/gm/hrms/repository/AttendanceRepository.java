package com.gm.hrms.repository;

import com.gm.hrms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);

    // Date Range
    @Query("""
SELECT a FROM Attendance a
JOIN FETCH a.employee e
JOIN FETCH e.department d
WHERE a.date BETWEEN :startDate AND :endDate
""")
    List<Attendance> findByDateRange(LocalDate startDate, LocalDate endDate);


    // Department
    @Query("""
SELECT a FROM Attendance a
JOIN FETCH a.employee e
JOIN FETCH e.department d
WHERE d.id = :deptId
AND a.date BETWEEN :startDate AND :endDate
""")
    List<Attendance> findByDepartmentAndDateRange(
            Long deptId,
            LocalDate startDate,
            LocalDate endDate
    );


    // Today All
    List<Attendance> findByDate(LocalDate date);


    // Late
    List<Attendance> findByLateInTrueAndDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );


    // Half Day
    List<Attendance> findByHalfDayTrueAndDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );


    // Employee Date Range
    List<Attendance> findByEmployeeIdAndDateBetween(
            Long empId,
            LocalDate start,
            LocalDate end
    );


    // Monthly
    @Query("""
SELECT a FROM Attendance a
WHERE MONTH(a.date)=:month
AND YEAR(a.date)=:year
""")
    List<Attendance> findMonthly(int month, int year);


    long countByEmployeeIdAndLateInTrueAndDateBetween(
            Long employeeId,
            LocalDate start,
            LocalDate end
    );



}

