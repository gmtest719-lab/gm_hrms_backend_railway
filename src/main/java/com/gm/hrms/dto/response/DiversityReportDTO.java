package com.gm.hrms.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiversityReportDTO {

    // ── Overall ───────────────────────────────────────────────
    private long totalEmployees;

    // ── Gender breakdown ──────────────────────────────────────
    private long maleCount;
    private long femaleCount;
    private long otherGenderCount;

    // ── Employment type breakdown ─────────────────────────────
    private long employeeCount;
    private long traineeCount;
    private long internCount;

    // ── Department breakdown: deptName → count ────────────────
    private Map<String, Long> byDepartment;

    // ── Gender % ──────────────────────────────────────────────
    private double malePercent;
    private double femalePercent;
    private double otherPercent;
}