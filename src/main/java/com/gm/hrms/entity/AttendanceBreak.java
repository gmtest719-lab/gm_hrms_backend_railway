package com.gm.hrms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_breaks")
public class AttendanceBreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "break_in")
    private LocalDateTime breakIn;

    @Column(name = "break_out")
    private LocalDateTime breakOut;

    @ManyToOne
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;
}

