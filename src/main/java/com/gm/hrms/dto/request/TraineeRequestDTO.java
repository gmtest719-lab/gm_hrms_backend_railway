package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class TraineeRequestDTO {

    private Double stipend;

    private TraineeWorkDetailsRequestDTO workDetails;
    private TraineeEducationRequestDTO educationDetails;
}