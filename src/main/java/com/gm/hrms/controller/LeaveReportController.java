package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveReportExportService;
import com.gm.hrms.service.LeaveReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-reports")
@RequiredArgsConstructor
public class LeaveReportController {

    private final LeaveReportService       reportService;
    private final LeaveReportExportService exportService;

    // ── 1. LEAVE BALANCE REPORT ──────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/balance")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave balance report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveBalanceReportDTO>>> getBalanceReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "leaveType") String sortBy,
            @RequestParam(defaultValue = "asc")       String sortDir) {

        return ok("Leave balance report fetched",
                reportService.getBalanceReport(safeFilter(filter), buildPage(page, size, sortBy, sortDir)));
    }

    // ── 2. LEAVE HISTORY REPORT ─────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/history")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave history report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveHistoryReportDTO>>> getHistoryReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDir) {

        return ok("Leave history report fetched",
                reportService.getHistoryReport(safeFilter(filter), buildPage(page, size, sortBy, sortDir)));
    }

    // ── 3. LEAVE REQUEST REPORT ─────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/requests")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave request report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveRequestReportDTO>>> getRequestReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDir) {

        return ok("Leave request report fetched",
                reportService.getRequestReport(safeFilter(filter), buildPage(page, size, sortBy, sortDir)));
    }

    // ── 4. LEAVE TYPE USAGE REPORT ──────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/type-usage")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave type usage report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveTypeUsageReportDTO>>> getTypeUsageReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ok("Leave type usage report fetched",
                reportService.getTypeUsageReport(safeFilter(filter), PageRequest.of(page, size)));
    }

    // ── 5. LEAVE TRENDS REPORT ──────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/trends")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave trends report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveTrendsReportDTO>>> getTrendsReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ok("Leave trends report fetched",
                reportService.getTrendsReport(safeFilter(filter), PageRequest.of(page, size)));
    }

    // ── 6. LEAVE ENCASHMENT REPORT ──────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/encashment")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave encashment report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveEncashmentReportDTO>>> getEncashmentReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc")            String sortDir) {

        return ok("Leave encashment report fetched",
                reportService.getEncashmentReport(safeFilter(filter), buildPage(page, size, sortBy, sortDir)));
    }

    // ── 7. LEAVE APPROVAL REPORT ────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/approvals")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "LeaveReport",
            description = "Leave approval report")
    public ResponseEntity<ApiResponse<LeaveReportResponseDTO<LeaveApprovalReportDTO>>> getApprovalReport(
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDir) {

        return ok("Leave approval report fetched",
                reportService.getApprovalReport(safeFilter(filter), buildPage(page, size, sortBy, sortDir)));
    }

    // ── EXPORT: PDF ─────────────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/export/pdf/{reportType}")
    public void exportPdf(
            @PathVariable String reportType,
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            HttpServletResponse response) throws Exception {

        exportService.exportPdf(reportType, safeFilter(filter), response);
    }

    // ── EXPORT: EXCEL ───────────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/export/excel/{reportType}")
    public void exportExcel(
            @PathVariable String reportType,
            @RequestBody(required = false) LeaveReportFilterDTO filter,
            HttpServletResponse response) throws Exception {

        exportService.exportExcel(reportType, safeFilter(filter), response);
    }

    // ── helpers ──────────────────────────────────────────────────────
    private <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .build());
    }

    private LeaveReportFilterDTO safeFilter(LeaveReportFilterDTO f) {
        return f != null ? f : new LeaveReportFilterDTO();
    }

    private PageRequest buildPage(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}