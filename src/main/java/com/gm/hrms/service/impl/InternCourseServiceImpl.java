package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.InternCourse;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.InternCourseMapper;
import com.gm.hrms.repository.InternCourseRepository;
import com.gm.hrms.service.InternCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternCourseServiceImpl implements InternCourseService {

    private final InternCourseRepository repository;

    // CREATE COURSE
    @Override
    public InternCourseResponseDTO createCourse(InternCourseRequestDTO dto) {

        if (repository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Course already exists with name: " + dto.getName()
            );
        }

        InternCourse course = InternCourseMapper.toEntity(dto);

        return InternCourseMapper.toResponse(repository.save(course));
    }

    // UPDATE COURSE (PATCH)
    @Override
    public InternCourseResponseDTO updateCourse(Long id, InternCourseRequestDTO dto) {

        InternCourse course = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + id
                        ));

        if (dto.getName() != null &&
                repository.existsByName(dto.getName()) &&
                !course.getName().equalsIgnoreCase(dto.getName())) {

            throw new DuplicateResourceException(
                    "Course already exists with name: " + dto.getName()
            );
        }

        InternCourseMapper.patchUpdate(course, dto);

        return InternCourseMapper.toResponse(repository.save(course));
    }

    // GET ACTIVE COURSES ONLY
    @Override
    public PageResponseDTO<InternCourseResponseDTO> getAllCourses(Pageable pageable) {

        Page<InternCourse> page = repository.findByStatusTrue(pageable);

        List<InternCourseResponseDTO> content = page.getContent()
                .stream()
                .map(InternCourseMapper::toResponse)
                .toList();

        return PageResponseDTO.<InternCourseResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // GET COURSE BY ID
    @Override
    public InternCourseResponseDTO getCourseById(Long id) {

        InternCourse course = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + id
                        ));

        return InternCourseMapper.toResponse(course);
    }

    // SOFT DELETE
    @Override
    public void deleteCourse(Long id) {

        InternCourse course = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + id
                        ));

        course.setStatus(false);

        repository.save(course);
    }
}