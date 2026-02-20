package com.gm.hrms.controller;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService service;

    // ================= EMPLOYEE: CREATE (DRAFT) =================
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody TimesheetRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet saved as draft")
                        .data(service.create(user.getEmployeeId(), dto))
                        .build()
        );
    }

    // ================= EMPLOYEE: SUBMIT =================
    @PatchMapping("/submit/{timesheetId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> submit(
            @PathVariable Long timesheetId,
            @AuthenticationPrincipal CustomUserDetails user){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet submitted")
                        .data(service.submit(timesheetId, user.getEmployeeId()))
                        .build()
        );
    }

    // ================= ADMIN / HR =================
    @PatchMapping("/approve/{timesheetId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> approve(@PathVariable Long timesheetId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet approved")
                        .data(service.approve(timesheetId))
                        .build()
        );
    }

    @PatchMapping("/reject/{timesheetId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> reject(@PathVariable Long timesheetId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet rejected")
                        .data(service.reject(timesheetId))
                        .build()
        );
    }

    // ================= EMPLOYEE: MY TIMESHEETS =================
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> getMy(
            @AuthenticationPrincipal CustomUserDetails user){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(service.getByEmployee(user.getEmployeeId()))
                        .build()
        );
    }

    // ================= ADMIN / HR =================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(service.getAll())
                        .build()
        );
    }
}