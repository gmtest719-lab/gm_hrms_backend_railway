package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "allowance_types")
public class AllowanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // HRA, TA, Medical, Bonus
}
