package com.gm.hrms.entity;

import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.enums.WorkMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "intern_internship_details")
@Getter
@Setter
public class InternInternshipDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "intern_id")
    private Intern intern;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private InternshipDomain domain;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer trainingPeriodMonths;
    private Double stipend;

    @Enumerated(EnumType.STRING)
    private InternShipType internshipType;
}