package com.gm.hrms.service;

import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.entity.Employee;

public interface EmployeeContactService {

    void createContact(Employee employee, EmployeeContactDTO dto);
    void updateContact(Employee employee, EmployeeContactDTO dto);

}
