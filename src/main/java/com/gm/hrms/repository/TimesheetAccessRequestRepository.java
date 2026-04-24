package com.gm.hrms.repository;

import com.gm.hrms.entity.TimesheetAccessRequest;
import com.gm.hrms.enums.TimesheetAccessStatus;
import com.gm.hrms.enums.TimesheetAccessType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimesheetAccessRequestRepository
        extends JpaRepository<TimesheetAccessRequest, Long> {

    Optional<TimesheetAccessRequest> findByPerson_IdAndRequestedDateAndAccessTypeAndStatus(
            Long personId,
            LocalDate requestedDate,
            TimesheetAccessType accessType,
            TimesheetAccessStatus status
    );

    List<TimesheetAccessRequest> findByPerson_IdOrderByCreatedAtDesc(Long personId);

    List<TimesheetAccessRequest> findByStatusOrderByCreatedAtAsc(TimesheetAccessStatus status);
}