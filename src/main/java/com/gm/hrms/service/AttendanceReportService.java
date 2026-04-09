package com.gm.hrms.service;

import com.gm.hrms.dto.request.AttendanceReportFilterDTO;
import com.gm.hrms.dto.request.RegularizationRequestDTO;
import com.gm.hrms.dto.request.RegularizationReviewDTO;
import com.gm.hrms.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttendanceReportService {

    // ===== 8 REPORTS =====
    ReportResponseDTO<DailyAttendanceReportDTO>        getDailyReport(AttendanceReportFilterDTO filter, Pageable pageable);
    ReportResponseDTO<MonthlyAttendanceSummaryDTO>     getMonthlySummary(AttendanceReportFilterDTO filter, Pageable pageable);
    ReportResponseDTO<EmployeeAttendanceDetailDTO>     getEmployeeAttendance(AttendanceReportFilterDTO filter, Pageable pageable);
    ReportResponseDTO<AbsentReportDTO>                 getAbsentReport(AttendanceReportFilterDTO filter, Pageable pageable);
    ReportResponseDTO<LateComingReportDTO>             getLateComingReport(AttendanceReportFilterDTO filter, Pageable pageable);
    ReportResponseDTO<OvertimeReportDTO>               getOvertimeReport(AttendanceReportFilterDTO filter, Pageable pageable);
    ReportResponseDTO<RegularizationReportDTO>         getRegularizationReport(AttendanceReportFilterDTO filter, Pageable pageable);
    List<ShiftAttendanceReportDTO>                     getShiftWiseReport(AttendanceReportFilterDTO filter);

    // ===== REGULARIZATION CRUD =====
    RegularizationReportDTO submitRegularization(RegularizationRequestDTO dto);
    RegularizationReportDTO reviewRegularization(Long regularizationId, RegularizationReviewDTO dto);
}