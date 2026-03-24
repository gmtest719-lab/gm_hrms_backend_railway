package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;


public interface InternCourseService {

    InternCourseResponseDTO createCourse(InternCourseRequestDTO dto);

    InternCourseResponseDTO updateCourse(Long id, InternCourseRequestDTO dto);

    PageResponseDTO<InternCourseResponseDTO> getAllCourses(Pageable pageable);

    InternCourseResponseDTO getCourseById(Long id);

    void deleteCourse(Long id);
}