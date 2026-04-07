package com.gm.hrms.repository;

import com.gm.hrms.entity.InternCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternCourseRepository extends JpaRepository<InternCourse, Long> {

    boolean existsByName(String name);
    Page<InternCourse> findByStatusTrue(Pageable pageable);
}