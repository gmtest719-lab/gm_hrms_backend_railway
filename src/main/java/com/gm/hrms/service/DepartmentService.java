package com.gm.hrms.service;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto);

    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto);

    DepartmentResponseDTO getDepartmentById(Long id);

    PageResponseDTO<DepartmentResponseDTO> getAllDepartments(Pageable pageable);

    void deleteDepartment(Long id);
}
