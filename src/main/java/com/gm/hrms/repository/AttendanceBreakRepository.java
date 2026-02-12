package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceBreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceBreakRepository extends JpaRepository<AttendanceBreak, Long> {

    List<AttendanceBreak> findByAttendanceId(Long attendanceId);

    Optional<AttendanceBreak>
    findTopByAttendanceIdAndBreakOutIsNull(Long attendanceId);
}
