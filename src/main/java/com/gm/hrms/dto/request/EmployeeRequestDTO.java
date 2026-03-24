package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class EmployeeRequestDTO {


    @NotNull(message = "Role is required")
    private RoleType role;

    @Valid
    private EmployeeEmploymentRequestDTO employment;

}