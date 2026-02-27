package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;

import java.util.List;

public interface EmployeeService {

    UserCreateResponseDTO create(EmployeeRequestDTO dto, Long personalInformationId);

    EmployeeResponseDTO update(Long personalInformationId, EmployeeUpdateDTO dto);

    EmployeeResponseDTO getById(Long personalInformationId);

    List<EmployeeResponseDTO> getAll();

    void delete(Long personalInformationId);
}