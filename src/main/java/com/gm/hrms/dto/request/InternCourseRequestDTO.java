package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InternCourseRequestDTO {

    @NotBlank(message = "Course name is required")
    private String name;

    private String description;

    @NotNull(message = "Status is required")
    private Boolean status;
}