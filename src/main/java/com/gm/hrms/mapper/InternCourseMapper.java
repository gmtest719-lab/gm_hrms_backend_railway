package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.entity.InternCourse;

public class InternCourseMapper {

    public static InternCourse toEntity(InternCourseRequestDTO dto) {
        return InternCourse.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();
    }

    public static InternCourseResponseDTO toResponse(InternCourse course) {
        return InternCourseResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .status(course.getStatus())
                .build();
    }

    // PATCH update
    public static void patchUpdate(InternCourse course, InternCourseRequestDTO dto) {

        if (dto.getName() != null) {
            course.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            course.setStatus(dto.getStatus());
        }
    }
}