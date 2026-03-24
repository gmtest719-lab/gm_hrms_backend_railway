package com.gm.hrms.controller;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.InternCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/intern-courses")
@RequiredArgsConstructor
public class InternCourseController {

    private final InternCourseService service;

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@RequestBody InternCourseRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Course created successfully")
                        .data(service.createCourse(dto))
                        .build()
        );
    }

    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @RequestBody InternCourseRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Course updated successfully")
                        .data(service.updateCourse(id, dto))
                        .build()
        );
    }

    // GET ALL
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Courses fetched successfully")
                        .data(service.getAllCourses())
                        .build()
        );
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {

        service.deleteCourse(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Course deleted successfully")
                        .build()
        );
    }
}