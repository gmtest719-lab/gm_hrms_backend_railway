package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectMasterReportDTO {
    private Long   projectId;
    private String projectName;
    private String projectCode;
    private String description;
    private String clientName;

    private LocalDate     startDate;
    private LocalDate     endDate;
    private ProjectStatus status;

    private long totalAssignees;
    private long employeeCount;
    private long traineeCount;
    private long internCount;

    private boolean delayed;
    private long    daysOverdue;
}