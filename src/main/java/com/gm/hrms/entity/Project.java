package com.gm.hrms.entity;

import com.gm.hrms.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_code")
    private String projectCode;

    private String description;

    @Column(name = "client_name")
    private String clientName; //optional

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "budget_amount")
    private Double budgetAmount;

    @Column(name = "actual_cost")
    private Double actualCost;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status; // NOT_STARTED / IN_PROGRESS / COMPLETED /ON_HOLD
}
