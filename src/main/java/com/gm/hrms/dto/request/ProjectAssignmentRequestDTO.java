package com.gm.hrms.dto.request;

import com.gm.hrms.enums.AssigneeType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectAssignmentRequestDTO {

    @NotNull
    private Long projectId;

    @NotNull
    private Long assigneeId;

    @NotNull
    private AssigneeType assigneeType;

    private String roleInProject;
}