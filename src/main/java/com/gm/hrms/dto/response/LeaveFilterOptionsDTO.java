package com.gm.hrms.dto.response;

import com.gm.hrms.enums.LeaveStatus;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveFilterOptionsDTO {
    private List<FilterOptionDTO>  employees;
    private List<FilterOptionDTO>  departments;
    private List<FilterOptionDTO>  designations;
    private List<FilterOptionDTO>  leaveTypes;
    private List<LeaveStatus>      leaveStatuses;
}