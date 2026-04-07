package com.gm.hrms.dto.response;

import com.gm.hrms.enums.TrainingCompletionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraineeEducationResponseDTO {

    // HSC
    private String hscCompletion;
    private Integer hscYear;

    // Bachelor
    private String bachelorCompletion;
    private Integer bachelorYear;

    // Master
    private String masterCompletion;
    private Integer masterYear;

    // Degree
    private String degreeName;
    private Double degreeResult;

    private String universityName;
    private String universityAddress;

    private TrainingCompletionStatus trainingCompletionStatus;
}