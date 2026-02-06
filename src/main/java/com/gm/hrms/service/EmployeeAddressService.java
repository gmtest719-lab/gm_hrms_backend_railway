package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeAddressDTO;
import com.gm.hrms.entity.Employee;

public interface EmployeeAddressService {

    void createAddress(Employee employee, EmployeeAddressDTO dto);
    void updateAddress(Employee employee, EmployeeAddressDTO dto);

}
