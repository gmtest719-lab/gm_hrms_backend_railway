package com.gm.hrms.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class BranchUpdateDTO {

    private String branchName;

    private String branchCode;

    private Boolean active;

    @Valid
    private AddressRequestDTO address;
}