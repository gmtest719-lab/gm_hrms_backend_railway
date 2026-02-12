package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.AttendanceMonthlyDTO;
import com.gm.hrms.dto.response.AttendanceReportDTO;
import com.gm.hrms.entity.Attendance;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.mapper.AttendanceReportMapper;
import com.gm.hrms.repository.AttendanceRepository;
import com.gm.hrms.service.AttendanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceRepository repo;
    private final AttendanceReportMapper mapper;

    @Override
    public List<AttendanceReportDTO> byDateRange(LocalDate s, LocalDate e){
        return repo.findByDateRange(s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceReportDTO> byEmployee(Long id, LocalDate s, LocalDate e){
        return repo.findByEmployeeIdAndDateBetween(id,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceReportDTO> byDepartment(Long id, LocalDate s, LocalDate e){
        return repo.findByDepartmentAndDateRange(id,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceReportDTO> late(LocalDate s, LocalDate e){
        return repo.findByLateInTrueAndDateBetween(s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceReportDTO> halfDay(LocalDate s, LocalDate e){
        return repo.findByHalfDayTrueAndDateBetween(s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceReportDTO> todayAll(){
        return repo.findByDate(LocalDate.now())
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceReportDTO> todayByDepartment(Long deptId){
        return repo.findByDate(LocalDate.now())
                .stream()
                .filter(a->a.getEmployee().getDepartment().getId().equals(deptId))
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<AttendanceReportDTO> todayByEmployee(Long empId){
        return repo.findByEmployeeIdAndDateBetween(
                empId,
                LocalDate.now(),
                LocalDate.now()
        ).stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<AttendanceMonthlyDTO> monthly(int m, int y){

        return repo.findMonthly(m,y).stream()
                .collect(Collectors.groupingBy(a->a.getEmployee().getId()))
                .values().stream()
                .map(this::buildMonthly)
                .toList();
    }

    @Override
    public AttendanceMonthlyDTO monthlyEmployee(Long empId, int m, int y){

        List<Attendance> list = repo.findMonthly(m,y)
                .stream()
                .filter(a->a.getEmployee().getId().equals(empId))
                .toList();

        return buildMonthly(list);
    }

    private AttendanceMonthlyDTO buildMonthly(List<Attendance> list){

        if(list.isEmpty()) return null;

        Employee e = list.getFirst().getEmployee();

        return AttendanceMonthlyDTO.builder()
                .employeeId(e.getId())
                .employeeName(e.getFirstName()+" "+e.getLastName())
                .totalDays(list.size())
                .presentDays((int) list.stream().filter(a->a.getClockIn()!=null).count())
                .lateDays((int) list.stream().filter(a->Boolean.TRUE.equals(a.getLateIn())).count())
                .halfDays((int) list.stream().filter(a->Boolean.TRUE.equals(a.getHalfDay())).count())
                .totalWorkingMinutes(list.stream()
                        .mapToInt(a->a.getTotalWorkingMinutes()==null?0:a.getTotalWorkingMinutes())
                        .sum())
                .build();
    }
}
