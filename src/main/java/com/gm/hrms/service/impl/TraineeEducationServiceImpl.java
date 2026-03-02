package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeEducationRequestDTO;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeEducationDetails;
import com.gm.hrms.repository.TraineeEducationRepository;
import com.gm.hrms.service.TraineeEducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeEducationServiceImpl implements TraineeEducationService {

    private final TraineeEducationRepository repository;

    @Override
    public void saveOrUpdate(Trainee trainee,
                             TraineeEducationRequestDTO dto) {

        if (dto == null) return;

        TraineeEducationDetails education =
                repository.findByTrainee(trainee)
                        .orElse(new TraineeEducationDetails());

        education.setTrainee(trainee);

        if (dto.getHscCompletion() != null)
            education.setHscCompletion(dto.getHscCompletion());

        if (dto.getHscYear() != null)
            education.setHscYear(dto.getHscYear());

        if (dto.getBachelorCompletion() != null)
            education.setBachelorCompletion(dto.getBachelorCompletion());

        if (dto.getBachelorYear() != null)
            education.setBachelorYear(dto.getBachelorYear());

        if (dto.getMasterCompletion() != null)
            education.setMasterCompletion(dto.getMasterCompletion());

        if (dto.getMasterYear() != null)
            education.setMasterYear(dto.getMasterYear());

        if (dto.getDegreeName() != null)
            education.setDegreeName(dto.getDegreeName());

        if (dto.getDegreeResult() != null)
            education.setDegreeResult(dto.getDegreeResult());

        if (dto.getUniversityName() != null)
            education.setUniversityName(dto.getUniversityName());

        if (dto.getUniversityAddress() != null)
            education.setUniversityAddress(dto.getUniversityAddress());

        if (dto.getTrainingCompletionStatus() != null)
            education.setTrainingCompletionStatus(dto.getTrainingCompletionStatus());

        repository.save(education);
    }
}