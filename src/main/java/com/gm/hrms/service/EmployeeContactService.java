package com.gm.hrms.service;

import com.gm.hrms.dto.response.EmployeeContactDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeContact;

public interface EmployeeContactService {

    EmployeeContact createContact(Employee employee, EmployeeContactDTO dto);
    EmployeeContact updateContact(Employee employee, EmployeeContactDTO dto);

}
