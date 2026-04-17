package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeProjectReportDTO {
    private Long          assignmentId;
    private Long          projectId;
    private String        projectName;
    private String        projectCode;
    private String        description;
    private String        clientName;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private ProjectStatus projectStatus;
    private String        roleInProject;
    private AssigneeType  assigneeType;
    private boolean       delayed;
    private long          daysOverdue;
}