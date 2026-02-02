package com.gm.hrms.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "office_timings")
public class OfficeTiming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "late_threshold_minutes")
    private Integer lateThresholdMinutes;
}
