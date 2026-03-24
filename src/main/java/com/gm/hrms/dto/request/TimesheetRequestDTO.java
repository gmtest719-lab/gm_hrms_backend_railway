package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TimesheetRequestDTO {

    @NotNull
    private Long personId;

    @NotNull
    private LocalDate workDate;

    @NotEmpty
    private List<TimesheetEntryDTO> entries;

}