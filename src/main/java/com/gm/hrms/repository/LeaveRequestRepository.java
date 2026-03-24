package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByPersonalId(Long personalId, Pageable pageable);
    boolean existsByPersonalIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long personalId,
            LocalDate end,
            LocalDate start
    );
}