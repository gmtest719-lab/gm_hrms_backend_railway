package com.gm.hrms.controller;

import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ProjectAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project-assignments")
@RequiredArgsConstructor
public class ProjectAssignmentController {

    private final ProjectAssignmentService assignmentService;

    @PostMapping
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

    @DeleteMapping
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

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<?>> getEmployees(@PathVariable Long projectId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(assignmentService.getEmployeesByProject(projectId))
                        .build()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<?>> getProjects(@PathVariable Long employeeId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(assignmentService.getProjectsByEmployee(employeeId))
                        .build()
        );
    }
}

