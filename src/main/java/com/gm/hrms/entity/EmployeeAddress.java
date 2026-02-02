package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_addresses")
public class EmployeeAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}

