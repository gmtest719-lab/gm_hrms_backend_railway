package com.gm.hrms.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.InternStatus;
import com.gm.hrms.enums.MaritalStatus;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternUpdateDTO {

    // ===== PERSONAL (Delegated) =====
    @Valid
    private PersonalInformationRequestDTO personalInformation;

    // ===== CORE =====
    private String internCode;
    private Long departmentId;
    private Long designationId;
    private InternStatus status;

    // ===== COLLEGE =====
    @Valid
    private InternCollegeRequestDTO collegeDetails;

    // ===== INTERNSHIP =====
    @Valid
    private InternInternshipRequestDTO internshipDetails;

    // ===== MENTOR =====
    @Valid
    private InternMentorRequestDTO mentorDetails;
}