package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.AttendanceRequestDTO;
import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.enums.ShiftType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AttendanceMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final AttendanceBreakLogRepository breakLogRepository;
    private final AttendanceCalculationRepository calculationRepository;
    private final PersonalInformationRepository personalInformationRepository;
    private final WorkProfileRepository workProfileRepository;

    // ✅ Common validation
    private void validateAttendanceRequest(AttendanceRequestDTO dto) {
        if (dto == null || dto.getPersonalInformationId() == null) {
            throw new InvalidRequestException("PersonalInformationId is required");
        }
    }

    @Override
    public AttendanceResponseDTO checkIn(AttendanceRequestDTO dto) {

        validateAttendanceRequest(dto);

        PersonalInformation person = personalInformationRepository
                .findById(dto.getPersonalInformationId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        Attendance existing =
                attendanceRepository
                        .findByPersonalInformationIdAndAttendanceDate(
                                person.getId(),
                                LocalDate.now())
                        .orElse(null);

        if(existing != null){
            throw new InvalidRequestException("Already checked in today");
        }

        WorkProfile profile =
                workProfileRepository
                        .findByPersonalInformationId(person.getId())
                        .orElse(null);

        Attendance attendance =
                AttendanceMapper.createAttendance(person, profile);

        if (attendance.getCheckIn() == null) {
            throw new InvalidRequestException("Check-in time cannot be null");
        }

        attendanceRepository.save(attendance);

        attendanceLogRepository.save(
                AttendanceMapper.createCheckInLog(person)
        );

        return AttendanceMapper.toResponse(attendance, null);
    }

    @Override
    public AttendanceResponseDTO checkOut(AttendanceRequestDTO dto) {

        validateAttendanceRequest(dto);

        Attendance attendance =
                attendanceRepository
                        .findByPersonalInformationIdAndAttendanceDate(
                                dto.getPersonalInformationId(),
                                LocalDate.now())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Attendance not found"));

        if (attendance.getCheckIn() == null) {
            throw new InvalidRequestException("Cannot checkout without check-in");
        }

        if(attendance.getCheckOut()!=null){
            throw new InvalidRequestException("Already checked out");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(attendance.getCheckIn())) {
            throw new InvalidRequestException("Checkout time cannot be before check-in");
        }

        attendance.setCheckOut(now);
        attendanceRepository.save(attendance);

        attendanceLogRepository.save(
                AttendanceMapper.createCheckOutLog(
                        attendance.getPersonalInformation())
        );

        AttendanceCalculation calc = calculateAttendance(attendance);

        return AttendanceMapper.toResponse(attendance, calc);
    }

    @Override
    public AttendanceResponseDTO breakStart(AttendanceRequestDTO dto) {

        validateAttendanceRequest(dto);

        Attendance attendance =
                attendanceRepository
                        .findByPersonalInformationIdAndAttendanceDate(
                                dto.getPersonalInformationId(),
                                LocalDate.now())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Attendance not found"));

        if (attendance.getCheckIn() == null) {
            throw new InvalidRequestException("Cannot start break before check-in");
        }

        if(attendance.getCheckOut()!=null){
            throw new InvalidRequestException("Cannot start break after checkout");
        }

        AttendanceBreakLog lastBreak =
                breakLogRepository
                        .findTopByAttendanceIdOrderByBreakStartDesc(
                                attendance.getId());

        if(lastBreak != null && lastBreak.getBreakEnd()==null){
            throw new InvalidRequestException("Break already started");
        }

        breakLogRepository.save(
                AttendanceMapper.createBreakStart(attendance)
        );

        return AttendanceMapper.toResponse(attendance, null);
    }

    @Override
    public AttendanceResponseDTO breakEnd(AttendanceRequestDTO dto) {

        validateAttendanceRequest(dto);

        Attendance attendance =
                attendanceRepository
                        .findByPersonalInformationIdAndAttendanceDate(
                                dto.getPersonalInformationId(),
                                LocalDate.now())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Attendance not found"));

        if(attendance.getCheckOut()!=null){
            throw new InvalidRequestException("Cannot end break after checkout");
        }

        AttendanceBreakLog breakLog =
                breakLogRepository
                        .findTopByAttendanceIdOrderByBreakStartDesc(
                                attendance.getId());

        if(breakLog==null){
            throw new InvalidRequestException("Break not started");
        }

        if (breakLog.getBreakEnd() != null) {
            throw new InvalidRequestException("Break already ended");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(breakLog.getBreakStart())) {
            throw new InvalidRequestException("Break end cannot be before break start");
        }

        breakLog.setBreakEnd(now);

        int minutes =
                (int)Duration.between(
                        breakLog.getBreakStart(),
                        breakLog.getBreakEnd()
                ).toMinutes();

        if (minutes < 0) {
            throw new InvalidRequestException("Invalid break duration");
        }

        breakLog.setDurationMinutes(minutes);

        breakLogRepository.save(breakLog);

        return AttendanceMapper.toResponse(attendance, null);
    }

    @Override
    public AttendanceResponseDTO getTodayAttendance(Long personalInformationId) {

        if (personalInformationId == null) {
            throw new InvalidRequestException("PersonalInformationId is required");
        }

        Attendance attendance =
                attendanceRepository
                        .findByPersonalInformationIdAndAttendanceDate(
                                personalInformationId,
                                LocalDate.now())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Attendance not found"));

        AttendanceCalculation calc =
                calculationRepository
                        .findByAttendanceId(attendance.getId())
                        .orElse(null);

        return AttendanceMapper.toResponse(attendance, calc);
    }

    @Override
    public PageResponseDTO<AttendanceResponseDTO> getAllAttendance(Pageable pageable) {

        if (pageable == null) {
            throw new InvalidRequestException("Pageable cannot be null");
        }

        Page<Attendance> page = attendanceRepository.findAll(pageable);

        List<Long> ids = page.getContent()
                .stream()
                .map(Attendance::getId)
                .toList();

        List<Attendance> attendanceWithCalc =
                attendanceRepository.findAllWithCalculationByIds(ids);

        Map<Long, Attendance> attendanceMap =
                attendanceWithCalc.stream()
                        .collect(Collectors.toMap(Attendance::getId, a -> a));

        List<AttendanceResponseDTO> content =
                page.getContent().stream()
                        .map(a -> {
                            Attendance fullData = attendanceMap.get(a.getId());
                            return AttendanceMapper.toResponse(
                                    fullData,
                                    fullData.getCalculation()
                            );
                        })
                        .toList();

        return PageResponseDTO.<AttendanceResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private AttendanceCalculation calculateAttendance(Attendance attendance){

        long totalMinutes =
                Duration.between(
                                attendance.getCheckIn(),
                                attendance.getCheckOut())
                        .toMinutes();

        int breakMinutes =
                breakLogRepository
                        .findByAttendanceId(attendance.getId())
                        .stream()
                        .mapToInt(AttendanceBreakLog::getDurationMinutes)
                        .sum();

        int workMinutes = (int) totalMinutes - breakMinutes;

        int lateMinutes = 0;
        int overtimeMinutes = 0;

        AttendanceStatus status = AttendanceStatus.PRESENT;

        WorkProfile profile = attendance.getWorkProfile();

        if(profile!=null && profile.getShift()!=null){

            Shift shift = profile.getShift();

            LocalTime shiftStart =
                    resolveShiftStartTime(
                            shift,
                            attendance.getAttendanceDate());

            if(shiftStart!=null && attendance.getCheckIn()!=null){

                int grace =
                        shift.getGraceMinutes()==null ?
                                0 :
                                shift.getGraceMinutes();

                LocalDateTime allowedTime =
                        attendance.getAttendanceDate()
                                .atTime(shiftStart)
                                .plusMinutes(grace);

                if(attendance.getCheckIn().isAfter(allowedTime)){

                    lateMinutes =
                            (int)Duration.between(
                                            allowedTime,
                                            attendance.getCheckIn())
                                    .toMinutes();
                }
            }

            if(shift.getMinimumWorkHours()!=null){

                int requiredMinutes =
                        shift.getMinimumWorkHours() * 60;

                if(workMinutes > requiredMinutes){
                    overtimeMinutes = workMinutes - requiredMinutes;
                }

                int lateCount =
                        calculationRepository
                                .countMonthlyLate(
                                        attendance.getPersonalInformation().getId(),
                                        attendance.getAttendanceDate().getMonthValue(),
                                        attendance.getAttendanceDate().getYear()
                                );

                if(lateMinutes > 0){
                    lateCount++;
                }

                if(lateCount >= 3 && workMinutes < requiredMinutes){
                    status = AttendanceStatus.HALF_DAY;
                }

                if(workMinutes < requiredMinutes/2){
                    status = AttendanceStatus.HALF_DAY;
                }
            }
        }

        attendance.setStatus(status);
        attendanceRepository.save(attendance);

        AttendanceCalculation calc =
                AttendanceMapper.createCalculation(
                        attendance,
                        workMinutes,
                        breakMinutes,
                        lateMinutes,
                        overtimeMinutes
                );

        return calculationRepository.save(calc);
    }

    private LocalTime resolveShiftStartTime(Shift shift, LocalDate date){

        if(shift.getShiftType()== ShiftType.NORMAL){

            ShiftTiming timing = shift.getTiming();

            return timing!=null ? timing.getStartTime() : null;
        }

        DayOfWeek today = date.getDayOfWeek();

        return shift.getDayConfigs()
                .stream()
                .filter(d -> d.getDayOfWeek() == today)
                .findFirst()
                .map(ShiftDayConfig::getStartTime)
                .orElse(null);
    }

    @Override
    public AttendanceResponseDTO correctAttendance(
            AttendanceCorrectionRequestDTO dto){

        if (dto == null || dto.getAttendanceId() == null) {
            throw new InvalidRequestException("AttendanceId is required");
        }

        Attendance attendance =
                attendanceRepository.findById(dto.getAttendanceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Attendance not found"));

        if(dto.getCheckIn()!=null && dto.getCheckOut()!=null){
            if(dto.getCheckOut().isBefore(dto.getCheckIn())){
                throw new InvalidRequestException("CheckOut cannot be before CheckIn");
            }
        }

        if(dto.getCheckIn()!=null){
            attendance.setCheckIn(dto.getCheckIn());
        }

        if(dto.getCheckOut()!=null){
            attendance.setCheckOut(dto.getCheckOut());
        }

        attendanceRepository.save(attendance);

        AttendanceCalculation calc =
                calculateAttendance(attendance);

        return AttendanceMapper.toResponse(attendance, calc);
    }
}