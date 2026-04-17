package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AssigneeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectAssignmentResponseDTO {

    private Long id;
    private String roleInProject;
    private AssigneeType assigneeType;

    // Project info
    private Long projectId;
    private String projectName;
    private String projectCode;

    private Long assigneeId;
    private String assigneeName;
    private String assigneeCode;
}