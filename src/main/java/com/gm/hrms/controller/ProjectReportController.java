// com/gm/hrms/controller/ProjectReportController.java
package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ProjectReportExportService;
import com.gm.hrms.service.ProjectReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-reports")
@RequiredArgsConstructor
public class ProjectReportController {

    private final ProjectReportService       reportService;
    private final ProjectReportExportService exportService;

    // ─── 1. PROJECT MASTER ───────────────────────────────────────────────────
    /**
     * ADMIN/HR: all projects with full details.
     * EMPLOYEE/TRAINEE/INTERN: only projects they are assigned to, limited fields.
     */
    @PostMapping("/master")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Project master report")
    public ResponseEntity<ApiResponse<ProjectReportResponseDTO<ProjectMasterReportDTO>>>
    getMasterReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @RequestParam(defaultValue = "0")             int page,
            @RequestParam(defaultValue = "20")            int size,
            @RequestParam(defaultValue = "startDate")     String sortBy,
            @RequestParam(defaultValue = "asc")           String sortDir,
            @AuthenticationPrincipal CustomUserDetails    user) {

        return ResponseEntity.ok(ApiResponse
                .<ProjectReportResponseDTO<ProjectMasterReportDTO>>builder()
                .success(true).message("Project master report fetched")
                .data(reportService.getProjectMasterReport(
                        safe(filter), buildPage(page, size, sortBy, sortDir), user))
                .build());
    }

    // ─── 2. PROJECT STATUS GROUP ─────────────────────────────────────────────
    /**
     * Projects grouped by status. All roles — filtered by assignment for non-admin.
     */
    @PostMapping("/status-group")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Project status group report")
    public ResponseEntity<ApiResponse<List<ProjectStatusGroupReportDTO>>>
    getStatusGroupReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(ApiResponse.<List<ProjectStatusGroupReportDTO>>builder()
                .success(true).message("Project status group report fetched")
                .data(reportService.getProjectStatusReport(safe(filter), user))
                .build());
    }

    // ─── 3. PROJECT TIMELINE ─────────────────────────────────────────────────
    /**
     * Start vs end date tracking, delay detection. All roles — filtered for non-admin.
     */
    @PostMapping("/timeline")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Project timeline report")
    public ResponseEntity<ApiResponse<ProjectReportResponseDTO<ProjectTimelineReportDTO>>>
    getTimelineReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "20")         int size,
            @RequestParam(defaultValue = "startDate")  String sortBy,
            @RequestParam(defaultValue = "asc")        String sortDir,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(ApiResponse
                .<ProjectReportResponseDTO<ProjectTimelineReportDTO>>builder()
                .success(true).message("Timeline report fetched")
                .data(reportService.getProjectTimelineReport(
                        safe(filter), buildPage(page, size, sortBy, sortDir), user))
                .build());
    }

    // ─── 4. RESOURCE ALLOCATION — ADMIN ONLY ─────────────────────────────────
    /**
     * Full view of who is assigned to every project.
     */
    @PostMapping("/resource-allocation")
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Resource allocation report")
    public ResponseEntity<ApiResponse<ProjectReportResponseDTO<ResourceAllocationReportDTO>>>
    getResourceAllocationReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "10")        int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc")       String sortDir) {

        return ResponseEntity.ok(ApiResponse
                .<ProjectReportResponseDTO<ResourceAllocationReportDTO>>builder()
                .success(true).message("Resource allocation report fetched")
                .data(reportService.getResourceAllocationReport(
                        safe(filter), buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // ─── 5. PROJECT-WISE EMPLOYEES — ADMIN/HR ────────────────────────────────
    /**
     * All people assigned to a single project.
     */
    @PostMapping("/project-wise/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Project-wise employee report")
    public ResponseEntity<ApiResponse<ResourceAllocationReportDTO>>
    getProjectWiseReport(
            @PathVariable Long projectId,
            @RequestBody(required = false) ProjectReportFilterDTO filter) {

        return ResponseEntity.ok(ApiResponse.<ResourceAllocationReportDTO>builder()
                .success(true).message("Project-wise employee report fetched")
                .data(reportService.getProjectWiseEmployeeReport(projectId, safe(filter)))
                .build());
    }

    // ─── 6. EMPLOYEE-WISE PROJECTS ───────────────────────────────────────────
    /**
     * ADMIN/HR: can pass any personalInformationId in filter.
     * EMPLOYEE/TRAINEE/INTERN: always sees only their own projects (self enforced in service).
     */
    @PostMapping("/employee-wise")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Employee-wise project report")
    public ResponseEntity<ApiResponse<ProjectReportResponseDTO<EmployeeProjectReportDTO>>>
    getEmployeeWiseReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @RequestParam(defaultValue = "0")              int page,
            @RequestParam(defaultValue = "20")             int size,
            @RequestParam(defaultValue = "projectName")    String sortBy,
            @RequestParam(defaultValue = "asc")            String sortDir,
            @AuthenticationPrincipal CustomUserDetails     user) {

        return ResponseEntity.ok(ApiResponse
                .<ProjectReportResponseDTO<EmployeeProjectReportDTO>>builder()
                .success(true).message("Employee-wise project report fetched")
                .data(reportService.getEmployeeWiseProjectReport(
                        safe(filter), buildPage(page, size, sortBy, sortDir), user))
                .build());
    }

    // ─── 7. PROJECT EFFORT ───────────────────────────────────────────────────
    /**
     * Hours per project. EMPLOYEE/TRAINEE/INTERN see only their own contribution.
     * Requires Timesheet module for totalHours to be populated.
     */
    @PostMapping("/effort")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Project effort report")
    public ResponseEntity<ApiResponse<ProjectReportResponseDTO<ProjectEffortReportDTO>>>
    getEffortReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "20")        int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc")       String sortDir,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(ApiResponse
                .<ProjectReportResponseDTO<ProjectEffortReportDTO>>builder()
                .success(true).message("Project effort report fetched")
                .data(reportService.getProjectEffortReport(
                        safe(filter), buildPage(page, size, sortBy, sortDir), user))
                .build());
    }

    // ─── 8. PROJECT COST — ADMIN ONLY ────────────────────────────────────────
    /**
     * Budget vs actual cost. Requires budgetAmount + actualCost fields in Project entity.
     */
    @PostMapping("/cost")
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "ProjectReport",
            description = "Project cost report")
    public ResponseEntity<ApiResponse<ProjectReportResponseDTO<ProjectCostReportDTO>>>
    getCostReport(
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "20")        int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc")       String sortDir) {

        return ResponseEntity.ok(ApiResponse
                .<ProjectReportResponseDTO<ProjectCostReportDTO>>builder()
                .success(true).message("Project cost report fetched")
                .data(reportService.getProjectCostReport(
                        safe(filter), buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // ─── EXPORT PDF ──────────────────────────────────────────────────────────
    /**
     * Types: master | timeline | resource-allocation | employee-wise | effort | cost
     * EMPLOYEE/TRAINEE/INTERN: role-filtered export (same rules as report endpoints).
     * resource-allocation and cost types are ADMIN-only (enforced in export service).
     */
    @PostMapping("/export/pdf/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    public void exportPdf(
            @PathVariable String type,
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response) throws Exception {

        exportService.exportPdf(type, safe(filter), user, response);
    }

    // ─── EXPORT EXCEL ────────────────────────────────────────────────────────
    @PostMapping("/export/excel/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    public void exportExcel(
            @PathVariable String type,
            @RequestBody(required = false) ProjectReportFilterDTO filter,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response) throws Exception {

        exportService.exportExcel(type, safe(filter), user, response);
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    private ProjectReportFilterDTO safe(ProjectReportFilterDTO f) {
        return f != null ? f : new ProjectReportFilterDTO();
    }

    private PageRequest buildPage(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}