package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // SL, CL, Paid, Unpaid, Comp Off
}
