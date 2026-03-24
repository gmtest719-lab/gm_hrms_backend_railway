package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Project;
import com.gm.hrms.entity.ProjectAssignment;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.ProjectAssignmentMapper;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.ProjectAssignmentRepository;
import com.gm.hrms.repository.ProjectRepository;
import com.gm.hrms.service.ProjectAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectAssignmentServiceImpl implements ProjectAssignmentService {

    private final ProjectAssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ProjectAssignmentResponseDTO assign(ProjectAssignmentRequestDTO dto) {

        if(assignmentRepository.existsByProjectIdAndEmployeeId(dto.getProjectId(), dto.getEmployeeId())){
            throw new DuplicateResourceException("Employee already assigned to project");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        ProjectAssignment pa = new ProjectAssignment();
        pa.setProject(project);
        pa.setEmployee(employee);
        pa.setRoleInProject(dto.getRoleInProject());

        return ProjectAssignmentMapper.toResponse(
                assignmentRepository.save(pa)
        );
    }

    @Override
    public void remove(Long projectId, Long employeeId) {

        if(!assignmentRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)){
            throw new ResourceNotFoundException("Assignment not found");
        }

        assignmentRepository.deleteByProjectIdAndEmployeeId(projectId, employeeId);
    }

    @Override
    public List<ProjectAssignmentResponseDTO> getEmployeesByProject(Long projectId) {

        return assignmentRepository.findByProjectId(projectId)
                .stream()
                .map(ProjectAssignmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProjectAssignmentResponseDTO> getProjectsByEmployee(Long employeeId) {

        return assignmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(ProjectAssignmentMapper::toResponse)
                .toList();
    }
}
