package com.gm.hrms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReportSummaryDTO {
    private long totalProjects;
    private long notStarted;
    private long inProgress;
    private long completed;
    private long onHold;
    private long cancelled;
    private long delayed;
    private long totalAssignees;
}