package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_legal_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankLegalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;

    @Column(length = 18)
    private String accountNumber;

    private String ifscCode;

    @Column(length = 10)
    private String panNumber;

    @Column(length = 12)
    private String aadhaarNumber;

    private String uanNumber;

    private String esicNumber;

    @OneToOne
    @JoinColumn(name = "personal_information_id", nullable = false, unique = true)
    private PersonalInformation personalInformation;
}