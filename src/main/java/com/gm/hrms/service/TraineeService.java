package com.gm.hrms.service;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TraineeService {

    // ================= CREATE =================
    UserCreateResponseDTO create(
            TraineeRequestDTO dto,
            Long personalInformationId
    );

    // ================= UPDATE (WITH DOCUMENTS) =================
    public TraineeResponseDTO update(
            Long id,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception;

    // ================= GET =================
    TraineeResponseDTO getById(Long id);

    List<TraineeResponseDTO> getAll();

    // ================= DELETE =================
    void delete(Long id);
}