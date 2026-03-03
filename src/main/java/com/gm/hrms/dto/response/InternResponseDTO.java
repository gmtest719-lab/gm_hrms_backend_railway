package com.gm.hrms.dto.response;

import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.InternStatus;
import com.gm.hrms.enums.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class InternResponseDTO {

    private Long personalInformationId;
    private Long internId;

    // PERSONAL
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Boolean active;

    // CORE
    private String internCode;
    private String departmentName;
    private String designationName;
    private RoleType role;
    private InternStatus status;

    // CONTACT
    private EmployeeContactResponseDTO contact;
    private EmployeeAddressResponseDTO address;
    private EmployeeBankDetailsResponseDTO bankDetails;

    // INTERN
    private InternCollegeResponseDTO collegeDetails;
    private InternInternshipResponseDTO internshipDetails;
    private InternMentorResponseDTO mentorDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
