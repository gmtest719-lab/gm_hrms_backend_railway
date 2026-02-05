package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RoleType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeUpdateDTO {

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String employeeCode;
    private LocalDate dateOfJoining;
    private Integer yearOfExperience;
    private String employmentType;
    private Boolean active;
    private String profileImageUrl;
    private Long departmentId;
    private Long designationId;
    private RoleType role;
}

