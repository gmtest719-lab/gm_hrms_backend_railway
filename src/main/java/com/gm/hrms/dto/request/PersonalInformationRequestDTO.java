package com.gm.hrms.dto.request;

import com.gm.hrms.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalInformationRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Middle name is required")
    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "PersonalInformation type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Marital status is required")
    private MaritalStatus maritalStatus;

    @NotBlank(message = "Spouse or parent name is required")
    private String spouseOrParentName;

    @NotBlank(message = "Profile image is required")
    private String profileImageUrl;

    // Contact
    @NotBlank(message = "Personal phone is required")
    private String personalPhone;

    @NotBlank(message = "Emergency phone is required")
    private String emergencyPhone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Personal email is required")
    private String personalEmail;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Office email is required")
    private String officeEmail;
}