package com.gm.hrms.controller;

import com.gm.hrms.service.AttendanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/attendance")
@RequiredArgsConstructor
public class AttendanceReportController {

    private final AttendanceReportService service;

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

    @GetMapping("/department/{id}")
    public ResponseEntity<?> dept(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.byDepartment(id,startDate,endDate));
    }

    @GetMapping("/late")
    public ResponseEntity<?> late(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.late(startDate,endDate));
    }

    @GetMapping("/half-day")
    public ResponseEntity<?> half(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate){
        return ResponseEntity.ok(service.halfDay(startDate,endDate));
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
