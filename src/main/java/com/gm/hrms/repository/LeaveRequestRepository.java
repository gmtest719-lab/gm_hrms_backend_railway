package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByPersonalId(Long personalId);

    boolean existsByPersonalIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long personalId,
            LocalDate end,
            LocalDate start
    );
}