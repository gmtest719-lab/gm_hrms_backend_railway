package com.gm.hrms.service;

import com.gm.hrms.dto.response.AttendanceFilterOptionsDTO;
import com.gm.hrms.dto.response.FilterOptionDTO;
import java.util.List;

public interface AttendanceFilterService {

    AttendanceFilterOptionsDTO getAllFilterOptions();

    List<FilterOptionDTO> getDesignationsByDepartment(Long departmentId);

    List<FilterOptionDTO> getEmployeesByDepartment(Long departmentId);
}