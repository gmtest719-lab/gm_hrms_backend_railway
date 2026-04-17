package com.gm.hrms.service;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectReportFilterDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface ProjectReportExportService {

    void exportPdf(String type,
                   ProjectReportFilterDTO filter,
                   CustomUserDetails user,
                   HttpServletResponse response) throws Exception;

    void exportExcel(String type,
                     ProjectReportFilterDTO filter,
                     CustomUserDetails user,
                     HttpServletResponse response) throws Exception;
}