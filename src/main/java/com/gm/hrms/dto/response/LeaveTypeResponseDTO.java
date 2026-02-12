package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveTypeResponseDTO {

    private Long id;
    private String name;
}

