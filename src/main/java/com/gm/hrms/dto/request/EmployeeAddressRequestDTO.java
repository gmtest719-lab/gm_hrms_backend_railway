package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class EmployeeAddressRequestDTO {

    private AddressDTO currentAddress;
    private AddressDTO permanentAddress;
    private Boolean sameAsCurrent;
}