package com.gm.hrms.service;

import com.gm.hrms.dto.response.FilterOptionDTO;
import com.gm.hrms.dto.response.LeaveFilterOptionsDTO;

import java.util.List;

public interface LeaveReportFilterService {
    LeaveFilterOptionsDTO getAllFilterOptions();
    List<FilterOptionDTO> getEmployeesByDepartment(Long departmentId);
}