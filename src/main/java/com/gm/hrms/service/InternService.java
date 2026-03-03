package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternRequestDTO;
import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;

import java.util.List;

public interface InternService {

    UserCreateResponseDTO create(InternRequestDTO dto, Long personalId);

    InternResponseDTO update(Long id, InternUpdateDTO dto);

    InternResponseDTO getById(Long id);

    List<InternResponseDTO> getAll();

    void delete(Long id);
}
