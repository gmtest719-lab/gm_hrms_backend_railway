package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeavePolicyRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeavePolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-policies")
@RequiredArgsConstructor
public class LeavePolicyController {

    private final LeavePolicyService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> create(
            @Valid @RequestBody LeavePolicyRequestDTO request) {

        LeavePolicyResponseDTO response = service.create(request);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> getById(
            @PathVariable Long id) {

        LeavePolicyResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeavePolicyResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<LeavePolicyResponseDTO> response =
                service.getAll(PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeavePolicyResponseDTO>>builder()
                        .success(true)
                        .message("Policies fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= PUT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody LeavePolicyRequestDTO request) {

        LeavePolicyResponseDTO response = service.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> patch(
            @PathVariable Long id,
            @RequestBody LeavePolicyRequestDTO request) {

        LeavePolicyResponseDTO response = service.patchUpdate(id, request);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy updated successfully")
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
                        .message("Policy deleted successfully")
                        .build()
        );
    }
}