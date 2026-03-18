package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeWorkDetailsRequestDTO;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeWorkDetails;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.TraineeWorkRepository;
import com.gm.hrms.service.TraineeWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeWorkServiceImpl implements TraineeWorkService {

    private final TraineeWorkRepository repository;

    @Override
    public void saveOrUpdate(Trainee trainee,
                             TraineeWorkDetailsRequestDTO dto) {

        if (dto == null) return;

        TraineeWorkDetails work =
                repository.findByTrainee(trainee)
                        .orElse(new TraineeWorkDetails());

        work.setTrainee(trainee);

        // ================= VALIDATION =================

        if (dto.getTrainingStartDate() != null &&
                dto.getTrainingEndDate() != null &&
                dto.getTrainingEndDate().isBefore(dto.getTrainingStartDate())) {

            throw new InvalidRequestException("Training end date cannot be before start date");
        }

        // ================= SET =================

        if (dto.getTrainingPeriodMonths() != null)
            work.setTrainingPeriodMonths(dto.getTrainingPeriodMonths());

        if (dto.getTrainingStartDate() != null)
            work.setTrainingStartDate(dto.getTrainingStartDate());

        if (dto.getTrainingEndDate() != null)
            work.setTrainingEndDate(dto.getTrainingEndDate());

        repository.save(work);
    }
}