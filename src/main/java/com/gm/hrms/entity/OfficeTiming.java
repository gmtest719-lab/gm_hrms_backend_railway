package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "office_timings")
@Getter
@Setter
public class OfficeTiming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "late_threshold_minutes")
    private Integer lateThresholdMinutes;
}
