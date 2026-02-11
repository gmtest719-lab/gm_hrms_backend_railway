package com.gm.hrms.controller;

import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    //  CLOCK IN
    @PostMapping("/clock-in/{employeeId}")
    public ResponseEntity<?> clockIn(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.clockIn(employeeId));
    }

    //  BREAK IN
    @PostMapping("/break-in/{employeeId}")
    public ResponseEntity<?> breakIn(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.breakIn(employeeId));
    }

    //  BREAK OUT
    @PostMapping("/break-out/{employeeId}")
    public ResponseEntity<?> breakOut(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.breakOut(employeeId));
    }

    //  CLOCK OUT
    @PostMapping("/clock-out/{employeeId}")
    public ResponseEntity<?> clockOut(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.clockOut(employeeId));
    }

    //  TODAY ATTENDANCE
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<?> today(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.getToday(employeeId));
    }

    //  GET ALL ATTENDANCE
    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    //  GET BY EMPLOYEE
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId){
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }

    //  DATE RANGE (REPORT BASE)
    @GetMapping("/date-range")
    public ResponseEntity<?> getByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end){

        return ResponseEntity.ok(
                service.getByDateRange(start, end)
        );
    }
}
