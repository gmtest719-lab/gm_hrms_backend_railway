package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BranchResponseDTO {

    private Long id;

    private String branchName;

    private String branchCode;

    private Boolean active;

    private AddressResponseDTO address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}