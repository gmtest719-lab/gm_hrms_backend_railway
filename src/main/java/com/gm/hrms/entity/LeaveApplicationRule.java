package com.gm.hrms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "leave_application_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 POLICY LINK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    // ================= RULES =================

    @NotNull
    private Boolean allowHalfDay;

    @NotNull
    @DecimalMin(value = "0.5", message = "Minimum leave duration must be at least 0.5")
    @DecimalMax(value = "365", message = "Invalid leave duration")
    private Double minLeaveDuration;

    @NotNull
    @Min(value = 1, message = "Max consecutive days must be at least 1")
    @Max(value = 365, message = "Max consecutive days too large")
    private Integer maxConsecutiveDays;

    @NotNull
    @Min(value = 0, message = "Apply before days cannot be negative")
    @Max(value = 365, message = "Apply before days too large")
    private Integer applyBeforeDays;

    @NotNull
    private Boolean allowBackdatedLeave;

    // ================= SANDWICH =================

    @NotNull
    private Boolean sandwichRuleEnabled;

    private Boolean includeHolidays;

    private Boolean includeWeekends;

    // ================= CONTROL =================

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isSystemDefined;
}