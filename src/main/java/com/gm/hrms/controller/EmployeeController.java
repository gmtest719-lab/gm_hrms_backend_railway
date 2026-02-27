package com.gm.hrms.controller;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDTO dto) {

        EmployeeResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponseDTO>builder()
                        .success(true)
                        .message("Employee updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getById(
            @PathVariable Long id) {

        EmployeeResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponseDTO>builder()
                        .success(true)
                        .message("Employee fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAll() {

        List<EmployeeResponseDTO> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<EmployeeResponseDTO>>builder()
                        .success(true)
                        .message("Employees fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Employee deactivated successfully")
                        .build()
        );
    }
}