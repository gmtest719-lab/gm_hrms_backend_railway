package com.gm.hrms.controller;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ProjectAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/project-assignments")
@RequiredArgsConstructor
public class ProjectAssignmentController {

    private final ProjectAssignmentService assignmentService;

    // ================= ADMIN ONLY =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> assign(
            @RequestBody ProjectAssignmentRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee assigned to project")
                        .data(assignmentService.assign(dto))
                        .build()
        );
    }

    // ================= ADMIN ONLY =================
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> remove(
            @RequestParam Long projectId,
            @RequestParam Long employeeId){

        assignmentService.remove(projectId, employeeId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Assignment removed")
                        .build()
        );
    }

    // ================= ADMIN + HR =================
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployeeResponseDTO>>> getEmployees(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<EmployeeResponseDTO>>builder()
                        .success(true)
                        .message("Employees fetched successfully")
                        .data(assignmentService.getEmployeesByProject(
                                projectId,
                                PageRequest.of(page, size)
                        ))
                        .build()
        );
    }

    // ================= EMPLOYEE (SELF ONLY) =================
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProjectResponseDTO>>> getMyProjects(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<ProjectResponseDTO>>builder()
                        .success(true)
                        .message("Projects fetched successfully")
                        .data(assignmentService.getProjectsByEmployee(
                                user.getUserId(),
                                PageRequest.of(page, size)
                        ))
                        .build()
        );
    }
}