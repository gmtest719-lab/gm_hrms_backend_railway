package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface LeaveReportExportService {
    void exportPdf  (String reportType, LeaveReportFilterDTO filter, HttpServletResponse response) throws Exception;
    void exportExcel(String reportType, LeaveReportFilterDTO filter, HttpServletResponse response) throws Exception;
}