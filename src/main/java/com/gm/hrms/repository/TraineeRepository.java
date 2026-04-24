package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    boolean existsByTraineeCode(String traineeCode);

    boolean existsByTraineeCodeAndIdNot(String traineeCode, Long id);

    Optional<Trainee> findByTraineeCode(String traineeCode);

    boolean existsByPersonalInformationId(Long personalInformationId);

    Optional<Trainee> findByPersonalInformationId(Long personalId);

    List<Trainee> findByPersonalInformationIdIn(List<Long> ids);

}