package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectCostReportDTO {
    private Long          projectId;
    private String        projectName;
    private String        projectCode;
    private ProjectStatus status;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private String        clientName;

    private Double budgetAmount;

    private Double actualCost;

    private Double variance;
    private Double variancePercent;

    private String costNote;
}