package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.UserCreateRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PersonalInformationService personalService;
    private final EmployeeService employeeService;
//    private final InternService internService;
    private final TraineeService traineeService;

    @Override
    public UserCreateResponseDTO create(UserCreateRequestDTO dto) {

        // 1️⃣ Create Personal Information
        PersonalInformationResponseDTO person =
                personalService.create(dto.getPersonalInformation());

        EmploymentType type =
                dto.getPersonalInformation().getEmploymentType();

        // 2️⃣ Route Based on Type
        switch (type) {

            case EMPLOYEE -> {
                if (dto.getEmployee() == null)
                    throw new InvalidRequestException("Employee data required");

                return employeeService.create(
                        dto.getEmployee(),
                        person.getId()
                );
            }

            case INTERN -> {
//                if (dto.getIntern() == null)
//                    throw new InvalidRequestException("Intern data required");
//
//                return internService.create(
//                        dto.getIntern(),
//                        person.getId()
//                );
            }

            case TRAINEE -> {
                if (dto.getTrainee() == null)
                    throw new InvalidRequestException("Trainee data required");

                return traineeService.create(
                        dto.getTrainee(),
                        person.getId()
                );
            }
        }

        throw new InvalidRequestException("Invalid employment type");
    }
}