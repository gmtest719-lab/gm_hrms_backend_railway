package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RoleType;

public class TraineeMapper {

    private TraineeMapper() {}

    public static TraineeResponseDTO toResponse(Trainee trainee) {

        PersonalInformation p = trainee.getPersonalInformation();
        WorkProfile wp = p != null ? p.getWorkProfile() : null;

        TraineeResponseDTO dto = TraineeResponseDTO.builder()
                .traineeId(trainee.getId())
                .traineeCode(trainee.getTraineeCode())
                .role(RoleType.TRAINEE)
                .recordStatus(p!=null ? p.getRecordStatus():null)
                .stipend(trainee.getStipend())
                .createdAt(trainee.getCreatedAt())
                .updatedAt(trainee.getUpdatedAt())
                .build();

        //  COMMON
        BaseUserMapper.mapCommon(dto, p);

        //  ROLE SPECIFIC
        dto.setWorkDetails(mapWork(trainee.getWorkDetails(), wp));
        dto.setEducationDetails(mapEducation(trainee.getEducationDetails()));

        return dto;
    }

    // ================= WORK =================
    private static TraineeWorkDetailsResponseDTO mapWork(
            TraineeWorkDetails w,
            WorkProfile wp
    ) {

        if (w == null) return null;

        return TraineeWorkDetailsResponseDTO.builder()
                .branchName(
                        wp != null && wp.getBranch() != null
                                ? wp.getBranch().getBranchName()
                                : null
                )
                .trainingPeriodMonths(w.getTrainingPeriodMonths())
                .trainingStartDate(w.getTrainingStartDate())
                .trainingEndDate(w.getTrainingEndDate())
                .trainingShiftTime(
                        wp != null && wp.getShift() != null
                                ? wp.getShift().getShiftName()
                                : null
                )
                .workMode(wp != null ? wp.getWorkMode() : null)
                .workingType(wp != null ? wp.getWorkingType() : null)
                .build();
    }

    // ================= EDUCATION =================
    private static TraineeEducationResponseDTO mapEducation(TraineeEducationDetails e) {

        if (e == null) return null;

        return TraineeEducationResponseDTO.builder()
                .hscCompletion(e.getHscCompletion())
                .hscYear(e.getHscYear())
                .bachelorCompletion(e.getBachelorCompletion())
                .bachelorYear(e.getBachelorYear())
                .masterCompletion(e.getMasterCompletion())
                .masterYear(e.getMasterYear())
                .degreeName(e.getDegreeName())
                .degreeResult(e.getDegreeResult())
                .universityName(e.getUniversityName())
                .universityAddress(e.getUniversityAddress())
                .trainingCompletionStatus(e.getTrainingCompletionStatus())
                .build();
    }
}