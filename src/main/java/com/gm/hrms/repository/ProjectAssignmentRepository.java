package com.gm.hrms.repository;

import com.gm.hrms.entity.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    List<ProjectAssignment> findByProjectId(Long projectId);

    List<ProjectAssignment> findByEmployeeId(Long employeeId);

    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    void deleteByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}

