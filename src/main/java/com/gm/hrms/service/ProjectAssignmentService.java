package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;

import java.util.List;

public interface ProjectAssignmentService {

    ProjectAssignmentResponseDTO assign(ProjectAssignmentRequestDTO dto);

    void remove(Long projectId, Long employeeId);

    List<ProjectAssignmentResponseDTO> getEmployeesByProject(Long projectId);

    List<ProjectAssignmentResponseDTO> getProjectsByEmployee(Long employeeId);
}

