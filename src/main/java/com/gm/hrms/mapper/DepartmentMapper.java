package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.entity.Department;

public class DepartmentMapper {

    public static Department toEntity(DepartmentRequestDTO dto) {
        return Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();
    }

    public static DepartmentResponseDTO toResponse(Department dept) {
        return DepartmentResponseDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .code(dept.getCode())
                .description(dept.getDescription())
                .status(dept.getStatus())
                .build();
    }

    // PATCH Update Mapper
    public static void patchUpdate(Department dept, DepartmentRequestDTO dto) {

        if (dto.getName() != null) {
            dept.setName(dto.getName());
        }

        if (dto.getCode() != null) {
            dept.setCode(dto.getCode());
        }

        if (dto.getDescription() != null) {
            dept.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            dept.setStatus(dto.getStatus());
        }
    }
}