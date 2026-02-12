package com.gm.hrms.controller;

import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.service.LeaveReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/leaves")
@RequiredArgsConstructor
public class LeaveReportController {

    private final LeaveReportService service;

    @GetMapping("/date-range")
    public ResponseEntity<?> dateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byDateRange(startDate,endDate));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<?> emp(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byEmployee(id,startDate,endDate));
    }

    @GetMapping("/department/{id}")
    public ResponseEntity<?> dept(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byDepartment(id,startDate,endDate));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> status(
            @PathVariable LeaveStatus status,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byStatus(status,startDate,endDate));
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<?> type(
            @PathVariable Long typeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byLeaveType(typeId,startDate,endDate));
    }

    @GetMapping("/today")
    public ResponseEntity<?> today(){
        return ResponseEntity.ok(service.todayAll());
    }

    @GetMapping("/today/employee/{id}")
    public ResponseEntity<?> todayEmp(@PathVariable Long id){
        return ResponseEntity.ok(service.todayByEmployee(id));
    }

    @GetMapping("/today/department/{id}")
    public ResponseEntity<?> todayDept(@PathVariable Long id){
        return ResponseEntity.ok(service.todayByDepartment(id));
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> monthly(
            @RequestParam int month,
            @RequestParam int year){
        return ResponseEntity.ok(service.monthly(month,year));
    }

    @GetMapping("/monthly/employee/{id}")
    public ResponseEntity<?> monthlyEmp(
            @PathVariable Long id,
            @RequestParam int month,
            @RequestParam int year){
        return ResponseEntity.ok(service.monthlyEmployee(id,month,year));
    }
}

