package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LeaveTypeRequestDTO {

    @NotBlank
    private String name;
}
