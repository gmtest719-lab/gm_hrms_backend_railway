package com.gm.hrms.entity;

import com.gm.hrms.enums.TimesheetAccessStatus;
import com.gm.hrms.enums.TimesheetAccessType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "timesheet_access_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetAccessRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonalInformation person;

    /**
     * The date for which access is requested.
     * For EDIT_OLD  → the date of the existing timesheet.
     * For EXTRA_WORK → today's date.
     */
    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private TimesheetAccessType accessType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TimesheetAccessStatus status;

    @Column(name = "reason", length = 500)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private PersonalInformation reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "access_expires_at")
    private LocalDateTime accessExpiresAt;
}