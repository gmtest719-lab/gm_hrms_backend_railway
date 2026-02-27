package com.gm.hrms.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String address;
    private String city;
    private String district;
    private String landmark;
    private String state;
    private String pinCode;
    private String country;
}