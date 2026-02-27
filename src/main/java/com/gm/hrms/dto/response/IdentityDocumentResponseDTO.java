package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IdentityDocumentResponseDTO {

    private Long id;

    private String documentName;

    private Boolean mandatory;

    private String filePath;

    private String reasonIfMissing;

    private Boolean verified;

    private LocalDateTime verifiedAt;
}