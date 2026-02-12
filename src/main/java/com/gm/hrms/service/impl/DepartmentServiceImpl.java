package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DepartmentMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    //  CREATE
    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {

        if (repository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Department already exists with name: " + dto.getName()
            );
        }

        Department dept = DepartmentMapper.toEntity(dto);

        return DepartmentMapper.toResponse(repository.save(dept));
    }

    //  UPDATE
    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {

        Department dept = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: " + id
                        ));

        // Optional duplicate check during update
        if (repository.existsByName(dto.getName()) &&
                !dept.getName().equalsIgnoreCase(dto.getName())) {

            throw new DuplicateResourceException(
                    "Department already exists with name: " + dto.getName()
            );
        }

        dept.setName(dto.getName());

        return DepartmentMapper.toResponse(repository.save(dept));
    }

    //  GET BY ID
    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {

        Department dept = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: " + id
                        ));

        return DepartmentMapper.toResponse(dept);
    }

    //  GET ALL
    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {

        return repository.findAll()
                .stream()
                .map(DepartmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    //  DELETE
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
