package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbsentReportDTO {

    private Long personalInformationId;
    private String employeeCode;
    private String traineeCode;
    private String internCode;
    private String employeeName;
    private String department;
    private String designation;
    private LocalDate absentDate;
}