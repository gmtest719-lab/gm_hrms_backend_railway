package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== CURRENT ADDRESS =====
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "current_address")),
            @AttributeOverride(name = "city", column = @Column(name = "current_city")),
            @AttributeOverride(name = "district", column = @Column(name = "current_district")),
            @AttributeOverride(name = "landmark", column = @Column(name = "current_landmark")),
            @AttributeOverride(name = "state", column = @Column(name = "current_state")),
            @AttributeOverride(name = "pinCode", column = @Column(name = "current_pin_code")),
            @AttributeOverride(name = "country", column = @Column(name = "current_country"))
    })
    private Address currentAddress;

    // ===== PERMANENT ADDRESS =====
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "permanent_address")),
            @AttributeOverride(name = "city", column = @Column(name = "permanent_city")),
            @AttributeOverride(name = "district", column = @Column(name = "permanent_district")),
            @AttributeOverride(name = "landmark", column = @Column(name = "permanent_landmark")),
            @AttributeOverride(name = "state", column = @Column(name = "permanent_state")),
            @AttributeOverride(name = "pinCode", column = @Column(name = "permanent_pin_code")),
            @AttributeOverride(name = "country", column = @Column(name = "permanent_country"))
    })
    private Address permanentAddress;

    private Boolean sameAsCurrent;

    @OneToOne
    @JoinColumn(name = "personal_information_id", nullable = false, unique = true)
    private PersonalInformation personalInformation;
}