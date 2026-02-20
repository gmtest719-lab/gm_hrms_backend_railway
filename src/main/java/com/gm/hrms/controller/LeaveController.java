package com.gm.hrms.controller;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.LeaveApplyRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // ================= APPLY LEAVE =================
    @PostMapping("/apply")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> apply(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody LeaveApplyRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave applied successfully")
                        .data(leaveService.applyLeave(user.getEmployeeId(), dto))
                        .build()
        );
    }

    // ================= GET MY LEAVES =================
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> getMyLeaves(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("My leaves")
                        .data(leaveService.getByEmployee(user.getEmployeeId()))
                        .build()
        );
    }

    // ================= CANCEL LEAVE =================
    @PatchMapping("/cancel/{leaveId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> cancel(
            @PathVariable Long leaveId,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave cancelled")
                        .data(leaveService.cancel(leaveId, user.getEmployeeId()))
                        .build()
        );
    }

    // ================= ADMIN: APPROVE =================
    @PatchMapping("/approve/{leaveId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> approve(@PathVariable Long leaveId) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave approved")
                        .data(leaveService.approve(leaveId))
                        .build()
        );
    }

    // ================= ADMIN: REJECT =================
    @PatchMapping("/reject/{leaveId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> reject(@PathVariable Long leaveId) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave rejected")
                        .data(leaveService.reject(leaveId))
                        .build()
        );
    }

    // ================= ADMIN: GET ALL =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("All leaves")
                        .data(leaveService.getAll())
                        .build()
        );
    }
}