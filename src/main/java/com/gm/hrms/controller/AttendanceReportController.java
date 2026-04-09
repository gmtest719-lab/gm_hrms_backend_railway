package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.AttendanceReportFilterDTO;
import com.gm.hrms.dto.request.RegularizationRequestDTO;
import com.gm.hrms.dto.request.RegularizationReviewDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AttendanceExportService;
import com.gm.hrms.service.AttendanceReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance/reports")
@RequiredArgsConstructor
public class AttendanceReportController {

    private final AttendanceReportService reportService;
    private final AttendanceExportService exportService;

    // DAILY ATTENDANCE REPORT
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/daily")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Daily attendance report")
    public ResponseEntity<ApiResponse<ReportResponseDTO<DailyAttendanceReportDTO>>> getDailyReport(
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "asc")  String sortDir) {

        filter = defaultFilter(filter);
        PageRequest pr = buildPage(page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<DailyAttendanceReportDTO>>builder()
                .success(true).message("Daily attendance report fetched")
                .data(reportService.getDailyReport(filter, pr)).build());
    }

    // MONTHLY SUMMARY REPORT
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/monthly-summary")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Monthly attendance summary")
    public ResponseEntity<ApiResponse<ReportResponseDTO<MonthlyAttendanceSummaryDTO>>> getMonthlySummary(
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "employeeName") String sortBy,
            @RequestParam(defaultValue = "asc")  String sortDir) {

        filter = defaultFilter(filter);
        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<MonthlyAttendanceSummaryDTO>>builder()
                .success(true).message("Monthly summary fetched")
                .data(reportService.getMonthlySummary(filter, buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // EMPLOYEE-WISE ATTENDANCE DETAIL
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/employee/{personalInformationId}")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Employee-wise attendance detail")
    public ResponseEntity<ApiResponse<ReportResponseDTO<EmployeeAttendanceDetailDTO>>> getEmployeeAttendance(
            @PathVariable Long personalInformationId,
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        filter = defaultFilter(filter);
        filter.setPersonalInformationId(personalInformationId);
        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<EmployeeAttendanceDetailDTO>>builder()
                .success(true).message("Employee attendance fetched")
                .data(reportService.getEmployeeAttendance(filter, buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // ABSENT REPORT
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/absent")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Absent report")
    public ResponseEntity<ApiResponse<ReportResponseDTO<AbsentReportDTO>>> getAbsentReport(
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "employeeName") String sortBy,
            @RequestParam(defaultValue = "asc")  String sortDir) {

        filter = defaultFilter(filter);
        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<AbsentReportDTO>>builder()
                .success(true).message("Absent report fetched")
                .data(reportService.getAbsentReport(filter, buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // LATE COMING REPORT
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/late-coming")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Late coming report")
    public ResponseEntity<ApiResponse<ReportResponseDTO<LateComingReportDTO>>> getLateComingReport(
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        filter = defaultFilter(filter);
        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<LateComingReportDTO>>builder()
                .success(true).message("Late coming report fetched")
                .data(reportService.getLateComingReport(filter, buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // OVERTIME REPORT
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/overtime")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Overtime report")
    public ResponseEntity<ApiResponse<ReportResponseDTO<OvertimeReportDTO>>> getOvertimeReport(
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        filter = defaultFilter(filter);
        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<OvertimeReportDTO>>builder()
                .success(true).message("Overtime report fetched")
                .data(reportService.getOvertimeReport(filter, buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // REGULARIZATION REPORT
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/regularization")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Regularization report")
    public ResponseEntity<ApiResponse<ReportResponseDTO<RegularizationReportDTO>>> getRegularizationReport(
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        filter = defaultFilter(filter);
        return ResponseEntity.ok(ApiResponse.<ReportResponseDTO<RegularizationReportDTO>>builder()
                .success(true).message("Regularization report fetched")
                .data(reportService.getRegularizationReport(filter, buildPage(page, size, sortBy, sortDir)))
                .build());
    }

    // SHIFT-WISE REPORT  — ADMIN/HR only
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/shift-wise")
    @Auditable(action = AuditAction.VIEW_REPORT, resource = "AttendanceReport",
            description = "Shift-wise attendance report")
    public ResponseEntity<ApiResponse<List<ShiftAttendanceReportDTO>>> getShiftWiseReport(
            @RequestBody(required = false) AttendanceReportFilterDTO filter) {

        filter = defaultFilter(filter);
        return ResponseEntity.ok(ApiResponse.<List<ShiftAttendanceReportDTO>>builder()
                .success(true).message("Shift-wise report fetched")
                .data(reportService.getShiftWiseReport(filter)).build());
    }

    // REGULARIZATION — SUBMIT
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/regularization/submit")
    @Auditable(action = AuditAction.CORRECT_ATTENDANCE, resource = "Regularization",
            description = "Submit attendance regularization request")
    public ResponseEntity<ApiResponse<RegularizationReportDTO>> submitRegularization(
            @RequestBody RegularizationRequestDTO dto) {

        return ResponseEntity.ok(ApiResponse.<RegularizationReportDTO>builder()
                .success(true).message("Regularization request submitted")
                .data(reportService.submitRegularization(dto)).build());
    }

    // REGULARIZATION — REVIEW
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/regularization/{id}/review")
    @Auditable(action = AuditAction.CORRECT_ATTENDANCE, resource = "Regularization",
            description = "Review attendance regularization request")
    public ResponseEntity<ApiResponse<RegularizationReportDTO>> reviewRegularization(
            @PathVariable Long id,
            @RequestBody RegularizationReviewDTO dto) {

        return ResponseEntity.ok(ApiResponse.<RegularizationReportDTO>builder()
                .success(true).message("Regularization reviewed")
                .data(reportService.reviewRegularization(id, dto)).build());
    }

    // EXPORT — PDF
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/export/pdf/{type}")
    public void exportPdf(
            @PathVariable String type,
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            HttpServletResponse response) throws Exception {

        exportService.exportPdf(type, defaultFilter(filter), response);
    }

    // EXPORT — EXCEL
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/export/excel/{type}")
    public void exportExcel(
            @PathVariable String type,
            @RequestBody(required = false) AttendanceReportFilterDTO filter,
            HttpServletResponse response) throws Exception {

        exportService.exportExcel(type, defaultFilter(filter), response);
    }

    // HELPERS
    private AttendanceReportFilterDTO defaultFilter(AttendanceReportFilterDTO filter) {
        return filter != null ? filter : new AttendanceReportFilterDTO();
    }

    private PageRequest buildPage(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}