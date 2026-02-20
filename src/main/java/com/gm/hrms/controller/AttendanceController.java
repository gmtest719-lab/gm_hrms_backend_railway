package com.gm.hrms.controller;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ================= EMPLOYEE =================

    @PostMapping("/clock-in")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> clockIn(@AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Clock-in successful")
                        .data(attendanceService.clockIn(user.getEmployeeId()))
                        .build()
        );
    }

    @PostMapping("/clock-out")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> clockOut(@AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Clock-out successful")
                        .data(attendanceService.clockOut(user.getEmployeeId()))
                        .build()
        );
    }

    @PostMapping("/break-in")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> breakIn(@AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Break-in recorded")
                        .data(attendanceService.breakIn(user.getEmployeeId()))
                        .build()
        );
    }

    @PostMapping("/break-out")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> breakOut(@AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Break-out recorded")
                        .data(attendanceService.breakOut(user.getEmployeeId()))
                        .build()
        );
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> myAttendance(@AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("My attendance fetched")
                        .data(attendanceService.getByEmployee(user.getEmployeeId()))
                        .build()
        );
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> today(@AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Today's attendance")
                        .data(attendanceService.getToday(user.getEmployeeId()))
                        .build()
        );
    }

    // ================= HR & ADMIN =================

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<?>> getByEmployee(@PathVariable Long employeeId) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee attendance fetched")
                        .data(attendanceService.getByEmployee(employeeId))
                        .build()
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("All attendance records")
                        .data(attendanceService.getAll())
                        .build()
        );
    }



    // ================= ADMIN =================

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Attendance report")
                        .data(attendanceService.getByDateRange(start, end))
                        .build()
        );
    }

}