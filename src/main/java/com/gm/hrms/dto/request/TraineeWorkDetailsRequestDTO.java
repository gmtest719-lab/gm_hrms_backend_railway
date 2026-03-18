package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeWorkDetailsRequestDTO {

    @NotNull(message = "Training period is required")
    @Min(value = 1, message = "Training period must be at least 1 month")
    private Integer trainingPeriodMonths;

    @NotNull(message = "Training start date is required")
    private LocalDate trainingStartDate;

    @NotNull(message = "Training end date is required")
    private LocalDate trainingEndDate;
}