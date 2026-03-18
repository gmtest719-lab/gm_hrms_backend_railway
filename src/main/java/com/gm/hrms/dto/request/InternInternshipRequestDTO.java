package com.gm.hrms.dto.request;

import com.gm.hrms.enums.InternShipType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InternInternshipRequestDTO {

    @NotNull(message = "Domain is required")
    private Long domainId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Training period is required")
    @Min(value = 1, message = "Training period must be at least 1 month")
    private Integer trainingPeriodMonths;

    @NotNull(message = "Internship type is required")
    private InternShipType internshipType;

    private Double stipend;
}