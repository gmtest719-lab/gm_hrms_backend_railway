// com/gm/hrms/dto/response/ProjectEffortReportDTO.java
package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class ProjectEffortReportDTO {
    private Long          projectId;
    private String        projectName;
    private String        projectCode;
    private ProjectStatus projectStatus;
    private LocalDate     startDate;
    private LocalDate     endDate;

    private long totalAssignees;

    private Long         assigneePersonalInformationId;
    private String       assigneeName;
    private String       assigneeCode;
    private AssigneeType assigneeType;

    private Double totalHours;

    private String timesheetNote;
}