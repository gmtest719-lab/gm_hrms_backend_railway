package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponseDTO {

    private Long personalInformationId;
    private Long employeeId;

    // ===== PERSONAL =====
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private MaritalStatus maritalStatus;
    private String spouseOrParentName;
    private Boolean active;

    // ===== EMPLOYEE =====
    private String employeeCode;
    private String departmentName;
    private String designationName;
    private String reportingManagerName;
    private RoleType role;

    // ===== MODULES =====
    private EmployeeContactResponseDTO contact;
    private EmployeeAddressResponseDTO address;
    private EmployeeEmploymentResponseDTO employment;
    private EmployeeBankDetailsResponseDTO bankDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}