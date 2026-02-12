package com.gm.hrms.entity;

import com.gm.hrms.mapper.TimesheetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "timesheets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_timesheet_employee_project_date",
                        columnNames = {
                                "employee_id",
                                "project_id",
                                "work_date"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timesheet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Work Date
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    //  Hours Worked
    @Column(nullable = false)
    private Double hours;

    //  Work Description
    @Column(length = 1000)
    private String description;

    //  Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimesheetStatus status = TimesheetStatus.DRAFT;

    //  Approval Info
    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDate approvedAt;

    //  Relations
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
}
