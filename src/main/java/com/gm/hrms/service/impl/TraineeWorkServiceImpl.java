package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeWorkDetailsRequestDTO;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeWorkDetails;
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

        if (dto.getBranchName() != null)
            work.setBranchName(dto.getBranchName());

        if (dto.getTrainingPeriodMonths() != null)
            work.setTrainingPeriodMonths(dto.getTrainingPeriodMonths());

        if (dto.getInternshipStartDate() != null)
            work.setInternshipStartDate(dto.getInternshipStartDate());

        if (dto.getInternshipEndDate() != null)
            work.setInternshipEndDate(dto.getInternshipEndDate());

        if (dto.getTrainingShiftTime() != null)
            work.setTrainingShiftTime(dto.getTrainingShiftTime());

        if (dto.getWorkMode() != null)
            work.setWorkMode(dto.getWorkMode());

        if (dto.getWorkingType() != null)
            work.setWorkingType(dto.getWorkingType());

        repository.save(work);
    }
}