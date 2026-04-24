package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
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

    // CREATE ────────────────────────────────────────────────────
    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {

        if (repository.existsByName(dto.getName()))
            throw new DuplicateResourceException("Department already exists with name: " + dto.getName());

        if (repository.existsByCode(dto.getCode()))
            throw new DuplicateResourceException("Department already exists with code: " + dto.getCode());

        Department parent = resolveParent(dto.getParentId(), null);

        Department dept = DepartmentMapper.toEntity(dto, parent);
        repository.save(dept);

        return DepartmentMapper.toResponse(dept, 0L); // brand new → 0 children
    }

    // UPDATE
    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {

        Department dept = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (dto.getName() != null
                && repository.existsByName(dto.getName())
                && !dept.getName().equalsIgnoreCase(dto.getName()))
            throw new DuplicateResourceException("Department already exists with name: " + dto.getName());

        if (dto.getCode() != null
                && repository.existsByCode(dto.getCode())
                && !dept.getCode().equalsIgnoreCase(dto.getCode()))
            throw new DuplicateResourceException("Department already exists with code: " + dto.getCode());

        if (dto.getParentId() != null) {
            Department newParent = resolveParent(dto.getParentId(), dept);
            dept.setParent(newParent);
        }

        DepartmentMapper.patchUpdate(dept, dto);
        repository.save(dept);

        long count = repository.countByParentId(dept.getId());
        return DepartmentMapper.toResponse(dept, count);
    }

    // GET BY ID
    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {

        Department dept = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        long count = repository.countByParentId(dept.getId());
        return DepartmentMapper.toResponse(dept, count);
    }

    // GET ALL (root-level, paginated)
    @Override
    public PageResponseDTO<DepartmentResponseDTO> getAllDepartments(Pageable pageable) {

        Page<Department> page = repository.findAllByParentIsNull(pageable);

        List<DepartmentResponseDTO> content = page.getContent().stream()
                .map(d -> DepartmentMapper.toResponse(d, repository.countByParentId(d.getId())))
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

    // GET SUB-DEPARTMENTS
    @Override
    public List<DepartmentResponseDTO> getSubDepartments(Long parentId) {

        if (!repository.existsById(parentId))
            throw new ResourceNotFoundException("Department not found with id: " + parentId);

        return repository.findAllByParentId(parentId).stream()
                .map(d -> DepartmentMapper.toResponse(d, repository.countByParentId(d.getId())))
                .toList();
    }

    // DELETE
    @Override
    public void deleteDepartment(Long id) {

        Department dept = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (repository.countByParentId(id) > 0)
            throw new InvalidRequestException(
                    "Cannot delete department with id: " + id + " - it has active sub-departments. " +
                            "Reassign or delete them first.");

        repository.delete(dept);
    }

    // PRIVATE HELPERS
    private Department resolveParent(Long parentId, Department self) {

        if (parentId == null) return null;

        Department parent = repository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent department not found with id: " + parentId));

        if (self != null && parent.getId().equals(self.getId()))
            throw new InvalidRequestException("A department cannot be its own parent.");

        if (self != null && isAncestor(self, parent))
            throw new InvalidRequestException(
                    "Circular reference detected: department id " + parent.getId() +
                            " is already a descendant of department id " + self.getId());

        return parent;
    }

    private boolean isAncestor(Department target, Department candidate) {
        Department cursor = candidate.getParent();
        while (cursor != null) {
            if (cursor.getId().equals(target.getId())) return true;
            cursor = cursor.getParent();
        }
        return false;
    }
}