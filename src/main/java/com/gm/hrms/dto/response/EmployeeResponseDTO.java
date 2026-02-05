package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RoleType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String employeeCode;

    private String departmentName;
    private String designationName;

    private Boolean active;
    private RoleType role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

