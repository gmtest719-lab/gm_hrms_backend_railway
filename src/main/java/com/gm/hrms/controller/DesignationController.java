package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DesignationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @Valid @RequestBody DesignationRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designations fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody DesignationRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation deleted successfully")
                        .build()
        );
    }
}
