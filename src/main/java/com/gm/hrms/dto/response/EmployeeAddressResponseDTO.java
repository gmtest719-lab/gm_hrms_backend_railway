package com.gm.hrms.dto.response;

import com.gm.hrms.dto.request.AddressDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeAddressResponseDTO {

    private AddressDTO currentAddress;
    private AddressDTO permanentAddress;
    private Boolean sameAsCurrent;
}