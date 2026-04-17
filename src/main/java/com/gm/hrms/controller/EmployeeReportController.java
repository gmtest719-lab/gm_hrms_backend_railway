package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.EmployeeReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.EmployeeExportService;
import com.gm.hrms.service.EmployeeReportFilterService;
import com.gm.hrms.service.EmployeeReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-reports")
@RequiredArgsConstructor
public class EmployeeReportController {

    private final EmployeeReportService       reportService;
    private final EmployeeExportService       exportService;
    private final EmployeeReportFilterService filterService;

    // ══════════════════════════════════════════════════════════
    // 1. MASTER REPORT  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/master")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee master report")
    public ResponseEntity<ApiResponse<EmployeeReportResponseDTO<EmployeeMasterReportDTO>>> getMasterReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter,
            @RequestParam(defaultValue = "0")           int    page,
            @RequestParam(defaultValue = "20")          int    size,
            @RequestParam(defaultValue = "firstName")   String sortBy,
            @RequestParam(defaultValue = "asc")         String sortDir) {

        return ResponseEntity.ok(ApiResponse.<EmployeeReportResponseDTO<EmployeeMasterReportDTO>>builder()
                .success(true)
                .message("Employee master report fetched")
                .data(reportService.getMasterReport(
                        defaultFilter(filter),
                        buildPage(page, size, sortBy, sortDir),
                        resolveRole(),
                        resolvePersonalId()))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 2. SELF MASTER REPORT  —  ALL ROLES (own data only)
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/master/self")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee self master report")
    public ResponseEntity<ApiResponse<EmployeeMasterReportDTO>> getSelfMasterReport() {

        Long   viewerPersonalId = resolvePersonalId();
        RoleType viewerRole     = resolveRole();

        return ResponseEntity.ok(ApiResponse.<EmployeeMasterReportDTO>builder()
                .success(true)
                .message("Self master report fetched")
                .data(reportService.getSelfMasterReport(viewerPersonalId, viewerRole))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 3. DEPARTMENT-WISE  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/department-wise")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Department-wise employee report")
    public ResponseEntity<ApiResponse<List<DepartmentWiseEmployeeReportDTO>>> getDepartmentWiseReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter) {

        return ResponseEntity.ok(ApiResponse.<List<DepartmentWiseEmployeeReportDTO>>builder()
                .success(true)
                .message("Department-wise report fetched")
                .data(reportService.getDepartmentWiseReport(defaultFilter(filter)))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 4. DESIGNATION-WISE  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/designation-wise")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Designation-wise employee report")
    public ResponseEntity<ApiResponse<List<DesignationWiseEmployeeReportDTO>>> getDesignationWiseReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter) {

        return ResponseEntity.ok(ApiResponse.<List<DesignationWiseEmployeeReportDTO>>builder()
                .success(true)
                .message("Designation-wise report fetched")
                .data(reportService.getDesignationWiseReport(defaultFilter(filter)))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 5. JOINING REPORT  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/joining")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee joining report")
    public ResponseEntity<ApiResponse<EmployeeReportResponseDTO<EmployeeJoiningReportDTO>>> getJoiningReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter,
            @RequestParam(defaultValue = "0")            int    page,
            @RequestParam(defaultValue = "20")           int    size,
            @RequestParam(defaultValue = "dateOfJoining") String sortBy,
            @RequestParam(defaultValue = "desc")         String sortDir) {

        return ResponseEntity.ok(ApiResponse.<EmployeeReportResponseDTO<EmployeeJoiningReportDTO>>builder()
                .success(true)
                .message("Joining report fetched")
                .data(reportService.getJoiningReport(
                        defaultFilter(filter),
                        buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 6. EXIT / ATTRITION REPORT  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/exit")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee exit / attrition report")
    public ResponseEntity<ApiResponse<EmployeeReportResponseDTO<EmployeeExitReportDTO>>> getExitReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter,
            @RequestParam(defaultValue = "0")         int    page,
            @RequestParam(defaultValue = "20")        int    size,
            @RequestParam(defaultValue = "exitDate")  String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDir) {

        return ResponseEntity.ok(ApiResponse.<EmployeeReportResponseDTO<EmployeeExitReportDTO>>builder()
                .success(true)
                .message("Exit report fetched")
                .data(reportService.getExitReport(
                        defaultFilter(filter),
                        buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 7. STATUS REPORT  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/status")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee status report")
    public ResponseEntity<ApiResponse<EmployeeReportResponseDTO<EmployeeStatusReportDTO>>> getStatusReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter,
            @RequestParam(defaultValue = "0")           int    page,
            @RequestParam(defaultValue = "20")          int    size,
            @RequestParam(defaultValue = "firstName")   String sortBy,
            @RequestParam(defaultValue = "asc")         String sortDir) {

        return ResponseEntity.ok(ApiResponse.<EmployeeReportResponseDTO<EmployeeStatusReportDTO>>builder()
                .success(true)
                .message("Status report fetched")
                .data(reportService.getStatusReport(
                        defaultFilter(filter),
                        buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 8. DIVERSITY REPORT  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/diversity")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee diversity report")
    public ResponseEntity<ApiResponse<DiversityReportDTO>> getDiversityReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter) {

        return ResponseEntity.ok(ApiResponse.<DiversityReportDTO>builder()
                .success(true)
                .message("Diversity report fetched")
                .data(reportService.getDiversityReport(defaultFilter(filter)))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 9. DIRECTORY REPORT  —  ALL ROLES
    //    (contacts masked for EMPLOYEE / TRAINEE / INTERN)
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/directory")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "EmployeeReport",
            description = "Employee directory report")
    public ResponseEntity<ApiResponse<EmployeeReportResponseDTO<EmployeeDirectoryDTO>>> getDirectoryReport(
            @RequestBody(required = false) EmployeeReportFilterDTO filter,
            @RequestParam(defaultValue = "0")         int    page,
            @RequestParam(defaultValue = "20")        int    size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc")       String sortDir) {

        return ResponseEntity.ok(ApiResponse.<EmployeeReportResponseDTO<EmployeeDirectoryDTO>>builder()
                .success(true)
                .message("Directory report fetched")
                .data(reportService.getDirectoryReport(
                        defaultFilter(filter),
                        buildPage(page, size, sortBy, sortDir),
                        resolveRole()))
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 10. FILTER OPTIONS  —  ADMIN / HR only
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/filters")
    public ResponseEntity<ApiResponse<EmployeeFilterOptionsDTO>> getFilterOptions() {

        return ResponseEntity.ok(ApiResponse.<EmployeeFilterOptionsDTO>builder()
                .success(true)
                .message("Filter options fetched")
                .data(filterService.getAllFilterOptions())
                .build());
    }

    // ══════════════════════════════════════════════════════════
    // 11. EXPORT — PDF
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/export/pdf/{type}")
    public void exportPdf(
            @PathVariable                          String                  type,
            @RequestBody(required = false)         EmployeeReportFilterDTO filter,
            HttpServletResponse                                            response) throws Exception {

        validateExportAccess(type, resolveRole());
        exportService.exportPdf(
                type,
                defaultFilter(filter),
                resolveRole(),
                resolvePersonalId(),
                response);
    }

    // ══════════════════════════════════════════════════════════
    // 12. EXPORT — EXCEL
    // ══════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/export/excel/{type}")
    public void exportExcel(
            @PathVariable                          String                  type,
            @RequestBody(required = false)         EmployeeReportFilterDTO filter,
            HttpServletResponse                                            response) throws Exception {

        validateExportAccess(type, resolveRole());
        exportService.exportExcel(
                type,
                defaultFilter(filter),
                resolveRole(),
                resolvePersonalId(),
                response);
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    /** Return a safe non-null filter. */
    private EmployeeReportFilterDTO defaultFilter(EmployeeReportFilterDTO filter) {
        return filter != null ? filter : new EmployeeReportFilterDTO();
    }

    /** Build a Pageable with sort. */
    private PageRequest buildPage(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    private RoleType resolveRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new InvalidRequestException("Authentication context is missing");
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .map(name -> {
                    try { return RoleType.valueOf(name); }
                    catch (IllegalArgumentException ex) { return null; }
                })
                .filter(r -> r != null)
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Unable to resolve user role"));
    }

    private Long resolvePersonalId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new InvalidRequestException("Authentication context is missing");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            try { return Long.parseLong(ud.getUsername()); }
            catch (NumberFormatException ignored) { return 0L; }
        }
        return 0L;
    }

    private void validateExportAccess(String reportType, RoleType role) {
        boolean isRestricted = (role == RoleType.EMPLOYEE
                || role == RoleType.TRAINEE
                || role == RoleType.INTERN);

        if (isRestricted && !"directory".equalsIgnoreCase(reportType)) {
            throw new InvalidRequestException(
                    "You are not authorised to export the '" + reportType + "' report.");
        }
    }
}