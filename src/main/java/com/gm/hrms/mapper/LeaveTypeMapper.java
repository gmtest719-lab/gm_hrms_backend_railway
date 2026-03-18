package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.entity.LeaveType;

public class LeaveTypeMapper {

    private LeaveTypeMapper() {}

    // ================= CREATE =================
    public static LeaveType toEntity(LeaveTypeRequestDTO dto) {
        return LeaveType.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .isPaid(dto.getIsPaid())
                .allowHalfDay(dto.getAllowHalfDay())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    // ================= RESPONSE =================
    public static LeaveTypeResponseDTO toResponse(LeaveType entity) {
        return LeaveTypeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .isPaid(entity.getIsPaid())
                .allowHalfDay(entity.getAllowHalfDay())
                .isActive(entity.getIsActive())
                .isSystemDefined(entity.getIsSystemDefined())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ================= PATCH UPDATE =================
    public static void updateEntity(LeaveType entity, LeaveTypeRequestDTO dto) {

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getCode() != null) {
            entity.setCode(dto.getCode().toUpperCase());
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getIsPaid() != null) {
            entity.setIsPaid(dto.getIsPaid());
        }

        if (dto.getAllowHalfDay() != null) {
            entity.setAllowHalfDay(dto.getAllowHalfDay());
        }
    }
}