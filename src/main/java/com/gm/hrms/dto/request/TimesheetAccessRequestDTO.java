package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TimesheetAccessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimesheetAccessRequestDTO {

    @NotNull
    private Long personId;

    @NotNull
    private LocalDate requestedDate;

    @NotNull
    private TimesheetAccessType accessType;

    @NotBlank
    private String reason;
}