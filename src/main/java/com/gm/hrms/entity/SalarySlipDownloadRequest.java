package com.gm.hrms.entity;

import com.gm.hrms.enums.DownloadRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks an employee's one-time request to download a specific salary slip.
 *
 * Lifecycle:
 *   PENDING  →  APPROVED  →  DOWNLOADED
 *           ↘  REJECTED
 *
 * A new request can be raised only if no PENDING / APPROVED request exists
 * for the same slip.
 */
@Entity
@Table(
        name = "salary_slip_download_requests",
        uniqueConstraints = @UniqueConstraint(
                name  = "uk_download_req_slip",
                columnNames = {"salary_slip_id"}          // one live request per slip
        )
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SalarySlipDownloadRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The slip this request is for. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_slip_id", nullable = false)
    private SalarySlip salarySlip;

    /** Redundant shortcut — avoids joining to SalarySlip just to send emails. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DownloadRequestStatus status;

    /**
     * BCrypt hash of the one-time password sent to the employee.
     * Set only when status = APPROVED.
     */
    @Column(name = "password_hash")
    private String passwordHash;

    /** When the employee submitted the request. */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** When the admin approved or rejected the request. */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /** Populated when status = REJECTED. */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}