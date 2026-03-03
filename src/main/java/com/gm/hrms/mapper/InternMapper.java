package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RoleType;

public class InternMapper {

    private InternMapper() {}

    public static InternResponseDTO toResponse(Intern intern) {

        if (intern == null) return null;

        PersonalInformation p = intern.getPersonalInformation();

        return InternResponseDTO.builder()

                // ===== IDS =====
                .personalInformationId(p != null ? p.getId() : null)
                .internId(intern.getId())

                // ===== PERSONAL =====
                .firstName(p != null ? p.getFirstName() : null)
                .middleName(p != null ? p.getMiddleName() : null)
                .lastName(p != null ? p.getLastName() : null)
                .gender(p != null ? p.getGender() : null)
                .dateOfBirth(p != null ? p.getDateOfBirth() : null)
                .active(p != null ? p.getActive() : null)

                // ===== CORE =====
                .internCode(intern.getInternCode())
                .departmentName(
                        intern.getDepartment() != null
                                ? intern.getDepartment().getName()
                                : null
                )
                .designationName(
                        intern.getDesignation() != null
                                ? intern.getDesignation().getName()
                                : null
                )
                .role(RoleType.INTERN)
                .status(intern.getStatus())

                // ===== CONTACT =====
                .contact(mapContact(p != null ? p.getContact() : null))

                // ===== ADDRESS =====
                .address(
                        p != null
                                ? EmployeeAddressMapper.toResponse(p.getAddress())
                                : null
                )

                // ===== BANK =====
                .bankDetails(mapBank(p != null ? p.getBankLegalDetails() : null))

                // ===== COLLEGE =====
                .collegeDetails(mapCollege(intern.getCollegeDetails()))

                // ===== INTERNSHIP =====
                .internshipDetails(mapInternship(intern.getInternshipDetails()))

                // ===== MENTOR =====
                .mentorDetails(mapMentor(intern.getMentorDetails()))

                .createdAt(intern.getCreatedAt())
                .updatedAt(intern.getUpdatedAt())
                .build();
    }

    // ================= CONTACT =================

    private static EmployeeContactResponseDTO mapContact(
            PersonalInformationContact contact) {

        if (contact == null) return null;

        return EmployeeContactResponseDTO.builder()
                .personalPhone(contact.getPersonalPhone())
                .emergencyPhone(contact.getEmergencyPhone())
                .personalEmail(contact.getPersonalEmail())
                .officeEmail(contact.getOfficeEmail())
                .build();
    }

    // ================= BANK =================

    private static EmployeeBankDetailsResponseDTO mapBank(
            BankLegalDetails bank) {

        if (bank == null) return null;

        return EmployeeBankDetailsResponseDTO.builder()
                .bankName(bank.getBankName())
                .accountNumber(bank.getAccountNumber())
                .ifscCode(bank.getIfscCode())
                .panNumber(bank.getPanNumber())
                .aadhaarNumber(bank.getAadhaarNumber())
                .uanNumber(bank.getUanNumber())
                .esicNumber(bank.getEsicNumber())
                .build();
    }

    // ================= COLLEGE =================

    private static InternCollegeResponseDTO mapCollege(
            InternCollegeDetails college) {

        if (college == null) return null;

        return InternCollegeResponseDTO.builder()
                .courseName(college.getCourseName())
                .semester(college.getSemester())
                .academicYear(college.getAcademicYear())
                .enrollmentNumber(college.getEnrollmentNumber())
                .collegeName(college.getCollegeName())
                .collegeAddress(college.getCollegeAddress())
                .universityName(college.getUniversityName())
                .degreeCompletionStatus(college.getDegreeCompletionStatus())
                .year(college.getYear())
                .build();
    }

    // ================= INTERNSHIP =================

    private static InternInternshipResponseDTO mapInternship(
            InternInternshipDetails details) {

        if (details == null) return null;

        return InternInternshipResponseDTO.builder()
                .domainId(
                        details.getDomain() != null
                                ? details.getDomain().getId()
                                : null
                )
                .domainName(
                        details.getDomain() != null
                                ? details.getDomain().getName()
                                : null
                )
                .startDate(details.getStartDate())
                .endDate(details.getEndDate())
                .shiftTiming(details.getShiftTiming())
                .mode(details.getMode())
                .build();
    }

    // ================= MENTOR =================

    private static InternMentorResponseDTO mapMentor(
            InternMentorDetails m) {

        if (m == null) return null;

        Employee mentor = m.getMentor();
        Employee supervisor = m.getSupervisor();

        return InternMentorResponseDTO.builder()

                .mentorEmployeeId(mentor != null ? mentor.getId() : null)
                .mentorName(
                        mentor != null
                                ? mentor.getPersonalInformation().getFirstName()
                                + " "
                                + mentor.getPersonalInformation().getLastName()
                                : null
                )
                .mentorDesignation(
                        mentor != null && mentor.getDesignation() != null
                                ? mentor.getDesignation().getName()
                                : null
                )

                .supervisorEmployeeId(supervisor != null ? supervisor.getId() : null)
                .supervisorName(
                        supervisor != null
                                ? supervisor.getPersonalInformation().getFirstName()
                                + " "
                                + supervisor.getPersonalInformation().getLastName()
                                : null
                )
                .supervisorDesignation(
                        supervisor != null && supervisor.getDesignation() != null
                                ? supervisor.getDesignation().getName()
                                : null
                )

                .build();
    }
}