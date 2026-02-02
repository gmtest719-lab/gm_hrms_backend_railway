package com.gm.hrms.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "timesheets")
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_date")
    private LocalDate workDate;

    private Double hours;
    private String description;

    private String status; // Draft / Submitted / Approved

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
