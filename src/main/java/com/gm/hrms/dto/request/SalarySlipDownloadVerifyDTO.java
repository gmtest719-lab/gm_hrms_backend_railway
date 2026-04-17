package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalarySlipDownloadVerifyDTO {

    @NotBlank(message = "Password is required")
    private String password;
}
