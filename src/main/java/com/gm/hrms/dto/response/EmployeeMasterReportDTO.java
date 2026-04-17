package com.gm.hrms.dto.response;

import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeMasterReportDTO {

    // ── Identity ──────────────────────────────────────────────
    private Long   personalInformationId;
    private String employeeCode;
    private String fullName;
    private String role;

    // ── Personal Info ─────────────────────────────────────────
    private Gender        gender;
    private LocalDate     dateOfBirth;
    private String        maritalStatus;
    private EmploymentType employmentType;

    // ── Contact (masked for non-admin) ────────────────────────
    private String personalEmail;   // masked for EMPLOYEE / TRAINEE / INTERN
    private String officeEmail;
    private String personalPhone;   // masked for EMPLOYEE / TRAINEE / INTERN
    private String emergencyPhone;  // masked

    // ── Job Details ───────────────────────────────────────────
    private String    department;
    private String    designation;
    private String    branch;
    private LocalDate dateOfJoining;
    private String    shiftName;

    // ── Employment ────────────────────────────────────────────
    private Double  ctc;            // null for non-admin / non-hr
    private Integer noticePeriod;
    private Integer yearOfExperience;

    // ── Status ────────────────────────────────────────────────
    private Boolean active;
    private String  recordStatus;

    // ── Address (masked for non-admin) ────────────────────────
    private String currentAddress;
    private String permanentAddress;

    // ── Profile ───────────────────────────────────────────────
    private String profileImageUrl;
}