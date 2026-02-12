package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.LeaveMonthlyDTO;
import com.gm.hrms.dto.response.LeaveReportDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.LeaveApplication;
import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.mapper.LeaveReportMapper;
import com.gm.hrms.repository.LeaveApplicationRepository;
import com.gm.hrms.service.LeaveReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveReportServiceImpl implements LeaveReportService {

    private final LeaveApplicationRepository repo;
    private final LeaveReportMapper mapper;

    @Override
    public List<LeaveReportDTO> byDateRange(LocalDate s, LocalDate e){
        return repo.findByStartDateBetween(s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> byEmployee(Long id, LocalDate s, LocalDate e){
        return repo.findByEmployeeIdAndStartDateBetween(id,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> byDepartment(Long id, LocalDate s, LocalDate e){
        return repo.findByDepartment(id,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> byStatus(
            LeaveStatus status,
            LocalDate s,
            LocalDate e){
        return repo.findByStatusAndStartDateBetween(status,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> byLeaveType(
            Long typeId,
            LocalDate s,
            LocalDate e){
        return repo.findByLeaveTypeIdAndStartDateBetween(typeId,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> todayAll(){
        return repo.findByStartDate(LocalDate.now())
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> todayByEmployee(Long empId){
        return repo.findByEmployeeIdAndStartDateBetween(
                empId,
                LocalDate.now(),
                LocalDate.now()
        ).stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveReportDTO> todayByDepartment(Long deptId){
        return repo.findByDepartment(
                deptId,
                LocalDate.now(),
                LocalDate.now()
        ).stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<LeaveMonthlyDTO> monthly(int m, int y){

        return repo.findMonthly(m,y)
                .stream()
                .collect(Collectors.groupingBy(l->l.getEmployee().getId()))
                .values()
                .stream()
                .map(this::buildMonthly)
                .toList();
    }

    @Override
    public LeaveMonthlyDTO monthlyEmployee(Long empId, int m, int y){

        List<LeaveApplication> list = repo.findMonthly(m,y)
                .stream()
                .filter(l->l.getEmployee().getId().equals(empId))
                .toList();

        return buildMonthly(list);
    }

    private LeaveMonthlyDTO buildMonthly(List<LeaveApplication> list){

        if(list.isEmpty()) return null;

        Employee e = list.get(0).getEmployee();

        return LeaveMonthlyDTO.builder()
                .employeeId(e.getId())
                .employeeName(e.getFirstName()+" "+e.getLastName())
                .totalLeaves(list.size())
                .approvedLeaves((int) list.stream()
                        .filter(l->l.getStatus()==LeaveStatus.APPROVED).count())
                .rejectedLeaves((int) list.stream()
                        .filter(l->l.getStatus()==LeaveStatus.REJECTED).count())
                .pendingLeaves((int) list.stream()
                        .filter(l->l.getStatus()==LeaveStatus.PENDING).count())
                .cancelledLeaves((int) list.stream()
                        .filter(l->l.getStatus()==LeaveStatus.CANCELLED).count())
                .build();
    }
}

