package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimesheetRequestDTO {

    @NotNull
    private LocalDate workDate;

    @NotNull
    @Min(0)
    @Max(24)
    private Double hours;

    private String description;

    @NotNull
    private Long projectId;
}

