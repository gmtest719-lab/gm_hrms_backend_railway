package com.gm.hrms.dto.request;

import com.gm.hrms.dto.response.EmployeeAddressDTO;
import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.MaritalStatus;
import com.gm.hrms.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeUpdateDTO {

    // ===== PERSONAL (Delegated) =====
    @Valid
    private PersonalInformationRequestDTO personalInformation;

    // ===== CORE =====
    private String employeeCode;
    private Long departmentId;
    private Long designationId;
    private RoleType role;
    private Long reportingManagerId;

    // ===== MODULES =====
    private EmployeeEmploymentRequestDTO employment;
}