package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.entity.Attendance;
import com.gm.hrms.entity.AttendanceBreak;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.OfficeTiming;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AttendanceMapper;
import com.gm.hrms.repository.AttendanceBreakRepository;
import com.gm.hrms.repository.AttendanceRepository;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.OfficeTimingRepository;
import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final AttendanceBreakRepository breakRepo;
    private final EmployeeRepository employeeRepo;
    private final OfficeTimingRepository officeTimingRepo;

    //  CLOCK IN
    @Override
    public AttendanceResponseDTO clockIn(Long employeeId) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        LocalDate today = LocalDate.now();

        // Prevent multiple clock-in same day
        if(attendanceRepo.findByEmployeeIdAndDate(employeeId, today).isPresent()){
            throw new RuntimeException("Already clocked in today");
        }

        OfficeTiming timing = officeTimingRepo.findFirstByOrderByIdAsc()
                .orElseThrow(() ->
                        new RuntimeException("Office timing not configured"));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime lateAllowed =
                LocalDateTime.of(today, timing.getStartTime())
                        .plusMinutes(timing.getLateThresholdMinutes());

        boolean isLate = now.isAfter(lateAllowed);

        Attendance attendance = Attendance.builder()
                .employee(emp)
                .date(today)
                .clockIn(now)
                .lateIn(isLate)
                .build();

        return AttendanceMapper.toResponse(
                attendanceRepo.save(attendance)
        );
    }

    //  BREAK IN
    @Override
    public AttendanceResponseDTO breakIn(Long employeeId) {

        Attendance attendance = getTodayAttendance(employeeId);

        AttendanceBreak br = AttendanceBreak.builder()
                .attendance(attendance)
                .breakIn(LocalDateTime.now())
                .build();

        breakRepo.save(br);

        return AttendanceMapper.toResponse(attendance);
    }

    //  BREAK OUT
    @Override
    public AttendanceResponseDTO breakOut(Long employeeId) {

        Attendance attendance = getTodayAttendance(employeeId);

        AttendanceBreak br =
                breakRepo.findTopByAttendanceIdAndBreakOutIsNull(attendance.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Break not started"));

        br.setBreakOut(LocalDateTime.now());

        breakRepo.save(br);

        return AttendanceMapper.toResponse(attendance);
    }

    //  CLOCK OUT
    @Override
    public AttendanceResponseDTO clockOut(Long employeeId) {

        Attendance attendance = getTodayAttendance(employeeId);

        LocalDateTime now = LocalDateTime.now();
        attendance.setClockOut(now);

        long breakMinutes =
                breakRepo.findByAttendanceId(attendance.getId())
                        .stream()
                        .filter(b -> b.getBreakOut() != null)
                        .mapToLong(b ->
                                Duration.between(
                                        b.getBreakIn(),
                                        b.getBreakOut()
                                ).toMinutes()
                        ).sum();

        long workingMinutes =
                Duration.between(attendance.getClockIn(), now)
                        .toMinutes() - breakMinutes;

        attendance.setTotalBreakMinutes((int) breakMinutes);
        attendance.setTotalWorkingMinutes((int) workingMinutes);

        // Half Day Rule (Example < 4 Hours)
        if(workingMinutes < 240){
            attendance.setHalfDay(true);
        }

        return AttendanceMapper.toResponse(
                attendanceRepo.save(attendance)
        );
    }

    //  GET TODAY ATTENDANCE
    @Override
    public AttendanceResponseDTO getToday(Long employeeId) {
        return AttendanceMapper.toResponse(
                getTodayAttendance(employeeId)
        );
    }

    //  PRIVATE HELPER
    private Attendance getTodayAttendance(Long employeeId){

        return attendanceRepo
                .findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .orElseThrow(() ->
                        new RuntimeException("Not clocked in today"));
    }

    // get all attendance
    @Override
    public List<AttendanceResponseDTO> getAll() {

        return attendanceRepo.findAll()
                .stream()
                .map(AttendanceMapper::toResponse)
                .toList();
    }

    //Get By Employee
    @Override
    public List<AttendanceResponseDTO> getByEmployee(Long employeeId) {

        return attendanceRepo.findByEmployeeId(employeeId)
                .stream()
                .map(AttendanceMapper::toResponse)
                .toList();
    }

    // Get By Date Range
    @Override
    public List<AttendanceResponseDTO> getByDateRange(
            LocalDate start,
            LocalDate end) {

        return attendanceRepo.findByDateBetween(start, end)
                .stream()
                .map(AttendanceMapper::toResponse)
                .toList();
    }

}
