package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TraineeStatus;
import lombok.Data;

@Data
public class TraineeUpdateDTO {

    // personal fields optional (partial update)
    private String firstName;
    private String lastName;
    private Boolean active;

    private Long departmentId;
    private Long designationId;

    private Double stipend;
    private TraineeStatus status;

    private TraineeWorkDetailsRequestDTO workDetails;
    private TraineeEducationRequestDTO educationDetails;
}
