package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.enums.RoleType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeReportService {

    // ── ADMIN / HR ────────────────────────────────────────────

    /** Complete employee master report (all employees). */
    EmployeeReportResponseDTO<EmployeeMasterReportDTO> getMasterReport(
            EmployeeReportFilterDTO filter, Pageable pageable, RoleType viewerRole, Long viewerPersonalId);

    /** Employees grouped by department. */
    List<DepartmentWiseEmployeeReportDTO> getDepartmentWiseReport(
            EmployeeReportFilterDTO filter);

    /** Employees grouped by designation. */
    List<DesignationWiseEmployeeReportDTO> getDesignationWiseReport(
            EmployeeReportFilterDTO filter);

    /** New joiners within a date range. */
    EmployeeReportResponseDTO<EmployeeJoiningReportDTO> getJoiningReport(
            EmployeeReportFilterDTO filter, Pageable pageable);

    /** Exited / terminated employees. */
    EmployeeReportResponseDTO<EmployeeExitReportDTO> getExitReport(
            EmployeeReportFilterDTO filter, Pageable pageable);

    /** Active / Inactive employee status. */
    EmployeeReportResponseDTO<EmployeeStatusReportDTO> getStatusReport(
            EmployeeReportFilterDTO filter, Pageable pageable);

    /** Gender & demographic diversity report. */
    DiversityReportDTO getDiversityReport(EmployeeReportFilterDTO filter);

    // ── ALL ROLES (restricted view) ───────────────────────────

    /** Employee directory — limited fields; contacts masked for non-admin. */
    EmployeeReportResponseDTO<EmployeeDirectoryDTO> getDirectoryReport(
            EmployeeReportFilterDTO filter, Pageable pageable, RoleType viewerRole);

    /** Self master report — only own data. */
    EmployeeMasterReportDTO getSelfMasterReport(Long viewerPersonalId, RoleType viewerRole);
}