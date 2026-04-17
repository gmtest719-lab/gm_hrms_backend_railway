package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AssigneeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectAssigneeDTO {
    private Long         assignmentId;
    private Long         assigneeId;
    private Long         personalInformationId;
    private String       assigneeName;
    private String       assigneeCode;
    private AssigneeType assigneeType;
    private String       roleInProject;
    private String       department;
    private String       designation;
}