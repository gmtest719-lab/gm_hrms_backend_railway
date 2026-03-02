package com.gm.hrms.entity;

import com.gm.hrms.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "personal_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInformation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== BASIC INFO =====

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType; // EMPLOYEE / INTERN / TRAINEE

    @Column(nullable = false)
    private Boolean active = true;

    // ===== MARITAL =====

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaritalStatus maritalStatus;

    @Column(nullable = false)
    private String spouseOrParentName;

    // ===== PROFILE =====

    @Column(nullable = false)
    private String profileImageUrl;

    // ===== CONTACT =====

    @OneToOne(mappedBy = "personalInformation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private PersonalInformationContact contact;


    // Bank Module
    @OneToOne(mappedBy = "personalInformation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private BankLegalDetails bankLegalDetails;

    // Address Module
    @OneToOne(mappedBy = "personalInformation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private EmployeeAddress address;

}