package com.gm.hrms.controller;

import com.gm.hrms.dto.response.FilterOptionDTO;
import com.gm.hrms.dto.response.LeaveFilterOptionsDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveReportFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-reports/filters")
@RequiredArgsConstructor
public class LeaveReportFilterController {

    private final LeaveReportFilterService filterService;

    /**
     * Returns all available filter options.
     * ADMIN/HR get employees, departments, designations + leave types.
     * EMPLOYEE/TRAINEE/INTERN get leave types only (personal filters auto-applied).
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<LeaveFilterOptionsDTO>> getAllOptions() {
        return ResponseEntity.ok(
                ApiResponse.<LeaveFilterOptionsDTO>builder()
                        .success(true)
                        .message("Filter options fetched")
                        .data(filterService.getAllFilterOptions())
                        .build());
    }

    /**
     * Cascading filter: employees for a given department.
     * ADMIN/HR only.
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employees/by-department/{departmentId}")
    public ResponseEntity<ApiResponse<List<FilterOptionDTO>>> getEmployeesByDepartment(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(
                ApiResponse.<List<FilterOptionDTO>>builder()
                        .success(true)
                        .message("Employees fetched")
                        .data(filterService.getEmployeesByDepartment(departmentId))
                        .build());
    }
}