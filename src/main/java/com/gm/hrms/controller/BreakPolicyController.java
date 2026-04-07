package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BreakPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/break-policies")
@RequiredArgsConstructor
public class BreakPolicyController {

    private final BreakPolicyService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_BREAK_POLICY,
            resource    = "BreakPolicy",
            description = "Create break policy"
    )
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> create(
            @Valid @RequestBody BreakPolicyRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_BREAK_POLICY,
            resource    = "BreakPolicy",
            description = "Update break policy"
    )
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody BreakPolicyRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BreakPolicyResponseDTO>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.<List<BreakPolicyResponseDTO>>builder()
                        .success(true)
                        .message("Break policies fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_BREAK_POLICY,
            resource    = "BreakPolicy",
            description = "Deactivate break policy"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Break policy deactivated successfully")
                        .build()
        );
    }
}