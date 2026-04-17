// com/gm/hrms/repository/ProjectReportRepository.java
package com.gm.hrms.repository;

import com.gm.hrms.entity.Project;
import com.gm.hrms.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface ProjectReportRepository extends JpaRepository<Project, Long> {

    // ─── PROJECT MASTER
    @Query("""
        SELECT p FROM Project p
        WHERE (:#{#status} IS NULL OR p.status = :status)
          AND p.startDate >= :fromDate
          AND p.endDate   <= :toDate
          AND (:projectId = 0L OR p.id = :projectId)
        ORDER BY p.startDate ASC
    """)
    Page<Project> findProjectsWithFilters(
            @Param("status")    ProjectStatus status,
            @Param("fromDate")  LocalDate fromDate,
            @Param("toDate")    LocalDate toDate,
            @Param("projectId") Long projectId,
            Pageable pageable
    );

    // ─── ALL PROJECTS FOR STATUS GROUPING ───────────────────────────────────
    @Query("""
        SELECT p FROM Project p
        WHERE p.startDate >= :fromDate
          AND p.endDate   <= :toDate
    """)
    List<Project> findAllForStatusGrouping(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate
    );

    // ─── PROJECTS ASSIGNED TO A SPECIFIC EMPLOYEE ───────────────────────────
    @Query("""
        SELECT DISTINCT pa.project FROM ProjectAssignment pa
        WHERE pa.employee.personalInformation.id = :personalId
          AND (:#{#status} IS NULL OR pa.project.status = :status)
          AND pa.project.startDate >= :fromDate
          AND pa.project.endDate   <= :toDate
    """)
    Page<Project> findProjectsByEmployeePersonalId(
            @Param("personalId") Long personalId,
            @Param("status")     ProjectStatus status,
            @Param("fromDate")   LocalDate fromDate,
            @Param("toDate")     LocalDate toDate,
            Pageable pageable
    );

    // ─── PROJECTS ASSIGNED TO A SPECIFIC TRAINEE ────────────────────────────
    @Query("""
        SELECT DISTINCT pa.project FROM ProjectAssignment pa
        WHERE pa.trainee.personalInformation.id = :personalId
          AND (:#{#status} IS NULL OR pa.project.status = :status)
          AND pa.project.startDate >= :fromDate
          AND pa.project.endDate   <= :toDate
    """)
    Page<Project> findProjectsByTraineePersonalId(
            @Param("personalId") Long personalId,
            @Param("status")     ProjectStatus status,
            @Param("fromDate")   LocalDate fromDate,
            @Param("toDate")     LocalDate toDate,
            Pageable pageable
    );

    // ─── PROJECTS ASSIGNED TO A SPECIFIC INTERN ─────────────────────────────
    @Query("""
        SELECT DISTINCT pa.project FROM ProjectAssignment pa
        WHERE pa.intern.personalInformation.id = :personalId
          AND (:#{#status} IS NULL OR pa.project.status = :status)
          AND pa.project.startDate >= :fromDate
          AND pa.project.endDate   <= :toDate
    """)
    Page<Project> findProjectsByInternPersonalId(
            @Param("personalId") Long personalId,
            @Param("status")     ProjectStatus status,
            @Param("fromDate")   LocalDate fromDate,
            @Param("toDate")     LocalDate toDate,
            Pageable pageable
    );

    // ─── COUNT QUERIES FOR SUMMARY ───────────────────────────────────────────
    long countByStatus(ProjectStatus status);

    @Query("SELECT COUNT(DISTINCT pa.project.id) FROM ProjectAssignment pa " +
            "WHERE pa.employee.personalInformation.id = :personalId")
    long countProjectsByEmployeePersonalId(@Param("personalId") Long personalId);
}