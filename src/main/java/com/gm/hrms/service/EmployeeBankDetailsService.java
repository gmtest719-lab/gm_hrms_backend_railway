package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeBankDetailsRequestDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.PersonalInformation;

public interface EmployeeBankDetailsService {

    void saveOrUpdate(PersonalInformation personalInformation,
                      EmployeeBankDetailsRequestDTO dto);
}