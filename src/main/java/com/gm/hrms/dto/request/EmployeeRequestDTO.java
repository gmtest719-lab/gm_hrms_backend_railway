package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RoleType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
public class EmployeeRequestDTO {

    @NotBlank(message = "First name required")
    private String firstName;

    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;

    @NotBlank(message = "Employee code required")
    private String employeeCode;

    private LocalDate dateOfJoining;
    private Integer yearOfExperience;
    private String employmentType;
    private Boolean active;
    private String profileImageUrl;

    @NotNull(message = "Department ID required")
    private Long departmentId;

    @NotNull(message = "Designation ID required")
    private Long designationId;

    @NotNull(message = "Role required")
    private RoleType role;
}
