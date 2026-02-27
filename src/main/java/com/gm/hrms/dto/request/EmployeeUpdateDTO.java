package com.gm.hrms.dto.request;

import com.gm.hrms.dto.response.EmployeeAddressDTO;
import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.MaritalStatus;
import com.gm.hrms.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeUpdateDTO {

    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String employeeCode;

    private MaritalStatus maritalStatus;
    private String spouseOrParentName;
    private String previousCompanyName;

    private Boolean active;

    private Long departmentId;
    private Long designationId;
    private RoleType role;
    private Long reportingManagerId;

    private EmployeeContactRequestDTO contact;
    private EmployeeAddressRequestDTO address;
    private EmployeeEmploymentRequestDTO employment;
    private EmployeeBankDetailsRequestDTO bankDetails;
}