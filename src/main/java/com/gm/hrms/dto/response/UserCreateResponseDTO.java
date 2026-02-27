package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserCreateResponseDTO {

    private Long personalInformationId;  // 🔥 system identity

    private Long employeeId;             // module id (nullable for intern/trainee)

    private String employeeCode;

    private String fullName;

    private RoleType role;

    private String departmentName;

    private Boolean active;

    private LocalDateTime createdAt;
}