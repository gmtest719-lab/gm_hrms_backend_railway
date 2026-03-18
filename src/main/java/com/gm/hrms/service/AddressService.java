package com.gm.hrms.service;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;

import java.util.List;

public interface AddressService {

    AddressResponseDTO create(AddressRequestDTO dto);

    AddressResponseDTO update(Long id, AddressRequestDTO dto);

    AddressResponseDTO getById(Long id);

    List<AddressResponseDTO> getAll();

    void delete(Long id);
}