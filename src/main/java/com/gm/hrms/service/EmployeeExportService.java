package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeReportFilterDTO;
import com.gm.hrms.enums.RoleType;
import jakarta.servlet.http.HttpServletResponse;

public interface EmployeeExportService {

    void exportPdf(String reportType,
                   EmployeeReportFilterDTO filter,
                   RoleType viewerRole,
                   Long viewerPersonalId,
                   HttpServletResponse response) throws Exception;

    void exportExcel(String reportType,
                     EmployeeReportFilterDTO filter,
                     RoleType viewerRole,
                     Long viewerPersonalId,
                     HttpServletResponse response) throws Exception;
}