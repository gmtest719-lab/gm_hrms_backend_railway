package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TrainingCompletionStatus;
import lombok.Data;

@Data
public class TraineeEducationRequestDTO {

    // HSC
    private String hscCompletion;
    private Integer hscYear;

    // Bachelor
    private String bachelorCompletion;
    private Integer bachelorYear;

    // Master
    private String masterCompletion;
    private Integer masterYear;

    // Degree Details
    private String degreeName;
    private Double degreeResult;

    private String universityName;
    private String universityAddress;

    private TrainingCompletionStatus trainingCompletionStatus;
}