package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TraineeResponseDTO {

    // ===== IDENTIFIERS =====
    private Long personalInformationId;
    private Long traineeId;

    // ===== PERSONAL =====
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private MaritalStatus maritalStatus;
    private String spouseOrParentName;
    private Boolean active;

    // ===== TRAINEE CORE =====
    private String traineeCode;
    private String departmentName;
    private String designationName;
    private RoleType role;
    private Double stipend;
    private TraineeStatus status;

    // ===== MODULES =====
    private EmployeeContactResponseDTO contact;
    private EmployeeBankDetailsResponseDTO bankDetails;
    private EmployeeAddressResponseDTO address;

    private TraineeWorkDetailsResponseDTO workDetails;
    private TraineeEducationResponseDTO educationDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}