package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeBankDetailsRequestDTO;
import com.gm.hrms.entity.Employee;

public interface EmployeeBankDetailsService {

    void saveOrUpdate(Employee employee,
                      EmployeeBankDetailsRequestDTO dto);
}