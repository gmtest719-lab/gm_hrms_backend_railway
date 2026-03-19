package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService service;

    @PostMapping
    public ResponseEntity<?> apply(@RequestBody @Valid LeaveRequestDTO dto) {
        return ResponseEntity.ok(service.apply(dto));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam Long approverId) {
        service.approve(id, approverId);
        return ResponseEntity.ok("Approved");
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestParam String reason) {
        service.reject(id, reason);
        return ResponseEntity.ok("Rejected");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.ok("Cancelled");
    }

    @GetMapping("/{personalId}")
    public ResponseEntity<?> getMyLeaves(@PathVariable Long personalId) {
        return ResponseEntity.ok(service.getMyLeaves(personalId));
    }

    @PatchMapping("/{id}/request-document")
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
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