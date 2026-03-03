package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intern_college_details")
@Getter
@Setter
public class InternCollegeDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "intern_id")
    private Intern intern;

    private String courseName;
    private String semester;
    private String academicYear;
    private String enrollmentNumber;
    private String collegeName;

    @Column(length = 1000)
    private String collegeAddress;

    private String universityName;
    private String degreeCompletionStatus;
    private Integer year;
}