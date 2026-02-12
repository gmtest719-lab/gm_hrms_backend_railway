package com.gm.hrms.controller;

import com.gm.hrms.service.TimesheetReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/timesheets")
@RequiredArgsConstructor
public class TimesheetReportController {

    private final TimesheetReportService service;

    @GetMapping("/date-range")
    public ResponseEntity<?> dateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byDateRange(startDate,endDate));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<?> employee(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byEmployee(id,startDate,endDate));
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<?> project(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byProject(id,startDate,endDate));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> status(
            @PathVariable String status,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byStatus(status,startDate,endDate));
    }

    @GetMapping("/today")
    public ResponseEntity<?> today(){
        return ResponseEntity.ok(service.todayAll());
    }

    @GetMapping("/today/employee/{id}")
    public ResponseEntity<?> todayEmp(@PathVariable Long id){
        return ResponseEntity.ok(service.todayByEmployee(id));
    }

    @GetMapping("/today/project/{id}")
    public ResponseEntity<?> todayProj(@PathVariable Long id){
        return ResponseEntity.ok(service.todayByProject(id));
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

