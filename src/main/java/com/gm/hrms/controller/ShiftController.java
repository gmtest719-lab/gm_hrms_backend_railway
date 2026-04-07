package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.ShiftResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_SHIFT,
            resource    = "Shift",
            description = "Create new shift"
    )
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> create(
            @Valid @RequestBody ShiftRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<ShiftResponseDTO>builder()
                        .success(true)
                        .message("Shift created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.<List<ShiftResponseDTO>>builder()
                        .success(true)
                        .data(service.getAll())
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<ShiftResponseDTO>builder()
                        .success(true)
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_SHIFT,
            resource    = "Shift",
            description = "Delete shift"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Shift deleted successfully")
                        .build()
        );
    }
}