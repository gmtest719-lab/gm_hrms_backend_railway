package com.gm.hrms.controller;

import com.gm.hrms.dto.request.CompOffRuleRequestDTO;
import com.gm.hrms.dto.response.CompOffRuleResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.CompOffRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comp-off-rules")
@RequiredArgsConstructor
public class CompOffRuleController {

    private final CompOffRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<CompOffRuleResponseDTO>> create(
            @Valid @RequestBody CompOffRuleRequestDTO request) {

        CompOffRuleResponseDTO response = service.create(request);

        return ResponseEntity.ok(
                ApiResponse.<CompOffRuleResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rule created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY POLICY =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<CompOffRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        CompOffRuleResponseDTO response = service.getByPolicy(policyId);

        return ResponseEntity.ok(
                ApiResponse.<CompOffRuleResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rule fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= PATCH UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CompOffRuleResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody CompOffRuleRequestDTO request) {

        CompOffRuleResponseDTO response = service.patchUpdate(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CompOffRuleResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rule updated successfully")
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
                        .message("Comp-off rule deactivated successfully")
                        .build()
        );
    }
}