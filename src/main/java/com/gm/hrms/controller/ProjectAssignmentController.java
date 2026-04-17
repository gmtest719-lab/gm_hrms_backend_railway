package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import com.gm.hrms.enums.AssigneeType;
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

    // ─────────────────────────── ASSIGN ──────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(
            action      = AuditAction.ASSIGN_PROJECT,
            resource    = "ProjectAssignment",
            description = "Assign employee/trainee/intern to project"
    )
    public ResponseEntity<ApiResponse<?>> assign(
            @RequestBody ProjectAssignmentRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Assigned to project successfully")
                        .data(assignmentService.assign(dto))
                        .build()
        );
    }

    // ─────────────────────────── REMOVE ──────────────────────────────────────
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(
            action      = AuditAction.REMOVE_ASSIGNMENT,
            resource    = "ProjectAssignment",
            description = "Remove employee/trainee/intern from project"
    )
    public ResponseEntity<ApiResponse<?>> remove(
            @RequestParam Long projectId,
            @RequestParam Long assigneeId,
            @RequestParam AssigneeType assigneeType) {

        assignmentService.remove(projectId, assigneeId, assigneeType);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Assignment removed successfully")
                        .build()
        );
    }

    // ─────────────────────── GET BY PROJECT ──────────────────────────────────
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProjectAssignmentResponseDTO>>> getByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<ProjectAssignmentResponseDTO>>builder()
                        .success(true)
                        .message("Assignees fetched successfully")
                        .data(assignmentService.getEmployeesByProject(
                                projectId, PageRequest.of(page, size)))
                        .build()
        );
    }

    // ───────────────────────────── MY PROJECTS ────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('EMPLOYEE','TRAINEE','INTERN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProjectAssignmentResponseDTO>>> getMyProjects(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<ProjectAssignmentResponseDTO>>builder()
                        .success(true)
                        .message("Your projects fetched successfully")
                        .data(assignmentService.getMyProjects(
                                user.getUserId(),
                                user.getRole(),
                                PageRequest.of(page, size)))
                        .build()
        );
    }
}