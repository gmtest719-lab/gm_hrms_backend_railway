package com.gm.hrms.controller;

import com.gm.hrms.dto.request.CompOffRequestDTO;
import com.gm.hrms.dto.response.CompOffResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.CompOffRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comp-off-requests")
@RequiredArgsConstructor
public class CompOffRequestController {

    private final CompOffRequestService service;

    // ================= APPLY =================
    @PostMapping
    public ResponseEntity<ApiResponse<CompOffResponseDTO>> apply(
            @RequestBody CompOffRequestDTO request) {

        CompOffResponseDTO response = service.apply(request);

        return ResponseEntity.ok(
                ApiResponse.<CompOffResponseDTO>builder()
                        .success(true)
                        .message("Comp-off applied successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= APPROVE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<CompOffResponseDTO>> approve(
            @PathVariable Long id,
            @RequestParam Long approverId) {

        CompOffResponseDTO response = service.approve(id, approverId);

        return ResponseEntity.ok(
                ApiResponse.<CompOffResponseDTO>builder()
                        .success(true)
                        .message("Comp-off approved successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= REJECT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<CompOffResponseDTO>> reject(
            @PathVariable Long id,
            @RequestParam Long approverId) {

        CompOffResponseDTO response = service.reject(id, approverId);

        return ResponseEntity.ok(
                ApiResponse.<CompOffResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rejected successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET USER REQUESTS =================
    @GetMapping("/user/{personalId}")
    public ResponseEntity<ApiResponse<List<CompOffResponseDTO>>> getByUser(
            @PathVariable Long personalId) {

        List<CompOffResponseDTO> response = service.getByUser(personalId);

        return ResponseEntity.ok(
                ApiResponse.<List<CompOffResponseDTO>>builder()
                        .success(true)
                        .message("Comp-off requests fetched successfully")
                        .data(response)
                        .build()
        );
    }
}