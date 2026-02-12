package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.entity.Designation;

public class DesignationMapper {

    public static Designation toEntity(DesignationRequestDTO dto) {
        return Designation.builder()
                .name(dto.getName())
                .build();
    }

    public static DesignationResponseDTO toResponse(Designation entity) {
        return DesignationResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
