package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.TraineeEducationResponseDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.TraineeWorkDetailsResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RoleType;

public class TraineeMapper {

    private TraineeMapper() {}

    public static TraineeResponseDTO toResponse(Trainee trainee) {

        PersonalInformation p = trainee.getPersonalInformation();

        return TraineeResponseDTO.builder()

                // ===== IDENTIFIERS =====
                .personalInformationId(p.getId())
                .traineeId(trainee.getId())

                // ===== PERSONAL =====
                .firstName(p.getFirstName())
                .middleName(p.getMiddleName())
                .lastName(p.getLastName())
                .gender(p.getGender())
                .dateOfBirth(p.getDateOfBirth())
                .maritalStatus(p.getMaritalStatus())
                .spouseOrParentName(p.getSpouseOrParentName())
                .active(p.getActive())

                // ===== CORE =====
                .traineeCode(trainee.getTraineeCode())
                .departmentName(
                        trainee.getDepartment() != null
                                ? trainee.getDepartment().getName()
                                : null
                )
                .designationName(
                        trainee.getDesignation() != null
                                ? trainee.getDesignation().getName()
                                : null
                )
                .role(RoleType.TRAINEE)
                .stipend(trainee.getStipend())
                .status(trainee.getStatus())

                // ===== CONTACT =====
                .contact(
                        p.getContact() != null
                                ? EmployeeMapper.mapContact(p.getContact())
                                : null
                )

                // ===== BANK =====
                .bankDetails(
                        p.getBankLegalDetails() != null
                                ? EmployeeMapper.mapBank(p.getBankLegalDetails())
                                : null
                )

                // ===== ADDRESS =====
                .address(
                        p.getAddress() != null
                                ? EmployeeAddressMapper.toResponse(p.getAddress())
                                : null
                )

                // ===== NORMALIZED TABLES =====
                .workDetails(mapWork(trainee.getWorkDetails()))
                .educationDetails(mapEducation(trainee.getEducationDetails()))

                .createdAt(trainee.getCreatedAt())
                .updatedAt(trainee.getUpdatedAt())
                .build();
    }

    private static TraineeWorkDetailsResponseDTO mapWork(TraineeWorkDetails w) {
        if (w == null) return null;

        return TraineeWorkDetailsResponseDTO.builder()
                .branchName(w.getBranchName())
                .trainingPeriodMonths(w.getTrainingPeriodMonths())
                .internshipStartDate(w.getInternshipStartDate())
                .internshipEndDate(w.getInternshipEndDate())
                .trainingShiftTime(w.getTrainingShiftTime())
                .workMode(w.getWorkMode())
                .workingType(w.getWorkingType())
                .build();
    }

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