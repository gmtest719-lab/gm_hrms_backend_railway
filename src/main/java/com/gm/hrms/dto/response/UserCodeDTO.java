package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCodeDTO {
    private String employeeCode;
    private String traineeCode;
    private String internCode;

    public String getDisplayCode() {
        if (employeeCode != null) return employeeCode;
        if (traineeCode  != null) return traineeCode;
        if (internCode   != null) return internCode;
        return "";
    }
}