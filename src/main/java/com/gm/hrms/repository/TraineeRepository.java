package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    boolean existsByTraineeCode(String traineeCode);
}