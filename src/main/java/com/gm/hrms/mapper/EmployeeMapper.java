package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;

public class EmployeeMapper {

    private EmployeeMapper() {}

    // ================= CREATE =================
    public static Employee toEntity(EmployeeRequestDTO dto, String autoCode) {
        return Employee.builder()
                .employeeCode(autoCode)
                .role(dto.getRole())
                .build();
    }

    // ================= RESPONSE =================
    public static EmployeeResponseDTO toResponse(Employee e) {

        PersonalInformation p = e.getPersonalInformation();
        WorkProfile wp = p != null ? p.getWorkProfile() : null;

        EmployeeResponseDTO dto = EmployeeResponseDTO.builder()
                .employeeId(e.getId())
                .employeeCode(e.getEmployeeCode())
                .role(e.getRole())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();

        //  COMMON
        BaseUserMapper.mapCommon(dto, p);

        //  ROLE SPECIFIC
        dto.setBranchName(
                wp != null && wp.getBranch() != null
                        ? wp.getBranch().getBranchName()
                        : null
        );

        dto.setShiftTiming(
                wp != null && wp.getShift() != null
                        ? wp.getShift().getShiftName()
                        : null
        );

        dto.setWorkMode(wp != null ? wp.getWorkMode() : null);
        dto.setWorkingType(wp != null ? wp.getWorkingType() : null);

        dto.setEmployment(mapEmployment(e.getEmployment()));

        return dto;
    }

    // ================= EMPLOYMENT =================
    private static EmployeeEmploymentResponseDTO mapEmployment(EmployeeEmployment emp) {

        if (emp == null) return null;

        return EmployeeEmploymentResponseDTO.builder()
                .dateOfJoining(emp.getDateOfJoining())
                .yearOfExperience(emp.getYearOfExperience())
                .ctc(emp.getCtc())
                .noticePeriod(emp.getNoticePeriod())
                .previousCompanyNames(emp.getPreviousCompanyNames())
                .build();
    }
}