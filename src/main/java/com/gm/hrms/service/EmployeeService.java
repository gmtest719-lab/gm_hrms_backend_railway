package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    UserCreateResponseDTO create(EmployeeRequestDTO dto, Long personalInformationId);

    public EmployeeResponseDTO update(
            Long id,
            String employeeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception;

    EmployeeResponseDTO getById(Long personalInformationId);

    PageResponseDTO<EmployeeResponseDTO> getAll(Pageable pageable);

    void delete(Long personalInformationId);
}