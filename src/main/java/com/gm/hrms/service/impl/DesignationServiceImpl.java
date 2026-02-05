package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DesignationMapper;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository repository;

    @Override
    public DesignationResponseDTO create(DesignationRequestDTO dto) {

        if (repository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Designation already exists with name: " + dto.getName());
        }

        Designation entity = repository.save(
                DesignationMapper.toEntity(dto));

        return DesignationMapper.toResponse(entity);
    }

    @Override
    public DesignationResponseDTO update(Long id, DesignationRequestDTO dto) {

        Designation entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        //  Duplicate Check During Update
        if (repository.existsByName(dto.getName()) &&
                !entity.getName().equalsIgnoreCase(dto.getName())) {

            throw new DuplicateResourceException(
                    "Designation already exists with name: " + dto.getName());
        }

        entity.setName(dto.getName());

        return DesignationMapper.toResponse(repository.save(entity));
    }


    @Override
    public DesignationResponseDTO getById(Long id) {

        Designation entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        return DesignationMapper.toResponse(entity);
    }

    @Override
    public List<DesignationResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(DesignationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {

        Designation entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        repository.delete(entity);
    }
}
