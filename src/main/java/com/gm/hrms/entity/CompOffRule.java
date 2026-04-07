package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comp_off_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompOffRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 POLICY LINK (1 policy = 1 rule)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    // ================= RULE CONFIG =================

    @Column(nullable = false)
    private Boolean isEnabled;

    @Column(nullable = false)
    private Boolean approvalRequired;

    @Column(nullable = false)
    private Integer maxPerMonth;   // e.g. 2 comp off/month

    @Column(nullable = false)
    private Integer expiryDays;    // e.g. 30 days validity

    // ================= SYSTEM =================

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isSystemDefined;
}