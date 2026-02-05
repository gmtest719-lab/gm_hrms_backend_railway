package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequestDTO {

    @NotBlank(message = "Department name is required")
    private String name;
}
