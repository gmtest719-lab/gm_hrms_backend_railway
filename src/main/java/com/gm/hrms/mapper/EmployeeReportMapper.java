package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.RoleType;

import java.util.Optional;

/**
 * Static mapping utility for Employee Report DTOs.
 *
 * Role-based masking rules:
 *  - ADMIN / HR  → full data (no masking)
 *  - EMPLOYEE / TRAINEE / INTERN → sensitive fields (phone, personal email,
 *    CTC, address, emergency phone) are nulled out unless viewing self
 */
public final class EmployeeReportMapper {

    private EmployeeReportMapper() {}

    // ──────────────────────────────────────────────────────────
    // Employee Master DTO
    // ──────────────────────────────────────────────────────────
    public static EmployeeMasterReportDTO toMasterDTO(
            PersonalInformation p,
            String employeeCode,
            RoleType viewerRole,
            boolean isSelf) {

        WorkProfile wp  = p.getWorkProfile();
        PersonalInformationContact c = p.getContact();
        EmployeeEmployment emp = resolveEmployment(p);

        boolean fullAccess = isAdminOrHr(viewerRole) || isSelf;

        return EmployeeMasterReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(employeeCode)
                .fullName(fullName(p))
                .role(roleOf(p))
                .gender(p.getGender())
                .dateOfBirth(fullAccess ? p.getDateOfBirth() : null)
                .maritalStatus(p.getMaritalStatus() != null
                        ? p.getMaritalStatus().name() : null)
                .employmentType(p.getEmploymentType())
                // Contact — masked unless admin/hr or self
                .officeEmail(c != null ? c.getOfficeEmail() : null)
                .personalEmail(fullAccess && c != null ? c.getPersonalEmail() : null)
                .personalPhone(fullAccess && c != null ? c.getPersonalPhone() : null)
                .emergencyPhone(isAdminOrHr(viewerRole) && c != null
                        ? c.getEmergencyPhone() : null)
                // Job
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .branch(wp != null && wp.getBranch() != null
                        ? wp.getBranch().getBranchName() : null)
                .dateOfJoining(wp != null ? wp.getDateOfJoining() : null)
                // Employment — CTC only for admin/hr
                .ctc(isAdminOrHr(viewerRole) && emp != null ? emp.getCtc() : null)
                .noticePeriod(emp != null ? emp.getNoticePeriod() : null)
                .yearOfExperience(emp != null ? emp.getYearOfExperience() : null)
                // Status
                .active(p.getActive())
                .recordStatus(p.getRecordStatus() != null
                        ? p.getRecordStatus().name() : null)
                // Address — masked unless admin/hr or self
                .currentAddress(fullAccess
                        ? addressString(p.getCurrentAddress()) : null)
                .permanentAddress(fullAccess
                        ? addressString(p.getPermanentAddress()) : null)
                .profileImageUrl(p.getProfileImageUrl())
                .build();
    }

    // ──────────────────────────────────────────────────────────
    // Directory DTO (limited fields)
    // ──────────────────────────────────────────────────────────
    public static EmployeeDirectoryDTO toDirectoryDTO(
            PersonalInformation p,
            String employeeCode,
            RoleType viewerRole) {

        WorkProfile wp = p.getWorkProfile();
        PersonalInformationContact c = p.getContact();

        return EmployeeDirectoryDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(employeeCode)
                .fullName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .branch(wp != null && wp.getBranch() != null
                        ? wp.getBranch().getBranchName() : null)
                // Contact visible only to admin/hr
                .officeEmail(isAdminOrHr(viewerRole) && c != null
                        ? c.getOfficeEmail() : null)
                .personalPhone(isAdminOrHr(viewerRole) && c != null
                        ? c.getPersonalPhone() : null)
                .role(roleOf(p))
                .profileImageUrl(p.getProfileImageUrl())
                .active(p.getActive())
                .build();
    }

    // ──────────────────────────────────────────────────────────
    // Joining Report DTO
    // ──────────────────────────────────────────────────────────
    public static EmployeeJoiningReportDTO toJoiningDTO(
            PersonalInformation p, String employeeCode) {

        WorkProfile wp = p.getWorkProfile();
        return EmployeeJoiningReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(employeeCode)
                .fullName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .branch(wp != null && wp.getBranch() != null
                        ? wp.getBranch().getBranchName() : null)
                .dateOfJoining(wp != null ? wp.getDateOfJoining() : null)
                .employmentType(p.getEmploymentType())
                .role(roleOf(p))
                .active(p.getActive())
                .build();
    }

    // ──────────────────────────────────────────────────────────
    // Exit / Attrition DTO
    // ──────────────────────────────────────────────────────────
    public static EmployeeExitReportDTO toExitDTO(
            PersonalInformation p, String employeeCode) {

        WorkProfile wp = p.getWorkProfile();
        return EmployeeExitReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(employeeCode)
                .fullName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .branch(wp != null && wp.getBranch() != null
                        ? wp.getBranch().getBranchName() : null)
                .dateOfJoining(wp != null ? wp.getDateOfJoining() : null)
                // exitDate and exitReason are stored in WorkProfile
                .exitDate(wp != null ? wp.getExitDate() : null)
                .exitReason(wp != null ? wp.getExitReason() : null)
                .exitRemarks(wp != null ? wp.getExitRemarks() : null)
                .role(roleOf(p))
                .build();
    }

    // ──────────────────────────────────────────────────────────
    // Status Report DTO
    // ──────────────────────────────────────────────────────────
    public static EmployeeStatusReportDTO toStatusDTO(
            PersonalInformation p, String employeeCode) {

        WorkProfile wp = p.getWorkProfile();
        return EmployeeStatusReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(employeeCode)
                .fullName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .employmentType(p.getEmploymentType())
                .active(p.getActive())
                .recordStatus(p.getRecordStatus() != null
                        ? p.getRecordStatus().name() : null)
                .dateOfJoining(wp != null ? wp.getDateOfJoining() : null)
                .role(roleOf(p))
                .build();
    }

    // ──────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────
    public static String fullName(PersonalInformation p) {
        if (p == null) return "";
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }

    public static boolean isAdminOrHr(RoleType role) {
        return role == RoleType.ADMIN || role == RoleType.HR;
    }

    private static String roleOf(PersonalInformation p) {
        // Determine role from employment type — can be enriched if UserAuth is joined
        if (p.getEmploymentType() == null) return null;
        return switch (p.getEmploymentType()) {
            case EMPLOYEE -> RoleType.EMPLOYEE.name();
            case INTERN   -> RoleType.INTERN.name();
            case TRAINEE  -> RoleType.TRAINEE.name();
        };
    }

    private static String addressString(Address a) {
        if (a == null) return null;
        StringBuilder sb = new StringBuilder();
        if (a.getAddress() != null) sb.append(a.getAddress());
        if (a.getCity()         != null) sb.append(", ").append(a.getCity());
        if (a.getState()        != null) sb.append(", ").append(a.getState());
        if (a.getPinCode()      != null) sb.append(" - ").append(a.getPinCode());
        return sb.toString();
    }

    private static EmployeeEmployment resolveEmployment(PersonalInformation p) {
        // Employee entity link
        if (p == null) return null;
        // We rely on Employee → Employment; reached through the repository in service
        // Return null here; service will enrich separately when needed
        return null;
    }
}