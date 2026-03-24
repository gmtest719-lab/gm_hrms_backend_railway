package com.gm.hrms.controller;

import com.gm.hrms.dto.request.ProjectRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // ================= ADMIN ONLY =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody ProjectRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project created successfully")
                        .data(projectService.create(dto))
                        .build()
        );
    }

    // ================= ADMIN + HR =================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Projects fetched successfully")
                        .data(projectService.getAll())
                        .build()
        );
    }

    // ================= ADMIN + HR =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project fetched successfully")
                        .data(projectService.getById(id))
                        .build()
        );
    }

    // ================= ADMIN ONLY =================
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @RequestBody ProjectRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project updated successfully")
                        .data(projectService.update(id, dto))
                        .build()
        );
    }

    // ================= ADMIN ONLY =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id){

        projectService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project deleted successfully")
                        .build()
        );
    }
}