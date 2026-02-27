package com.gm.hrms.dto.request;
import com.gm.hrms.enums.EmployeeStatus;
import com.gm.hrms.enums.WorkMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeEmploymentRequestDTO {

    @NotNull(message = "Date of joining required")
    private LocalDate dateOfJoining;

    @Min(value = 0)
    private Integer yearOfExperience;

    @DecimalMin(value = "0.0")
    private Double ctc;

    private List<String> previousCompanyNames;

    @NotNull
    private WorkMode workMode;

    @NotNull
    private EmployeeStatus employeeStatus;

    @Min(value = 0)
    private Integer noticePeriod;

    private String shiftTiming;
    private String branchName;
}