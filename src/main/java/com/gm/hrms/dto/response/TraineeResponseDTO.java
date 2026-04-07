package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TraineeResponseDTO extends BaseUserResponseDTO {

    private Long traineeId;

    private String traineeCode;
    private Double stipend;

    private TraineeWorkDetailsResponseDTO workDetails;
    private TraineeEducationResponseDTO educationDetails;
}