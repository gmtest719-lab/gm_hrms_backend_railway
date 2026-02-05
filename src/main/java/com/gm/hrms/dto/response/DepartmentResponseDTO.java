package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponseDTO {

    private Long id;
    private String name;
}
