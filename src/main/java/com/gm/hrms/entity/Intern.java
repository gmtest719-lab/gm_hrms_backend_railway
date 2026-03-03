package com.gm.hrms.entity;

import com.gm.hrms.enums.InternStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "interns")
@Getter
@Setter
public class Intern extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String internCode;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Designation designation;

    @OneToOne
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @Enumerated(EnumType.STRING)
    private InternStatus status;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private InternCollegeDetails collegeDetails;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private InternInternshipDetails internshipDetails;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private InternMentorDetails mentorDetails;
}
