package com.gm.hrms.dto.request;

import com.gm.hrms.enums.InternStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InternRequestDTO {

    @NotNull
    private Long departmentId;

    @NotNull
    private Long designationId;

    private InternStatus status;

    @Valid
    private InternCollegeRequestDTO collegeDetails;

    @Valid
    private InternInternshipRequestDTO internshipDetails;

    @Valid
    private InternMentorRequestDTO mentorDetails;
}