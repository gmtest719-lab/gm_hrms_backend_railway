package com.gm.hrms.controller;

import com.gm.hrms.dto.response.AttendanceFilterOptionsDTO;
import com.gm.hrms.dto.response.FilterOptionDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AttendanceFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance/filters")
@RequiredArgsConstructor
public class AttendanceFilterController {

    private final AttendanceFilterService filterService;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<AttendanceFilterOptionsDTO>> getAllOptions() {
        return ResponseEntity.ok(
                ApiResponse.<AttendanceFilterOptionsDTO>builder()
                        .success(true)
                        .message("Filter options fetched")
                        .data(filterService.getAllFilterOptions())
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/designations")
    public ResponseEntity<ApiResponse<List<FilterOptionDTO>>> getDesignations(
            @RequestParam Long departmentId) {
        return ResponseEntity.ok(
                ApiResponse.<List<FilterOptionDTO>>builder()
                        .success(true)
                        .message("Designations fetched")
                        .data(filterService.getDesignationsByDepartment(departmentId))
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<List<FilterOptionDTO>>> getEmployees(
            @RequestParam Long departmentId) {
        return ResponseEntity.ok(
                ApiResponse.<List<FilterOptionDTO>>builder()
                        .success(true)
                        .message("Employees fetched")
                        .data(filterService.getEmployeesByDepartment(departmentId))
                        .build());
    }
}