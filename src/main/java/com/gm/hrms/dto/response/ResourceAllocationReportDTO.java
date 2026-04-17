package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ResourceAllocationReportDTO {
    private Long          projectId;
    private String        projectName;
    private String        projectCode;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private ProjectStatus projectStatus;

    private int totalAssignees;
    private int employeeCount;
    private int traineeCount;
    private int internCount;

    private List<ProjectAssigneeDTO> assignees;
}