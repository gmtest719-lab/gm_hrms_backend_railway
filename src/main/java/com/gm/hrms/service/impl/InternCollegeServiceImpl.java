package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternCollegeRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternCollegeDetails;
import com.gm.hrms.repository.InternCollegeRepository;
import com.gm.hrms.service.InternCollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternCollegeServiceImpl implements InternCollegeService {

    private final InternCollegeRepository repository;

    @Override
    public void saveOrUpdate(Intern intern,
                             InternCollegeRequestDTO dto) {

        if (dto == null) return;

        InternCollegeDetails college =
                repository.findByIntern(intern)
                        .orElse(new InternCollegeDetails());

        college.setIntern(intern);

        if (dto.getCourseName() != null)
            college.setCourseName(dto.getCourseName());

        if (dto.getSemester() != null)
            college.setSemester(dto.getSemester());

        if (dto.getAcademicYear() != null)
            college.setAcademicYear(dto.getAcademicYear());

        if (dto.getEnrollmentNumber() != null)
            college.setEnrollmentNumber(dto.getEnrollmentNumber());

        if (dto.getCollegeName() != null)
            college.setCollegeName(dto.getCollegeName());

        if (dto.getCollegeAddress() != null)
            college.setCollegeAddress(dto.getCollegeAddress());

        if (dto.getUniversityName() != null)
            college.setUniversityName(dto.getUniversityName());

        if (dto.getDegreeCompletionStatus() != null)
            college.setDegreeCompletionStatus(dto.getDegreeCompletionStatus());

        if (dto.getYear() != null)
            college.setYear(dto.getYear());

        repository.save(college);
    }
}