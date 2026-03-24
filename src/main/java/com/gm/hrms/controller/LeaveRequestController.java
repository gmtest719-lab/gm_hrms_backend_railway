package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService service;

    // ================= APPLY =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> apply(
            @Valid @RequestBody LeaveRequestDTO dto) {

        LeaveRequestResponseDTO response = service.apply(dto);

        return ResponseEntity.ok(
                ApiResponse.<LeaveRequestResponseDTO>builder()
                        .success(true)
                        .message("Leave applied successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= APPROVE =================
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(
            @PathVariable Long id,
            @RequestParam Long approverId) {

        service.approve(id, approverId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave approved successfully")
                        .build()
        );
    }

    // ================= REJECT =================
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long id,
            @RequestParam String reason) {

        service.reject(id, reason);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave rejected successfully")
                        .build()
        );
    }

    // ================= CANCEL =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {

        service.cancel(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave cancelled successfully")
                        .build()
        );
    }

    // ================= GET MY LEAVES =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @GetMapping("/{personalId}")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponseDTO>>> getMyLeaves(
            @PathVariable Long personalId) {

        List<LeaveRequestResponseDTO> response = service.getMyLeaves(personalId);

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveRequestResponseDTO>>builder()
                        .success(true)
                        .message("Leave requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= REQUEST DOCUMENT =================
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}/request-document")
    public ResponseEntity<ApiResponse<Void>> requestDocument(@PathVariable Long id) {

        service.requestDocument(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document requested successfully")
                        .build()
        );
    }
}