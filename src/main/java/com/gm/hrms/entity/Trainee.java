package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private Double stipend;

    @OneToOne
    @JoinColumn(name = "personal_information_id", unique = true)
    private PersonalInformation personalInformation;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL)
    private TraineeWorkDetails workDetails;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL)
    private TraineeEducationDetails educationDetails;

}
