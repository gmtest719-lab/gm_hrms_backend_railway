package com.gm.hrms.entity;

import com.gm.hrms.enums.TrainingCompletionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainee_education_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeEducationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hscCompletion;
    private Integer hscYear;

    private String bachelorCompletion;
    private Integer bachelorYear;

    private String masterCompletion;
    private Integer masterYear;

    private String degreeName;
    private Double degreeResult;

    private String universityName;
    private String universityAddress;

    @Enumerated(EnumType.STRING)
    private TrainingCompletionStatus trainingCompletionStatus;

    @OneToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
}
