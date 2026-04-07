package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DepartmentMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    // CREATE
    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {

        if (repository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Department already exists with name: " + dto.getName()
            );
        }

        if (repository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException(
                    "Department already exists with code: " + dto.getCode()
            );
        }

        Department dept = DepartmentMapper.toEntity(dto);

        return DepartmentMapper.toResponse(repository.save(dept));
    }

    // UPDATE
    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {

        Department dept = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: " + id
                        ));

        if (dto.getName() != null &&
                repository.existsByName(dto.getName()) &&
                !dept.getName().equalsIgnoreCase(dto.getName())) {

            throw new DuplicateResourceException(
                    "Department already exists with name: " + dto.getName()
            );
        }

        if (dto.getCode() != null &&
                repository.existsByCode(dto.getCode()) &&
                !dept.getCode().equalsIgnoreCase(dto.getCode())) {

            throw new DuplicateResourceException(
                    "Department already exists with code: " + dto.getCode()
            );
        }

        // update using mapper
        DepartmentMapper.patchUpdate(dept, dto);

        return DepartmentMapper.toResponse(repository.save(dept));
    }

    // GET BY ID
    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {

        Department dept = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: " + id
                        ));

        return DepartmentMapper.toResponse(dept);
    }

    @Override
    public PageResponseDTO<DepartmentResponseDTO> getAllDepartments(Pageable pageable) {

        Page<Department> page = repository.findAll(pageable);

        List<DepartmentResponseDTO> content = page.getContent()
                .stream()
                .map(DepartmentMapper::toResponse)
                .toList();

        return PageResponseDTO.<DepartmentResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // DELETE
    @Override
    public void deleteDepartment(Long id) {

        Department dept = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: " + id
                        ));

        repository.delete(dept);
    }
}