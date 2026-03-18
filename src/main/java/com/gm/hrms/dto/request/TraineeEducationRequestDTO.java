package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TrainingCompletionStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TraineeEducationRequestDTO {

    // ================= HSC =================
    @NotBlank(message = "HSC completion status is required")
    private String hscCompletion;

    @Min(value = 1900, message = "Invalid HSC year")
    @Max(value = 2100, message = "Invalid HSC year")
    private Integer hscYear;

    // ================= BACHELOR =================
    private String bachelorCompletion;

    @Min(value = 1900, message = "Invalid bachelor year")
    @Max(value = 2100, message = "Invalid bachelor year")
    private Integer bachelorYear;

    // ================= MASTER =================
    private String masterCompletion;

    @Min(value = 1900, message = "Invalid master year")
    @Max(value = 2100, message = "Invalid master year")
    private Integer masterYear;

    // ================= DEGREE =================
    @NotBlank(message = "Degree name is required")
    private String degreeName;

    @DecimalMin(value = "0.0", message = "Result cannot be negative")
    @DecimalMax(value = "100.0", message = "Result cannot exceed 100")
    private Double degreeResult;

    @NotBlank(message = "University name is required")
    private String universityName;

    @NotBlank(message = "University address is required")
    private String universityAddress;

    @NotNull(message = "Training completion status is required")
    private TrainingCompletionStatus trainingCompletionStatus;
}