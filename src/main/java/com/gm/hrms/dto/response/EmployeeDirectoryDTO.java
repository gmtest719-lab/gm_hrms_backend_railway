package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDirectoryDTO {

    private Long   personalInformationId;
    private String employeeCode;
    private String fullName;
    private String department;
    private String designation;
    private String branch;

    // Contact — shown only to ADMIN/HR; masked/null for others
    private String officeEmail;
    private String personalPhone;

    private String role;
    private String profileImageUrl;
    private Boolean active;
}