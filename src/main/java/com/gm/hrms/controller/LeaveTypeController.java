package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> create(
            @Valid @RequestBody LeaveTypeRequestDTO request) {

        LeaveTypeResponseDTO response = service.create(request);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Leave type created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> getById(
            @PathVariable Long id) {

        LeaveTypeResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Leave type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveTypeResponseDTO>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<LeaveTypeResponseDTO> response =
                service.getAll(search, PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveTypeResponseDTO>>builder()
                        .success(true)
                        .message("Leave types fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= PATCH UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody LeaveTypeRequestDTO request) {

        LeaveTypeResponseDTO response = service.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Leave type updated successfully")
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
                        .message("Leave type deleted successfully")
                        .build()
        );
    }
}