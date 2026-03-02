package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeAddressRequestDTO;
import com.gm.hrms.dto.response.EmployeeAddressResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAddress;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeAddressMapper;
import com.gm.hrms.repository.EmployeeAddressRepository;
import com.gm.hrms.service.EmployeeAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeAddressServiceImpl implements EmployeeAddressService {

    private final EmployeeAddressRepository repository;

    @Override
    public EmployeeAddressResponseDTO saveOrUpdate(
            PersonalInformation personalInformation,
            EmployeeAddressRequestDTO dto) {

        EmployeeAddress address = repository.findByPersonalInformationId(personalInformation.getId())
                .orElse(null);

        if (address == null) {
            address = EmployeeAddressMapper.toEntity(dto, personalInformation);
        } else {
            EmployeeAddressMapper.patchEntity(address, dto);
        }

        repository.save(address);

        return EmployeeAddressMapper.toResponse(address);
    }

    @Override
    public EmployeeAddressResponseDTO getAddress(PersonalInformation personalInformation) {

        EmployeeAddress address = repository.findByPersonalInformationId(personalInformation.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return EmployeeAddressMapper.toResponse(address);
    }
}