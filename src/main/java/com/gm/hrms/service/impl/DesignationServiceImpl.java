package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DesignationMapper;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository repository;

    // ================= CREATE =================

    @Override
    public DesignationResponseDTO create(DesignationRequestDTO dto) {

        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException(
                    "Designation already exists with name: " + dto.getName());
        }

        Designation entity = DesignationMapper.toEntity(dto);

        repository.save(entity);

        return DesignationMapper.toResponse(entity);
    }

    // ================= UPDATE =================

    @Override
    public DesignationResponseDTO update(Long id, DesignationRequestDTO dto) {

        Designation entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        // Duplicate check only if name is changing
        if (dto.getName() != null &&
                repository.existsByNameIgnoreCase(dto.getName()) &&
                !entity.getName().equalsIgnoreCase(dto.getName())) {

            throw new DuplicateResourceException(
                    "Designation already exists with name: " + dto.getName());
        }

        DesignationMapper.updateEntity(entity, dto);

        repository.save(entity);

        return DesignationMapper.toResponse(entity);
    }

    // ================= GET BY ID =================

    @Override
    public DesignationResponseDTO getById(Long id) {

        Designation entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        return DesignationMapper.toResponse(entity);
    }

    // ================= GET ALL =================

    @Override
    public PageResponseDTO<DesignationResponseDTO> getAll(Pageable pageable) {

        Page<Designation> page = repository.findAll(pageable);

        List<DesignationResponseDTO> content = page.getContent()
                .stream()
                .map(DesignationMapper::toResponse)
                .toList();

        return PageResponseDTO.<DesignationResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= DELETE =================

    @Override
    public void delete(Long id) {

        Designation entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        // Soft delete recommended
        entity.setActive(false);

        repository.save(entity);
    }
}