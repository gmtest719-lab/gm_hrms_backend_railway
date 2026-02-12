package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.EmployeeAddressDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAddress;
import com.gm.hrms.repository.EmployeeAddressRepository;
import com.gm.hrms.service.EmployeeAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeAddressServiceImpl implements EmployeeAddressService {

    private final EmployeeAddressRepository addressRepository;

    @Override
    public void createAddress(Employee employee, EmployeeAddressDTO dto) {

        EmployeeAddress address = new EmployeeAddress();

        address.setEmployee(employee);
        address.setCurrentAddress(dto.getCurrentAddress());
        address.setPermanentAddress(dto.getPermanentAddress());

        addressRepository.save(address);
    }

    @Override
    public void updateAddress(Employee employee, EmployeeAddressDTO dto) {

        EmployeeAddress address =
                addressRepository.findByEmployee(employee)
                        .orElse(new EmployeeAddress());

        address.setEmployee(employee);

        if(dto.getCurrentAddress() != null){
            address.setCurrentAddress(dto.getCurrentAddress());
        }

        if(dto.getPermanentAddress() != null){
            address.setPermanentAddress(dto.getPermanentAddress());
        }

        addressRepository.save(address);
    }


}
