package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.AddressDTO;
import com.gm.hrms.dto.request.EmployeeAddressRequestDTO;
import com.gm.hrms.dto.response.EmployeeAddressResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeAddress;

public class EmployeeAddressMapper {

    public static EmployeeAddress toEntity(
            EmployeeAddressRequestDTO dto,
            Employee employee) {

        EmployeeAddress entity = new EmployeeAddress();
        entity.setEmployee(employee);

        updateEntity(entity, dto);

        return entity;
    }

    public static void patchEntity(
            EmployeeAddress entity,
            EmployeeAddressRequestDTO dto) {

        if (dto.getSameAsCurrent() != null)
            entity.setSameAsCurrent(dto.getSameAsCurrent());

        // 🔹 PATCH CURRENT ADDRESS
        if (dto.getCurrentAddress() != null) {

            if (entity.getCurrentAddress() == null)
                entity.setCurrentAddress(new Address());

            patchAddress(entity.getCurrentAddress(), dto.getCurrentAddress());
        }

        // 🔹 PATCH PERMANENT ADDRESS
        if (Boolean.TRUE.equals(dto.getSameAsCurrent())) {

            entity.setPermanentAddress(entity.getCurrentAddress());

        } else if (dto.getPermanentAddress() != null) {

            if (entity.getPermanentAddress() == null)
                entity.setPermanentAddress(new Address());

            patchAddress(entity.getPermanentAddress(), dto.getPermanentAddress());
        }
    }

    private static void patchAddress(Address entity,
                                     AddressDTO dto) {

        if (dto.getAddress() != null)
            entity.setAddress(dto.getAddress());

        if (dto.getCity() != null)
            entity.setCity(dto.getCity());

        if (dto.getState() != null)
            entity.setState(dto.getState());

        if (dto.getPinCode() != null)
            entity.setPinCode(dto.getPinCode());

        if (dto.getDistrict() != null)
            entity.setDistrict(dto.getDistrict());

        if (dto.getLandmark() != null)
            entity.setLandmark(dto.getLandmark());

        if (dto.getCountry() != null)
            entity.setCountry(dto.getCountry());
    }


    public static void updateEntity(
            EmployeeAddress entity,
            EmployeeAddressRequestDTO dto) {

        Address current = mapToAddress(dto.getCurrentAddress());
        entity.setCurrentAddress(current);

        if (Boolean.TRUE.equals(dto.getSameAsCurrent())) {
            entity.setPermanentAddress(current);
        } else {
            entity.setPermanentAddress(mapToAddress(dto.getPermanentAddress()));
        }

        entity.setSameAsCurrent(dto.getSameAsCurrent());
    }

    private static Address mapToAddress(AddressDTO dto) {

        if (dto == null) return null;

        return Address.builder()
                .address(dto.getAddress())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .landmark(dto.getLandmark())
                .state(dto.getState())
                .pinCode(dto.getPinCode())
                .country(dto.getCountry())
                .build();
    }

    private static AddressDTO mapToDTO(Address address) {

        if (address == null) return null;

        AddressDTO dto = new AddressDTO();
        dto.setAddress(address.getAddress());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setLandmark(address.getLandmark());
        dto.setState(address.getState());
        dto.setPinCode(address.getPinCode());
        dto.setCountry(address.getCountry());

        return dto;
    }

    public static EmployeeAddressResponseDTO toResponse(EmployeeAddress entity) {

        return EmployeeAddressResponseDTO.builder()
                .currentAddress(mapToDTO(entity.getCurrentAddress()))
                .permanentAddress(mapToDTO(entity.getPermanentAddress()))
                .sameAsCurrent(entity.getSameAsCurrent())
                .build();
    }
}