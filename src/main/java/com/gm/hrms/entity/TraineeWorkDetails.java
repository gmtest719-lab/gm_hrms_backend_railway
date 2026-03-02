package com.gm.hrms.entity;

import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainee_work_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeWorkDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branchName;
    private Integer trainingPeriodMonths;

    private LocalDate internshipStartDate;
    private LocalDate internshipEndDate;

    private String trainingShiftTime;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode; // REMOTE, HYBRID, ONSITE

    @Enumerated(EnumType.STRING)
    private WorkingType workingType; // PART_TIME, FULL_TIME

    @OneToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
}
