package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.entity.Address;

public class AddressMapper {

    private AddressMapper() {}

    public static Address toEntity(AddressRequestDTO dto) {

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

    public static void patchEntity(Address entity, AddressRequestDTO dto) {

        if (dto.getAddress() != null)
            entity.setAddress(dto.getAddress());

        if (dto.getCity() != null)
            entity.setCity(dto.getCity());

        if (dto.getDistrict() != null)
            entity.setDistrict(dto.getDistrict());

        if (dto.getLandmark() != null)
            entity.setLandmark(dto.getLandmark());

        if (dto.getState() != null)
            entity.setState(dto.getState());

        if (dto.getPinCode() != null)
            entity.setPinCode(dto.getPinCode());

        if (dto.getCountry() != null)
            entity.setCountry(dto.getCountry());
    }

    public static AddressResponseDTO toResponse(Address entity) {

        return AddressResponseDTO.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .city(entity.getCity())
                .district(entity.getDistrict())
                .landmark(entity.getLandmark())
                .state(entity.getState())
                .pinCode(entity.getPinCode())
                .country(entity.getCountry())
                .build();
    }
}