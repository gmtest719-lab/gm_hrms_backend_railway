// com/gm/hrms/service/ProjectReportService.java
package com.gm.hrms.service;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectReportFilterDTO;
import com.gm.hrms.dto.response.*;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface ProjectReportService {

    ProjectReportResponseDTO<ProjectMasterReportDTO> getProjectMasterReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user);

    List<ProjectStatusGroupReportDTO> getProjectStatusReport(
            ProjectReportFilterDTO filter, CustomUserDetails user);

    ProjectReportResponseDTO<ProjectTimelineReportDTO> getProjectTimelineReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user);

    ProjectReportResponseDTO<ResourceAllocationReportDTO> getResourceAllocationReport(
            ProjectReportFilterDTO filter, Pageable pageable);

    ResourceAllocationReportDTO getProjectWiseEmployeeReport(
            Long projectId, ProjectReportFilterDTO filter);

    ProjectReportResponseDTO<EmployeeProjectReportDTO> getEmployeeWiseProjectReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user);

    ProjectReportResponseDTO<ProjectEffortReportDTO> getProjectEffortReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user);

    ProjectReportResponseDTO<ProjectCostReportDTO> getProjectCostReport(
            ProjectReportFilterDTO filter, Pageable pageable);
}