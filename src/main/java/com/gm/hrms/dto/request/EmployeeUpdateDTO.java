package com.gm.hrms.dto.request;

import com.gm.hrms.dto.response.EmployeeAddressDTO;
import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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

    private EmployeeContactDTO contact;
    private EmployeeAddressDTO address;
}
