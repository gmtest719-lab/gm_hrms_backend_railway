package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_allowances")
public class EmployeeAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "payroll_config_id", nullable = false)
    private PayrollConfig payrollConfig;

    @ManyToOne
    @JoinColumn(name = "allowance_type_id", nullable = false)
    private AllowanceType allowanceType;
}

