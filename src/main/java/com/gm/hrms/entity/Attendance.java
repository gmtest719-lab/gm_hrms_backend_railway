package com.gm.hrms.entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(name="clock_in")
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Column(name = "total_working_time")
    private Duration totalWorkingTime;

    @Column(name = "total_break_time")
    private Duration totalBreakTime;

    @Column(name = "late_in")
    private Boolean lateIn;

    @Column(name = "half_day")
    private Boolean halfDay;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
