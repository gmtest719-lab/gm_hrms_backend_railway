package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeDocumentResponseDTO {

    private Long id;
    private String documentType;
    private String filePath;
}
