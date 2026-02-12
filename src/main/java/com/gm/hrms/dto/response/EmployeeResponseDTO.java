package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RoleType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private EmployeeContactResponseDTO contact;
    private EmployeeAddressResponseDTO address;
    private List<EmployeeDocumentResponseDTO> documents;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


