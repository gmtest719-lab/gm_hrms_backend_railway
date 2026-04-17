package com.gm.hrms.repository;

import com.gm.hrms.entity.ProjectAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    // ── by project (for listing all assignees of a project) ──────────────────
    Page<ProjectAssignment> findByProjectId(Long projectId, Pageable pageable);

    // ── EMPLOYEE ─────────────────────────────────────────────────────────────
    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);
    void    deleteByProjectIdAndEmployeeId(Long projectId, Long employeeId);
    Page<ProjectAssignment> findByEmployeeId(Long employeeId, Pageable pageable);

    // ── TRAINEE ──────────────────────────────────────────────────────────────
    boolean existsByProjectIdAndTraineeId(Long projectId, Long traineeId);
    void    deleteByProjectIdAndTraineeId(Long projectId, Long traineeId);
    Page<ProjectAssignment> findByTraineeId(Long traineeId, Pageable pageable);

    // ── INTERN ───────────────────────────────────────────────────────────────
    boolean existsByProjectIdAndInternId(Long projectId, Long internId);
    void    deleteByProjectIdAndInternId(Long projectId, Long internId);
    Page<ProjectAssignment> findByInternId(Long internId, Pageable pageable);
}