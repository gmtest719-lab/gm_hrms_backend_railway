package com.gm.hrms.dto.request;

import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectReportFilterDTO {

    private LocalDate fromDate;
    private LocalDate toDate;

    private Long projectId;

    private Long personalInformationId;

    private Long departmentId;

    private ProjectStatus status;

    private AssigneeType assigneeType;
}