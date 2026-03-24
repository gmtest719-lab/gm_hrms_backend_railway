package com.gm.hrms.controller;

import com.gm.hrms.dto.request.CarryForwardRuleRequestDTO;
import com.gm.hrms.dto.response.CarryForwardRuleResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.CarryForwardRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carry-forward-rules")
@RequiredArgsConstructor
public class CarryForwardRuleController {

    private final CarryForwardRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<CarryForwardRuleResponseDTO>> create(
            @Valid @RequestBody CarryForwardRuleRequestDTO request) {

        CarryForwardRuleResponseDTO response = service.create(request);

        return ResponseEntity.ok(
                ApiResponse.<CarryForwardRuleResponseDTO>builder()
                        .success(true)
                        .message("Carry forward rule created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<CarryForwardRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        CarryForwardRuleResponseDTO response = service.getByPolicy(policyId);

        return ResponseEntity.ok(
                ApiResponse.<CarryForwardRuleResponseDTO>builder()
                        .success(true)
                        .message("Carry forward rule fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CarryForwardRuleResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody CarryForwardRuleRequestDTO request) {

        CarryForwardRuleResponseDTO response = service.patchUpdate(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CarryForwardRuleResponseDTO>builder()
                        .success(true)
                        .message("Carry forward rule updated successfully")
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
                        .message("Carry forward rule deleted successfully")
                        .build()
        );
    }
}