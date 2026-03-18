package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_eligibility_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveEligibilityRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  POLICY LINK (ONE RULE PER POLICY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    //  PROBATION
    @Column(nullable = false)
    private Integer probationPeriodInMonths;

    @Column(nullable = false)
    private Boolean restrictPaidLeaveDuringProbation;

    @Column(nullable = false)
    private Boolean allowSickLeaveDuringProbation;

    @Column(nullable = false)
    private Boolean allowUnpaidLeaveDuringProbation;

    @Column(nullable = false)
    private Boolean allowCompOff;

    //  SYSTEM CONTROL
    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isSystemDefined;
}