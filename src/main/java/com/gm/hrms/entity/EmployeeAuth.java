package com.gm.hrms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
        name = "employee_auth",
        indexes = {
                @Index(name = "idx_auth_username", columnList = "username"),
                @Index(name = "idx_auth_employee", columnList = "employee_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAuth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  1–1 mapping with Employee
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    //  Login identifier (office email)
    @Column(nullable = false, unique = true)
    private String username;

    //  BCrypt hashed password
    @Column(nullable = false)
    private String passwordHash;

    //  Account status
    @Column(nullable = false)
    private Boolean active = true;

    //  Security controls
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    private Boolean accountLocked = false;

    //  Session Tracking (ENTERPRISE )
    @Column(name = "is_logged_in", nullable = false)
    private Boolean isLoggedIn = false;

    //  Audit
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
