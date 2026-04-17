package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.ProjectAssignmentMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.ProjectAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectAssignmentServiceImpl implements ProjectAssignmentService {

    private final ProjectAssignmentRepository assignmentRepository;
    private final ProjectRepository           projectRepository;
    private final EmployeeRepository          employeeRepository;
    private final TraineeRepository           traineeRepository;
    private final InternRepository            internRepository;

    // ─────────────────────────── ASSIGN ──────────────────────────────────────
    @Override
    public ProjectAssignmentResponseDTO assign(ProjectAssignmentRequestDTO dto) {

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        ProjectAssignment pa = new ProjectAssignment();
        pa.setProject(project);
        pa.setRoleInProject(dto.getRoleInProject());
        pa.setAssigneeType(dto.getAssigneeType());

        switch (dto.getAssigneeType()) {

            case EMPLOYEE -> {
                if (assignmentRepository.existsByProjectIdAndEmployeeId(
                        dto.getProjectId(), dto.getAssigneeId())) {
                    throw new DuplicateResourceException("Employee already assigned to this project");
                }
                Employee employee = employeeRepository.findById(dto.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
                pa.setEmployee(employee);
            }

            case TRAINEE -> {
                if (assignmentRepository.existsByProjectIdAndTraineeId(
                        dto.getProjectId(), dto.getAssigneeId())) {
                    throw new DuplicateResourceException("Trainee already assigned to this project");
                }
                Trainee trainee = traineeRepository.findById(dto.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
                pa.setTrainee(trainee);
            }

            case INTERN -> {
                if (assignmentRepository.existsByProjectIdAndInternId(
                        dto.getProjectId(), dto.getAssigneeId())) {
                    throw new DuplicateResourceException("Intern already assigned to this project");
                }
                Intern intern = internRepository.findById(dto.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));
                pa.setIntern(intern);
            }
        }

        return ProjectAssignmentMapper.toResponse(assignmentRepository.save(pa));
    }

    // ─────────────────────────── REMOVE ──────────────────────────────────────
    @Override
    public void remove(Long projectId, Long assigneeId, AssigneeType assigneeType) {

        switch (assigneeType) {

            case EMPLOYEE -> {
                if (!assignmentRepository.existsByProjectIdAndEmployeeId(projectId, assigneeId))
                    throw new ResourceNotFoundException("Assignment not found");
                assignmentRepository.deleteByProjectIdAndEmployeeId(projectId, assigneeId);
            }

            case TRAINEE -> {
                if (!assignmentRepository.existsByProjectIdAndTraineeId(projectId, assigneeId))
                    throw new ResourceNotFoundException("Assignment not found");
                assignmentRepository.deleteByProjectIdAndTraineeId(projectId, assigneeId);
            }

            case INTERN -> {
                if (!assignmentRepository.existsByProjectIdAndInternId(projectId, assigneeId))
                    throw new ResourceNotFoundException("Assignment not found");
                assignmentRepository.deleteByProjectIdAndInternId(projectId, assigneeId);
            }
        }
    }

    // ─────────────────────── GET BY PROJECT ──────────────────────────────────
    @Override
    public PageResponseDTO<ProjectAssignmentResponseDTO> getEmployeesByProject(
            Long projectId, Pageable pageable) {

        return buildPage(assignmentRepository.findByProjectId(projectId, pageable));
    }

    // ─────────────── GET MY PROJECTS (self — all three types) ────────────────
    @Override
    public PageResponseDTO<ProjectAssignmentResponseDTO> getMyProjects(
            Long personalInformationId, String role, Pageable pageable) {

        return switch (role) {

            case "EMPLOYEE" -> {
                Employee emp = employeeRepository
                        .findByPersonalInformationId(personalInformationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));
                yield buildPage(assignmentRepository.findByEmployeeId(emp.getId(), pageable));
            }

            case "TRAINEE" -> {
                Trainee tr = traineeRepository
                        .findByPersonalInformationId(personalInformationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));
                yield buildPage(assignmentRepository.findByTraineeId(tr.getId(), pageable));
            }

            case "INTERN" -> {
                Intern intern = internRepository
                        .findByPersonalInformationId(personalInformationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Intern profile not found"));
                yield buildPage(assignmentRepository.findByInternId(intern.getId(), pageable));
            }

            default -> throw new ResourceNotFoundException(
                    "No project assignments available for role: " + role);
        };
    }

    // ─────────────────────────── HELPER ──────────────────────────────────────
    private PageResponseDTO<ProjectAssignmentResponseDTO> buildPage(
            Page<ProjectAssignment> page) {

        List<ProjectAssignmentResponseDTO> content = page.getContent()
                .stream()
                .map(ProjectAssignmentMapper::toResponse)
                .toList();

        return PageResponseDTO.<ProjectAssignmentResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}