package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmployeeContactRequestDTO {

    @Email(message = "Invalid personal email")
    private String personalEmail;

    @Email(message = "Invalid office email")
    private String officeEmail;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid personal phone")
    private String personalPhone;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid emergency phone")
    private String emergencyPhone;
}