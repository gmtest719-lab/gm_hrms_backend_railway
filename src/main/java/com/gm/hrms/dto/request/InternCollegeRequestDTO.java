package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InternCollegeRequestDTO {

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotBlank(message = "Enrollment number is required")
    private String enrollmentNumber;

    @NotBlank(message = "College name is required")
    private String collegeName;

    @NotBlank(message = "College address is required")
    private String collegeAddress;

    @NotBlank(message = "University name is required")
    private String universityName;

    @NotBlank(message = "Degree completion status is required")
    private String degreeCompletionStatus;

    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 10, message = "Year cannot exceed 10")
    private Integer year;
}