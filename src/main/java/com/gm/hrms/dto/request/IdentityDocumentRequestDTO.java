package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class IdentityDocumentRequestDTO {

    private Long documentTypeId;

    // Required only if mandatory doc not uploaded
    private String reasonIfMissing;
}