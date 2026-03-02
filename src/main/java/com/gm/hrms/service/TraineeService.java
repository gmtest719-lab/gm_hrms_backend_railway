package com.gm.hrms.service;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;

import java.util.List;

public interface TraineeService {

    UserCreateResponseDTO create(TraineeRequestDTO dto, Long personalInformationId);

    TraineeResponseDTO update(Long id, TraineeUpdateDTO dto);

    TraineeResponseDTO getById(Long id);

    List<TraineeResponseDTO> getAll();

    void delete(Long id);
}