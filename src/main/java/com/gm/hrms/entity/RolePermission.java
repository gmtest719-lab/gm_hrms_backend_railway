package com.gm.hrms.entity;

import com.gm.hrms.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @ManyToOne
    private Permission permission;
}
