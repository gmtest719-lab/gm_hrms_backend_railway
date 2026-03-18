package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    boolean existsByTraineeCode(String traineeCode);

    //  REQUIRED FOR UPDATE (avoid duplicate with self)
    boolean existsByTraineeCodeAndIdNot(String traineeCode, Long id);

    //  OPTIONAL (good practice)
    Optional<Trainee> findByTraineeCode(String traineeCode);

    //  OPTIONAL (consistency with Intern)
    boolean existsByPersonalInformationId(Long personalInformationId);
}