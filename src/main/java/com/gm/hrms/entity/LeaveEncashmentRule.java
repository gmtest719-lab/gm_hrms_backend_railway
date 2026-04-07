package com.gm.hrms.entity;

import com.gm.hrms.enums.EncashmentTiming;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_encashment_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveEncashmentRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 POLICY LINK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    // ================= RULE =================

    @Column(nullable = false)
    private Boolean isEnabled;

    @Column(nullable = false)
    private Integer maxEncashment;   // max leaves allowed to encash

    @Enumerated(EnumType.STRING)
    private EncashmentTiming timing; // YEAR_END / RESIGNATION / BOTH

    // ================= SYSTEM =================

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isSystemDefined;
}