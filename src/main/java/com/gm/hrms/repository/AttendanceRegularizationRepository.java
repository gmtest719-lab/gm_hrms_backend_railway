package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceRegularization;
import com.gm.hrms.enums.RegularizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRegularizationRepository
        extends JpaRepository<AttendanceRegularization, Long> {

    Optional<AttendanceRegularization> findByAttendanceIdAndStatus(
            Long attendanceId, RegularizationStatus status);

    boolean existsByAttendanceIdAndStatus(
            Long attendanceId, RegularizationStatus status);

    @Query("""
        SELECT r FROM AttendanceRegularization r
        JOIN FETCH r.attendance a
        JOIN FETCH r.requestedBy p
        WHERE (:status IS NULL OR r.status = :status)
        AND a.attendanceDate >= :fromDate
        AND a.attendanceDate <= :toDate
        AND (:personalId = 0L OR p.id = :personalId)
        ORDER BY a.attendanceDate DESC
    """)
    Page<AttendanceRegularization> findWithFilters(
            @Param("status")     RegularizationStatus status,
            @Param("fromDate")   LocalDate fromDate,
            @Param("toDate")     LocalDate toDate,
            @Param("personalId") Long personalId,
            Pageable pageable
    );
}