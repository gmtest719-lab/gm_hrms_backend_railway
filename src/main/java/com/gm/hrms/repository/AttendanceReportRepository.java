package com.gm.hrms.repository;

import com.gm.hrms.entity.Attendance;
import com.gm.hrms.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceReportRepository extends JpaRepository<Attendance, Long> {

    // ===== DAILY REPORT =====
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.personalInformation p
        LEFT JOIN FETCH a.workProfile wp
        LEFT JOIN FETCH wp.department d
        LEFT JOIN FETCH wp.designation des
        LEFT JOIN FETCH wp.shift s
        LEFT JOIN FETCH a.calculation c
        WHERE a.attendanceDate = :date
        AND (:departmentId  = 0L OR wp.department.id  = :departmentId)
        AND (:designationId = 0L OR wp.designation.id = :designationId)
        AND (:shiftId       = 0L OR wp.shift.id       = :shiftId)
        AND (:branchId      = 0L OR wp.branch.id      = :branchId)
        AND (:#{#status}    IS NULL OR a.status        = :status)
    """)
    Page<Attendance> findDailyReport(
            @Param("date")          LocalDate date,
            @Param("departmentId")  Long departmentId,
            @Param("designationId") Long designationId,
            @Param("shiftId")       Long shiftId,
            @Param("branchId")      Long branchId,
            @Param("status")        AttendanceStatus status,
            Pageable pageable
    );

    // ===== MONTHLY =====
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.personalInformation p
        LEFT JOIN FETCH a.workProfile wp
        LEFT JOIN FETCH wp.department d
        LEFT JOIN FETCH wp.designation des
        LEFT JOIN FETCH a.calculation c
        WHERE MONTH(a.attendanceDate) = :month
        AND YEAR(a.attendanceDate)    = :year
        AND (:personalId    = 0L OR p.id              = :personalId)
        AND (:departmentId  = 0L OR wp.department.id  = :departmentId)
        AND (:designationId = 0L OR wp.designation.id = :designationId)
    """)
    List<Attendance> findMonthlyAttendance(
            @Param("month")         int month,
            @Param("year")          int year,
            @Param("personalId")    Long personalId,
            @Param("departmentId")  Long departmentId,
            @Param("designationId") Long designationId
    );

    // ===== EMPLOYEE-WISE DETAIL — dates always provided, no IS NULL needed =====
    @Query("""
        SELECT a FROM Attendance a
        LEFT JOIN FETCH a.calculation c
        WHERE a.personalInformation.id = :personalId
        AND a.attendanceDate >= :fromDate
        AND a.attendanceDate <= :toDate
        ORDER BY a.attendanceDate DESC
    """)
    Page<Attendance> findEmployeeAttendance(
            @Param("personalId") Long personalId,
            @Param("fromDate")   LocalDate fromDate,
            @Param("toDate")     LocalDate toDate,
            Pageable pageable
    );

    // ===== ABSENT =====
    @Query("""
        SELECT p.id FROM PersonalInformation p
        LEFT JOIN p.workProfile wp
        WHERE p.active = true
        AND (:departmentId  = 0L OR wp.department.id  = :departmentId)
        AND (:designationId = 0L OR wp.designation.id = :designationId)
        AND p.id NOT IN (
            SELECT a.personalInformation.id FROM Attendance a
            WHERE a.attendanceDate = :date
        )
    """)
    List<Long> findAbsentPersonIds(
            @Param("date")          LocalDate date,
            @Param("departmentId")  Long departmentId,
            @Param("designationId") Long designationId
    );

    // ===== LATE COMING — dates always provided =====
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.personalInformation p
        LEFT JOIN FETCH a.workProfile wp
        LEFT JOIN FETCH wp.department d
        LEFT JOIN FETCH wp.designation des
        LEFT JOIN FETCH wp.shift s
        JOIN FETCH a.calculation c
        WHERE c.lateMinutes > 0
        AND a.attendanceDate >= :fromDate
        AND a.attendanceDate <= :toDate
        AND (:personalId    = 0L OR p.id              = :personalId)
        AND (:departmentId  = 0L OR wp.department.id  = :departmentId)
        AND (:designationId = 0L OR wp.designation.id = :designationId)
        ORDER BY a.attendanceDate DESC
    """)
    Page<Attendance> findLateComingReport(
            @Param("fromDate")      LocalDate fromDate,
            @Param("toDate")        LocalDate toDate,
            @Param("personalId")    Long personalId,
            @Param("departmentId")  Long departmentId,
            @Param("designationId") Long designationId,
            Pageable pageable
    );

    // ===== OVERTIME — dates always provided =====
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.personalInformation p
        LEFT JOIN FETCH a.workProfile wp
        LEFT JOIN FETCH wp.department d
        LEFT JOIN FETCH wp.designation des
        JOIN FETCH a.calculation c
        WHERE c.overtimeMinutes > 0
        AND a.attendanceDate >= :fromDate
        AND a.attendanceDate <= :toDate
        AND (:personalId    = 0L OR p.id              = :personalId)
        AND (:departmentId  = 0L OR wp.department.id  = :departmentId)
        AND (:designationId = 0L OR wp.designation.id = :designationId)
        ORDER BY a.attendanceDate DESC
    """)
    Page<Attendance> findOvertimeReport(
            @Param("fromDate")      LocalDate fromDate,
            @Param("toDate")        LocalDate toDate,
            @Param("personalId")    Long personalId,
            @Param("departmentId")  Long departmentId,
            @Param("designationId") Long designationId,
            Pageable pageable
    );

    // ===== SHIFT-WISE =====
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.personalInformation p
        LEFT JOIN FETCH a.workProfile wp
        LEFT JOIN FETCH wp.shift s
        LEFT JOIN FETCH a.calculation c
        WHERE wp.shift.id = :shiftId
        AND a.attendanceDate = :date
    """)
    List<Attendance> findShiftWiseAttendance(
            @Param("shiftId") Long shiftId,
            @Param("date")    LocalDate date
    );

    @Query("""
        SELECT DISTINCT a.personalInformation.id
        FROM Attendance a
        WHERE a.attendanceDate BETWEEN :fromDate AND :toDate
    """)
    List<Long> findPersonIdsWithAttendanceBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate
    );
}