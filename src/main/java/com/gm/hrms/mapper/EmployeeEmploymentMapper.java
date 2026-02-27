package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.EmployeeEmploymentRequestDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeEmployment;

public class EmployeeEmploymentMapper {

    private EmployeeEmploymentMapper() {}

    public static EmployeeEmployment toEntity(EmployeeEmploymentRequestDTO dto,
                                              Employee employee) {

        if (dto == null) return null;

        return EmployeeEmployment.builder()
                .employee(employee)
                .dateOfJoining(dto.getDateOfJoining())
                .yearOfExperience(dto.getYearOfExperience())
                .ctc(dto.getCtc())
                .workMode(dto.getWorkMode())
                .employeeStatus(dto.getEmployeeStatus())
                .noticePeriod(dto.getNoticePeriod())
                .shiftTiming(dto.getShiftTiming())
                .branchName(dto.getBranchName())
                .build();
    }
}