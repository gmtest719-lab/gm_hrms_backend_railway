// ===== Interface =====
package com.gm.hrms.service;

import com.gm.hrms.dto.request.AttendanceReportFilterDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AttendanceExportService {

    void exportPdf(String reportType, AttendanceReportFilterDTO filter, HttpServletResponse response) throws Exception;

    void exportExcel(String reportType, AttendanceReportFilterDTO filter, HttpServletResponse response) throws Exception;
}