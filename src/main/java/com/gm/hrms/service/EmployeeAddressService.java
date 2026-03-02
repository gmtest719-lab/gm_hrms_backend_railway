package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeAddressRequestDTO;
import com.gm.hrms.dto.response.EmployeeAddressResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.PersonalInformation;

public interface EmployeeAddressService {

    EmployeeAddressResponseDTO saveOrUpdate(
            PersonalInformation personalInformation,
            EmployeeAddressRequestDTO dto);

    EmployeeAddressResponseDTO getAddress(PersonalInformation personalInformation);
}