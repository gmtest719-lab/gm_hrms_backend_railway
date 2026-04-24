package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.UserCodeDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.InternRepository;
import com.gm.hrms.repository.TraineeRepository;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCodeResolverServiceImpl implements UserCodeResolverService {

    private final EmployeeRepository employeeRepository;
    private final InternRepository internRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public String getCode(Long personalId) {

        // 🔹 EMPLOYEE
        Employee emp = employeeRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (emp != null) return emp.getEmployeeCode();

        // 🔹 INTERN
        Intern intern = internRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (intern != null) return intern.getInternCode();

        // 🔹 TRAINEE
        Trainee trainee = traineeRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (trainee != null) return trainee.getTraineeCode();

        return "N/A";
    }

    @Override
    public UserCodeDTO resolve(Long personalInformationId) {
        return UserCodeDTO.builder()
                .employeeCode(employeeRepository
                        .findByPersonalInformationId(personalInformationId)
                        .map(Employee::getEmployeeCode).orElse(null))
                .traineeCode(traineeRepository
                        .findByPersonalInformationId(personalInformationId)
                        .map(Trainee::getTraineeCode).orElse(null))
                .internCode(internRepository
                        .findByPersonalInformationId(personalInformationId)
                        .map(Intern::getInternCode).orElse(null))
                .build();
    }

    @Override
    public Map<Long, UserCodeDTO> resolveAll(List<Long> personalInformationIds) {

        Map<Long, String> empCodes = employeeRepository
                .findByPersonalInformationIdIn(personalInformationIds).stream()
                .collect(Collectors.toMap(
                        e -> e.getPersonalInformation().getId(),
                        Employee::getEmployeeCode));

        Map<Long, String> traineeCodes = traineeRepository
                .findByPersonalInformationIdIn(personalInformationIds).stream()
                .collect(Collectors.toMap(
                        t -> t.getPersonalInformation().getId(),
                        Trainee::getTraineeCode));

        Map<Long, String> internCodes = internRepository
                .findByPersonalInformationIdIn(personalInformationIds).stream()
                .collect(Collectors.toMap(
                        i -> i.getPersonalInformation().getId(),
                        Intern::getInternCode));

        return personalInformationIds.stream().collect(Collectors.toMap(
                id -> id,
                id -> UserCodeDTO.builder()
                        .employeeCode(empCodes.get(id))
                        .traineeCode(traineeCodes.get(id))
                        .internCode(internCodes.get(id))
                        .build()
        ));
    }

}