package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.RoleType;
import org.springframework.data.domain.Pageable;

public interface ProjectAssignmentService {

    ProjectAssignmentResponseDTO assign(ProjectAssignmentRequestDTO dto);

    void remove(Long projectId, Long assigneeId, AssigneeType assigneeType);

    PageResponseDTO<ProjectAssignmentResponseDTO> getEmployeesByProject(Long projectId, Pageable pageable);

    PageResponseDTO<ProjectAssignmentResponseDTO> getMyProjects(
            Long personalInformationId, String role, Pageable pageable);
}