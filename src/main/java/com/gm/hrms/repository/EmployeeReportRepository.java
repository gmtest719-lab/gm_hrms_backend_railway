package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeReportRepository extends JpaRepository<PersonalInformation, Long> {

    // ──────────────────────────────────────────────────────────
    // 1. EMPLOYEE MASTER — full filter (ADMIN / HR)
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN p.workProfile wp
            LEFT JOIN wp.department  d
            LEFT JOIN wp.designation des
            LEFT JOIN wp.branch      b
            WHERE (:personalId   = 0  OR p.id               = :personalId)
              AND (:departmentId = 0  OR d.id               = :departmentId)
              AND (:designationId= 0  OR des.id             = :designationId)
              AND (:branchId     = 0  OR b.id               = :branchId)
              AND (:active       IS NULL OR p.active         = :active)
              AND (:gender       IS NULL OR p.gender         = :gender)
            """)
    Page<PersonalInformation> findMasterReport(
            @Param("personalId")    Long    personalId,
            @Param("departmentId")  Long    departmentId,
            @Param("designationId") Long    designationId,
            @Param("branchId")      Long    branchId,
            @Param("active")        Boolean active,
            @Param("gender")        Gender  gender,
            Pageable pageable
    );

    // ──────────────────────────────────────────────────────────
    // 2. JOINING REPORT — filter by joining date range
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN p.workProfile wp
            LEFT JOIN wp.department  d
            LEFT JOIN wp.designation des
            LEFT JOIN wp.branch      b
            WHERE wp.dateOfJoining BETWEEN :fromDate AND :toDate
              AND (:departmentId  = 0   OR d.id    = :departmentId)
              AND (:designationId = 0   OR des.id  = :designationId)
              AND (:branchId      = 0   OR b.id    = :branchId)
              AND (:gender        IS NULL OR p.gender = :gender)
            ORDER BY wp.dateOfJoining ASC
            """)
    Page<PersonalInformation> findJoiningReport(
            @Param("fromDate")      LocalDate fromDate,
            @Param("toDate")        LocalDate toDate,
            @Param("departmentId")  Long      departmentId,
            @Param("designationId") Long      designationId,
            @Param("branchId")      Long      branchId,
            @Param("gender")        Gender    gender,
            Pageable pageable
    );

    // ──────────────────────────────────────────────────────────
    // 3. EXIT / ATTRITION — inactive employees with exit date
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN p.workProfile wp
            LEFT JOIN wp.department  d
            LEFT JOIN wp.designation des
            LEFT JOIN wp.branch      b
            WHERE p.active = false
              AND (:fromDate      IS NULL OR wp.exitDate   >= :fromDate)
              AND (:toDate        IS NULL OR wp.exitDate   <= :toDate)
              AND (:departmentId  = 0     OR d.id          = :departmentId)
              AND (:designationId = 0     OR des.id        = :designationId)
            """)
    Page<PersonalInformation> findExitReport(
            @Param("fromDate")      LocalDate fromDate,
            @Param("toDate")        LocalDate toDate,
            @Param("departmentId")  Long      departmentId,
            @Param("designationId") Long      designationId,
            Pageable pageable
    );

    // ──────────────────────────────────────────────────────────
    // 4. STATUS REPORT — active / inactive
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN p.workProfile wp
            LEFT JOIN wp.department  d
            LEFT JOIN wp.designation des
            WHERE (:active       IS NULL OR p.active  = :active)
              AND (:departmentId = 0     OR d.id      = :departmentId)
              AND (:designationId= 0     OR des.id    = :designationId)
            """)
    Page<PersonalInformation> findStatusReport(
            @Param("active")        Boolean active,
            @Param("departmentId")  Long    departmentId,
            @Param("designationId") Long    designationId,
            Pageable pageable
    );

    // ──────────────────────────────────────────────────────────
    // 5. DIRECTORY — all active employees (lightweight)
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN p.workProfile wp
            LEFT JOIN wp.department  d
            LEFT JOIN wp.designation des
            LEFT JOIN wp.branch      b
            WHERE p.active = true
              AND (:departmentId  = 0  OR d.id   = :departmentId)
              AND (:designationId = 0  OR des.id = :designationId)
              AND (:branchId      = 0  OR b.id   = :branchId)
              AND (:gender        IS NULL OR p.gender = :gender)
            """)
    Page<PersonalInformation> findDirectory(
            @Param("departmentId")  Long    departmentId,
            @Param("designationId") Long    designationId,
            @Param("branchId")      Long    branchId,
            @Param("gender")        Gender  gender,
            Pageable pageable
    );

    // ──────────────────────────────────────────────────────────
    // 6. DEPARTMENT-WISE — all employees per department
    //    (used by service; no pagination — grouped in memory)
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN FETCH p.workProfile wp
            LEFT JOIN FETCH wp.department  d
            LEFT JOIN FETCH wp.designation des
            LEFT JOIN FETCH wp.branch      b
            WHERE d.id = :departmentId
              AND (:active IS NULL OR p.active = :active)
            """)
    List<PersonalInformation> findByDepartment(
            @Param("departmentId") Long    departmentId,
            @Param("active")       Boolean active
    );

    // ──────────────────────────────────────────────────────────
    // 7. DESIGNATION-WISE — all employees per designation
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN FETCH p.workProfile wp
            LEFT JOIN FETCH wp.department  d
            LEFT JOIN FETCH wp.designation des
            WHERE des.id = :designationId
              AND (:active IS NULL OR p.active = :active)
            """)
    List<PersonalInformation> findByDesignation(
            @Param("designationId") Long    designationId,
            @Param("active")        Boolean active
    );

    // ──────────────────────────────────────────────────────────
    // 8. DIVERSITY — all active employees (gender breakdown)
    // ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM PersonalInformation p
            LEFT JOIN FETCH p.workProfile wp
            LEFT JOIN FETCH wp.department d
            WHERE (:departmentId = 0 OR d.id = :departmentId)
              AND (:active IS NULL   OR p.active = :active)
            """)
    List<PersonalInformation> findForDiversity(
            @Param("departmentId") Long    departmentId,
            @Param("active")       Boolean active
    );

    // ──────────────────────────────────────────────────────────
    // 9. COUNT helpers for summary
    // ──────────────────────────────────────────────────────────
    long countByActive(Boolean active);

    @Query("SELECT COUNT(DISTINCT wp.department.id) FROM WorkProfile wp WHERE wp.department IS NOT NULL")
    long countDistinctDepartments();

    @Query("SELECT COUNT(DISTINCT wp.designation.id) FROM WorkProfile wp WHERE wp.designation IS NOT NULL")
    long countDistinctDesignations();
}