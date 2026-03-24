package com.gm.hrms.repository;

import com.gm.hrms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
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

}