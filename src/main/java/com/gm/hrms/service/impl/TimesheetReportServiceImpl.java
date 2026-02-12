package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.TimesheetMonthlyDTO;
import com.gm.hrms.dto.response.TimesheetReportDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Timesheet;
import com.gm.hrms.mapper.TimesheetReportMapper;
import com.gm.hrms.repository.TimesheetRepository;
import com.gm.hrms.service.TimesheetReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesheetReportServiceImpl implements TimesheetReportService {

    private final TimesheetRepository repo;
    private final TimesheetReportMapper mapper;

    @Override
    public List<TimesheetReportDTO> byDateRange(LocalDate s, LocalDate e){
        return repo.findByWorkDateBetween(s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetReportDTO> byEmployee(Long id, LocalDate s, LocalDate e){
        return repo.findByEmployeeIdAndWorkDateBetween(id,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetReportDTO> byProject(Long id, LocalDate s, LocalDate e){
        return repo.findByProjectIdAndWorkDateBetween(id,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetReportDTO> byStatus(String status, LocalDate s, LocalDate e){
        return repo.findByStatusAndWorkDateBetween(status,s,e)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetReportDTO> todayAll(){
        return repo.findByWorkDate(LocalDate.now())
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetReportDTO> todayByEmployee(Long id){
        return repo.findByEmployeeIdAndWorkDateBetween(
                id,
                LocalDate.now(),
                LocalDate.now()
        ).stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetReportDTO> todayByProject(Long id){
        return repo.findByProjectIdAndWorkDateBetween(
                id,
                LocalDate.now(),
                LocalDate.now()
        ).stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<TimesheetMonthlyDTO> monthly(int m, int y){

        return repo.findMonthly(m,y)
                .stream()
                .collect(Collectors.groupingBy(t->t.getEmployee().getId()))
                .values()
                .stream()
                .map(this::buildMonthly)
                .toList();
    }

    @Override
    public TimesheetMonthlyDTO monthlyEmployee(Long empId, int m, int y){

        List<Timesheet> list = repo.findMonthly(m,y)
                .stream()
                .filter(t->t.getEmployee().getId().equals(empId))
                .toList();

        return buildMonthly(list);
    }

    private TimesheetMonthlyDTO buildMonthly(List<Timesheet> list){

        if(list.isEmpty()) return null;

        Employee e = list.get(0).getEmployee();

        return TimesheetMonthlyDTO.builder()
                .employeeId(e.getId())
                .employeeName(e.getFirstName()+" "+e.getLastName())
                .totalEntries(list.size())
                .totalHours(
                        list.stream()
                                .mapToDouble(t->t.getHours()==null?0:t.getHours())
                                .sum()
                )
                .build();
    }
}

