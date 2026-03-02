package com.gm.hrms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateRequestDTO {

    @Valid
    @NotNull
    private PersonalInformationRequestDTO personalInformation;

    // Optional modules
    private EmployeeRequestDTO employee;
//    private InternRequestDTO intern;
    private TraineeRequestDTO trainee; // future
}