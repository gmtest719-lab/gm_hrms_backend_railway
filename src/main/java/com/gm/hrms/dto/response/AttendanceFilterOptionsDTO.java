package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.enums.RegularizationStatus;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceFilterOptionsDTO {

    private List<FilterOptionDTO>      departments;
    private List<FilterOptionDTO>      designations;
    private List<FilterOptionDTO>      shifts;
    private List<FilterOptionDTO>      branches;
    private List<FilterOptionDTO>      employees;
    private List<AttendanceStatus>     attendanceStatuses;
    private List<RegularizationStatus> regularizationStatuses;
}