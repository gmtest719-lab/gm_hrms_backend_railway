package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceBreakLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceBreakLogRepository
        extends JpaRepository<AttendanceBreakLog,Long> {

    List<AttendanceBreakLog> findByAttendance_Id(Long attendanceId);

    AttendanceBreakLog findTopByAttendanceIdOrderByBreakStartDesc(Long attendanceId);

    @Query("""
    SELECT b FROM AttendanceBreakLog b
    WHERE b.attendance.id IN :attendanceIds
    AND b.breakStart = (
        SELECT MAX(b2.breakStart)
        FROM AttendanceBreakLog b2
        WHERE b2.attendance.id = b.attendance.id
    )
""")
    List<AttendanceBreakLog> findLatestPerAttendanceIds(
            @Param("attendanceIds") List<Long> attendanceIds);

}