package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    // CREATE → Admin Only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @Valid @RequestBody DepartmentRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department created successfully")
                        .data(service.createDepartment(dto))
                        .build()
        );
    }

    // UPDATE → Admin Only
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @RequestBody DepartmentRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department updated successfully")
                        .data(service.updateDepartment(id, dto))
                        .build()
        );
    }

    // GET BY ID → Admin + HR
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department fetched successfully")
                        .data(service.getDepartmentById(id))
                        .build()
        );
    }

    // GET ALL → Admin + HR + Employee
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<DepartmentResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<DepartmentResponseDTO>>builder()
                        .success(true)
                        .message("Departments fetched successfully")
                        .data(service.getAllDepartments(PageRequest.of(page, size)))
                        .build()
        );
    }
    // DELETE → Admin Only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(
            @PathVariable Long id) {

        service.deleteDepartment(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department deleted successfully")
                        .build()
        );
    }
}