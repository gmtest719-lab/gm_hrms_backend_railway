package com.gm.hrms.dto.request;

import com.gm.hrms.enums.Status;
import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkProfileRequestDTO {

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotNull(message = "Designation is required")
    private Long designationId;

    @NotNull(message = "Branch is required")
    private Long branchId;

    @NotNull(message = "Shift is required")
    private Long shiftId;

    // Optional
    private Long reportingManagerProfileId;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @NotNull(message = "Working type is required")
    private WorkingType workingType;

    @NotNull(message = "Status is required")
    private Status status;
}