package com.gm.hrms.repository;

import com.gm.hrms.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance,Long> {

    Optional<Attendance> findByPersonalInformationIdAndAttendanceDate(
            Long personalInformationId,
            LocalDate attendanceDate
    );

    boolean existsByPersonalInformationIdAndAttendanceDate(
            Long personalInformationId,
            LocalDate attendanceDate
    );

    //  Pagination query (correct count)
    Page<Attendance> findAll(Pageable pageable);


    //  Fetch calculation in bulk
    @Query("""
    SELECT a FROM Attendance a
    LEFT JOIN FETCH a.calculation
    WHERE a.id IN :ids
""")
    List<Attendance> findAllWithCalculationByIds(List<Long> ids);
    List<Attendance> findByPersonalInformationIdAndAttendanceDateBetween(
            Long personalInformationId,
            LocalDate from,
            LocalDate to
    );

}