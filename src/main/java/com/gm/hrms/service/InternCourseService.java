package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;

import java.util.List;

public interface InternCourseService {

    InternCourseResponseDTO createCourse(InternCourseRequestDTO dto);

    InternCourseResponseDTO updateCourse(Long id, InternCourseRequestDTO dto);

    List<InternCourseResponseDTO> getAllCourses();

    InternCourseResponseDTO getCourseById(Long id);

    void deleteCourse(Long id);
}