package com.gm.hrms.dto.request;

import com.gm.hrms.enums.Status;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TraineeUpdateDTO {


    @Valid
    private PersonalInformationRequestDTO personalInformation;

    private Double stipend;

    private  String traineeCode;
    @Valid
    private TraineeWorkDetailsRequestDTO workDetails;
    @Valid
    private TraineeEducationRequestDTO educationDetails;
}
