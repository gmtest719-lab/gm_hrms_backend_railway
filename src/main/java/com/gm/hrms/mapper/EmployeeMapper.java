package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.entity.Employee;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequestDTO dto,
                                    Department dept,
                                    Designation desig){

        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .employeeCode(dto.getEmployeeCode())
                .dateOfJoining(dto.getDateOfJoining())
                .yearOfExperience(dto.getYearOfExperience())
                .employmentType(dto.getEmploymentType())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .profileImageUrl(dto.getProfileImageUrl())
                .department(dept)
                .designation(desig)
                .role(dto.getRole())
                .build();
    }

    public static EmployeeResponseDTO toResponse(Employee e){

        return EmployeeResponseDTO.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .employeeCode(e.getEmployeeCode())
                .departmentName(e.getDepartment().getName())
                .designationName(e.getDesignation().getName())
                .active(e.getActive())
                .role(e.getRole())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
