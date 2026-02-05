package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    //  CREATE
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

    //  UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Department updated successfully")
                        .data(service.updateDepartment(id, dto))
                        .build()
        );
    }

    //  GET BY ID
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

    //  GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Departments fetched successfully")
                        .data(service.getAllDepartments())
                        .build()
        );
    }

    //  DELETE
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
