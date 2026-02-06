package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmployeeContactDTO {

    @Email(message = "Invalid personal email")
    private String personalEmail;

    @Email(message = "Invalid office email")
    private String officeEmail;

    private String personalPhone;

    private String emergencyPhone;
}
