package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.entity.Department;

public class DepartmentMapper {

    public static Department toEntity(DepartmentRequestDTO dto, Department parent) {
        return Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .parent(parent)
                .build();
    }

    public static DepartmentResponseDTO toResponse(Department dept, long subDepartmentCount) {
        return DepartmentResponseDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .code(dept.getCode())
                .description(dept.getDescription())
                .status(dept.getStatus())
                .parentId(dept.getParent() != null ? dept.getParent().getId()   : null)
                .parentName(dept.getParent() != null ? dept.getParent().getName() : null)
                .subDepartmentCount(subDepartmentCount)
                .build();
    }

    public static void patchUpdate(Department dept, DepartmentRequestDTO dto) {
        if (dto.getName()        != null) dept.setName(dto.getName());
        if (dto.getCode()        != null) dept.setCode(dto.getCode());
        if (dto.getDescription() != null) dept.setDescription(dto.getDescription());
        if (dto.getStatus()      != null) dept.setStatus(dto.getStatus());
    }
}