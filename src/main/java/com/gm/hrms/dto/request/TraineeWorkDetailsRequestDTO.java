package com.gm.hrms.dto.request;

import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeWorkDetailsRequestDTO {

    private String branchName;

    private Integer trainingPeriodMonths;

    private LocalDate internshipStartDate;
    private LocalDate internshipEndDate;

    private String trainingShiftTime;

    private WorkMode workMode;       // REMOTE, HYBRID, ONSITE
    private WorkingType workingType; // PART_TIME, FULL_TIME
}