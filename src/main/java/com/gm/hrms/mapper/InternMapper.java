package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RoleType;

public class InternMapper {

    private InternMapper() {}

    public static InternResponseDTO toResponse(Intern intern) {

        if (intern == null) return null;

        PersonalInformation p = intern.getPersonalInformation();
        WorkProfile wp = p != null ? p.getWorkProfile() : null;

        InternResponseDTO dto = InternResponseDTO.builder()
                .internId(intern.getId())
                .internCode(intern.getInternCode())
                .role(RoleType.INTERN)
                .createdAt(intern.getCreatedAt())
                .updatedAt(intern.getUpdatedAt())
                .build();

        // ✅ COMMON
        BaseUserMapper.mapCommon(dto, p);

        // ✅ ROLE SPECIFIC
        dto.setCollegeDetails(mapCollege(intern.getCollegeDetails()));
        dto.setInternshipDetails(mapInternship(intern.getInternshipDetails(), wp));
        dto.setMentorDetails(mapMentor(intern.getMentorDetails()));

        return dto;
    }

    // ================= COLLEGE =================
    private static InternCollegeResponseDTO mapCollege(InternCollegeDetails c) {

        if (c == null) return null;

        return InternCollegeResponseDTO.builder()
                .courseName(c.getCourseName())
                .semester(c.getSemester())
                .academicYear(c.getAcademicYear())
                .enrollmentNumber(c.getEnrollmentNumber())
                .collegeName(c.getCollegeName())
                .collegeAddress(c.getCollegeAddress())
                .universityName(c.getUniversityName())
                .degreeCompletionStatus(c.getDegreeCompletionStatus())
                .year(c.getYear())
                .build();
    }

    // ================= INTERNSHIP =================
    private static InternInternshipResponseDTO mapInternship(
            InternInternshipDetails d,
            WorkProfile wp
    ) {

        if (d == null) return null;

        return InternInternshipResponseDTO.builder()
                .domainId(d.getDomain() != null ? d.getDomain().getId() : null)
                .domainName(d.getDomain() != null ? d.getDomain().getName() : null)
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .shiftTiming(
                        wp != null && wp.getShift() != null
                                ? wp.getShift().getShiftName()
                                : null
                )
                .mode(wp != null ? wp.getWorkMode() : null)
                .branchName(
                        wp != null && wp.getBranch() != null
                                ? wp.getBranch().getBranchName()
                                : null
                )
                .trainingPeriodMonths(d.getTrainingPeriodMonths())
                .stipend(d.getStipend())
                .internshipType(d.getInternshipType())
                .build();
    }

    // ================= MENTOR =================
    private static InternMentorResponseDTO mapMentor(InternMentorDetails m) {

        if (m == null) return null;

        return InternMentorResponseDTO.builder()
                .mentorEmployeeId(
                        m.getMentor() != null ? m.getMentor().getId() : null
                )
                .supervisorEmployeeId(
                        m.getSupervisor() != null ? m.getSupervisor().getId() : null
                )
                .build();
    }
}