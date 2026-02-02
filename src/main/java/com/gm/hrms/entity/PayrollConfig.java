package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payroll_configs")
public class PayrollConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "basic_salary")
    private Double basicSalary;

    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
