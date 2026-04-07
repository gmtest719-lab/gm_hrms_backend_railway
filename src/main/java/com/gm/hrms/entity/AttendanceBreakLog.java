package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_break_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceBreakLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @ManyToOne
    @JoinColumn(name = "break_policy_id")
    private BreakPolicy breakPolicy;

    private LocalDateTime breakStart;

    private LocalDateTime breakEnd;

    private Integer durationMinutes;
}