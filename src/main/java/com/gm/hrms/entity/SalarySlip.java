package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "salary_slips")
public class SalarySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer month;
    private Integer year;

    @Column(name = "net_salary")
    private Double netSalary;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
