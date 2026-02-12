package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    // CREATE
    EmployeeResponseDTO create(EmployeeRequestDTO dto,List<MultipartFile> documents);

    // UPDATE
    EmployeeResponseDTO update(Long id, EmployeeUpdateDTO dto,List<MultipartFile> documents);

    // GET BY ID
    EmployeeResponseDTO getById(Long id);

    // GET ALL
    List<EmployeeResponseDTO> getAll();

    // DELETE
    void delete(Long id);
}
