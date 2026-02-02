package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deduction_types")
public class DeductionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // PF, Tax, Loan, Insurance
}
