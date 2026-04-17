package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectTimelineReportDTO {
    private Long          projectId;
    private String        projectName;
    private String        projectCode;
    private String        clientName;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private ProjectStatus status;

    private long durationDays;
    private long elapsedDays;
    private int  progressPercent;

    private boolean delayed;
    private long    daysOverdue;
    private long    daysRemaining;
}