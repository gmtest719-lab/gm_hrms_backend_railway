package com.gm.hrms.entity;

import com.gm.hrms.enums.TraineeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainee extends  BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String traineeCode;   // GMTR001

    @ManyToOne
    private Department department;

    @ManyToOne
    private Designation designation;

    private Double stipend;

    @Enumerated(EnumType.STRING)
    private TraineeStatus status; // ACTIVE, INACTIVE, ON_HOLD

    @OneToOne
    @JoinColumn(name = "personal_information_id", unique = true)
    private PersonalInformation personalInformation;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL)
    private TraineeWorkDetails workDetails;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL)
    private TraineeEducationDetails educationDetails;

}
