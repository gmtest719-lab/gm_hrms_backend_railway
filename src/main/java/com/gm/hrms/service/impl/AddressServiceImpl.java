package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.repository.AddressRepository;
import com.gm.hrms.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;

    @Override
    public AddressResponseDTO create(AddressRequestDTO dto) {

        Address address = AddressMapper.toEntity(dto);

        repository.save(address);

        return AddressMapper.toResponse(address);
    }

    @Override
    public AddressResponseDTO update(Long id, AddressRequestDTO dto) {

        Address address = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        AddressMapper.patchEntity(address, dto);

        repository.save(address);

        return AddressMapper.toResponse(address);
    }

    @Override
    public AddressResponseDTO getById(Long id) {

        Address address = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return AddressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Address address = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        repository.delete(address);
    }
}