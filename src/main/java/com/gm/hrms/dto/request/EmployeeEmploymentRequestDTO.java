package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeEmploymentRequestDTO {

    @NotNull(message = "Date of joining is required")
    @PastOrPresent(message = "Date of joining cannot be in the future")
    private LocalDate dateOfJoining;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer yearOfExperience;

    @DecimalMin(value = "0.0", inclusive = false, message = "CTC must be greater than 0")
    private Double ctc;

    private List<
            @NotBlank(message = "Company name cannot be blank")
                    String> previousCompanyNames;

    @Min(value = 0, message = "Notice period cannot be negative")
    private Integer noticePeriod;
}