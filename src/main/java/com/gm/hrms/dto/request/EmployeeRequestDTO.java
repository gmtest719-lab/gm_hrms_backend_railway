package com.gm.hrms.dto.request;

import com.gm.hrms.dto.response.EmployeeAddressDTO;
import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.MaritalStatus;
import com.gm.hrms.enums.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
@Data
public class EmployeeRequestDTO {


    // ===== OFFICE INFO =====
    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotNull(message = "Designation is required")
    private Long designationId;

    @NotNull(message = "Role is required")
    private RoleType role;

    private Long reportingManagerId;

    @Valid
    private EmployeeAddressRequestDTO address;

    @Valid
    private EmployeeEmploymentRequestDTO employment;

    @Valid
    private EmployeeBankDetailsRequestDTO bankDetails;


}