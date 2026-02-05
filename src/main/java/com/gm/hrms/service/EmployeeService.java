package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {

    // CREATE
    EmployeeResponseDTO create(EmployeeRequestDTO dto);

    // UPDATE
    EmployeeResponseDTO update(Long id, EmployeeUpdateDTO dto);

    // GET BY ID
    EmployeeResponseDTO getById(Long id);

    // GET ALL
    List<EmployeeResponseDTO> getAll();

    // DELETE
    void delete(Long id);
}
