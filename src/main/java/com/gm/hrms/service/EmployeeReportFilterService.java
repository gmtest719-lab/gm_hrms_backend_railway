package com.gm.hrms.service;

import com.gm.hrms.dto.response.EmployeeFilterOptionsDTO;

public interface EmployeeReportFilterService {

    /** All dropdown options for the employee-report filter panel. */
    EmployeeFilterOptionsDTO getAllFilterOptions();
}