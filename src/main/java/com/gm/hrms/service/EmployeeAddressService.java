package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeAddressRequestDTO;
import com.gm.hrms.dto.response.EmployeeAddressResponseDTO;
import com.gm.hrms.entity.Employee;

public interface EmployeeAddressService {

    EmployeeAddressResponseDTO saveOrUpdate(
            Employee employee,
            EmployeeAddressRequestDTO dto);

    EmployeeAddressResponseDTO getAddress(Employee employee);
}