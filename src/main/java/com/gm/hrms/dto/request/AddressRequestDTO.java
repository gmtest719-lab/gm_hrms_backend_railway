package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressRequestDTO {

    @NotBlank(message = "Address required")
    private String address;

    @NotBlank(message = "City required")
    private String city;

    @NotBlank(message = "State required")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Invalid PIN code")
    private String pinCode;

    private String district;
    private String landmark;
    private String country;
}