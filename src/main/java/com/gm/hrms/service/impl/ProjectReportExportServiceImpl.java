// com/gm/hrms/service/impl/ProjectReportExportServiceImpl.java
package com.gm.hrms.service.impl;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.service.ProjectReportExportService;
import com.gm.hrms.service.ProjectReportService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectReportExportServiceImpl implements ProjectReportExportService {

    private final ProjectReportService reportService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Pageable ALL = PageRequest.of(0, Integer.MAX_VALUE);

    // ======================================================
    // PDF
    // ======================================================
    @Override
    public void exportPdf(String type, ProjectReportFilterDTO filter,
                          CustomUserDetails user, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"project-report-" + type + "-"
                        + LocalDate.now() + ".pdf\"");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4.rotate())) {

            document.setMargins(30, 30, 30, 30);

            switch (type.toLowerCase()) {
                case "master"              -> writeMasterPdf(document, filter, user);
                case "timeline"            -> writeTimelinePdf(document, filter, user);
                case "resource-allocation" -> writeAllocationPdf(document, filter);
                case "employee-wise"       -> writeEmployeeWisePdf(document, filter, user);
                case "effort"              -> writeEffortPdf(document, filter, user);
                case "cost"                -> writeCostPdf(document, filter);
                default -> throw new InvalidRequestException("Unknown report type: " + type);
            }
        }
    }

    // ── PDF writers ──────────────────────────────────────────────────────────

    private void writeMasterPdf(Document doc, ProjectReportFilterDTO filter,
                                CustomUserDetails user) {
        List<ProjectMasterReportDTO> rows = reportService
                .getProjectMasterReport(filter, ALL, user).getData().getContent();

        addTitle(doc, "Project Master Report");
        Table t = pdfTable(new float[]{2f, 1.5f, 2f, 1.2f, 1.2f, 1.2f, 1f, 1f, 1f});
        pdfHeader(t, "Project Name", "Code", "Client", "Start", "End",
                "Status", "Total", "Delayed", "Overdue (days)");
        rows.forEach(r -> {
            pdfCell(t, r.getProjectName());
            pdfCell(t, r.getProjectCode());
            pdfCell(t, nullStr(r.getClientName()));
            pdfCell(t, fmt(r.getStartDate()));
            pdfCell(t, fmt(r.getEndDate()));
            pdfCell(t, nullStr(r.getStatus()));
            pdfCell(t, String.valueOf(r.getTotalAssignees()));
            pdfCell(t, r.isDelayed() ? "YES" : "NO");
            pdfCell(t, String.valueOf(r.getDaysOverdue()));
        });
        doc.add(t);
    }

    private void writeTimelinePdf(Document doc, ProjectReportFilterDTO filter,
                                  CustomUserDetails user) {
        List<ProjectTimelineReportDTO> rows = reportService
                .getProjectTimelineReport(filter, ALL, user).getData().getContent();

        addTitle(doc, "Project Timeline Report");
        Table t = pdfTable(new float[]{2f, 1.2f, 1.2f, 1.2f, 1f, 1f, 1f, 1f, 1f});
        pdfHeader(t, "Project Name", "Code", "Start", "End",
                "Duration", "Elapsed", "Progress %", "Delayed", "Days Overdue");
        rows.forEach(r -> {
            pdfCell(t, r.getProjectName());
            pdfCell(t, r.getProjectCode());
            pdfCell(t, fmt(r.getStartDate()));
            pdfCell(t, fmt(r.getEndDate()));
            pdfCell(t, r.getDurationDays() + " d");
            pdfCell(t, r.getElapsedDays() + " d");
            pdfCell(t, r.getProgressPercent() + "%");
            pdfCell(t, r.isDelayed() ? "YES" : "NO");
            pdfCell(t, String.valueOf(r.getDaysOverdue()));
        });
        doc.add(t);
    }

    private void writeAllocationPdf(Document doc, ProjectReportFilterDTO filter) {
        List<ResourceAllocationReportDTO> rows = reportService
                .getResourceAllocationReport(filter, ALL).getData().getContent();

        addTitle(doc, "Resource Allocation Report");
        rows.forEach(proj -> {
            doc.add(new Paragraph("Project: " + proj.getProjectName()
                    + " [" + proj.getProjectCode() + "]  |  Status: " + proj.getProjectStatus()
                    + "  |  Total Assignees: " + proj.getTotalAssignees())
                    .setBold().setFontSize(10).setMarginTop(10));

            if (proj.getAssignees().isEmpty()) {
                // FIX #1: changed setItalics() → setItalic()
                doc.add(new Paragraph("  No assignees").setItalic().setFontSize(9));
                return;
            }

            Table t = pdfTable(new float[]{2f, 1.5f, 1.2f, 1.5f, 1.5f, 1.5f});
            pdfHeader(t, "Name", "Code", "Type", "Role", "Department", "Designation");
            proj.getAssignees().forEach(a -> {
                pdfCell(t, nullStr(a.getAssigneeName()));
                pdfCell(t, nullStr(a.getAssigneeCode()));
                pdfCell(t, nullStr(a.getAssigneeType()));
                pdfCell(t, nullStr(a.getRoleInProject()));
                pdfCell(t, nullStr(a.getDepartment()));
                pdfCell(t, nullStr(a.getDesignation()));
            });
            doc.add(t);
        });
    }

    private void writeEmployeeWisePdf(Document doc, ProjectReportFilterDTO filter,
                                      CustomUserDetails user) {
        List<EmployeeProjectReportDTO> rows = reportService
                .getEmployeeWiseProjectReport(filter, ALL, user).getData().getContent();

        addTitle(doc, "Employee-wise Project Report");
        Table t = pdfTable(new float[]{2f, 1.2f, 1.5f, 1.2f, 1.2f, 1.2f, 1.5f, 1f, 1f});
        pdfHeader(t, "Project Name", "Code", "Client", "Start", "End",
                "Status", "Role", "Delayed", "Overdue (d)");
        rows.forEach(r -> {
            pdfCell(t, r.getProjectName());
            pdfCell(t, r.getProjectCode());
            pdfCell(t, nullStr(r.getClientName()));
            pdfCell(t, fmt(r.getStartDate()));
            pdfCell(t, fmt(r.getEndDate()));
            pdfCell(t, nullStr(r.getProjectStatus()));
            pdfCell(t, nullStr(r.getRoleInProject()));
            pdfCell(t, r.isDelayed() ? "YES" : "NO");
            pdfCell(t, String.valueOf(r.getDaysOverdue()));
        });
        doc.add(t);
    }

    private void writeEffortPdf(Document doc, ProjectReportFilterDTO filter,
                                CustomUserDetails user) {
        List<ProjectEffortReportDTO> rows = reportService
                .getProjectEffortReport(filter, ALL, user).getData().getContent();

        addTitle(doc, "Project Effort Report");
        Table t = pdfTable(new float[]{2f, 1.2f, 1.5f, 1.2f, 1.2f, 1.5f, 1.5f});
        pdfHeader(t, "Project Name", "Code", "Status", "Start", "End",
                "Total Assignees", "Total Hours");
        rows.forEach(r -> {
            pdfCell(t, r.getProjectName());
            pdfCell(t, r.getProjectCode());
            pdfCell(t, nullStr(r.getProjectStatus()));
            pdfCell(t, fmt(r.getStartDate()));
            pdfCell(t, fmt(r.getEndDate()));
            pdfCell(t, String.valueOf(r.getTotalAssignees()));
            pdfCell(t, r.getTotalHours() != null
                    ? String.valueOf(r.getTotalHours()) : "N/A (Timesheet)");
        });
        doc.add(t);
    }

    private void writeCostPdf(Document doc, ProjectReportFilterDTO filter) {
        List<ProjectCostReportDTO> rows = reportService
                .getProjectCostReport(filter, ALL).getData().getContent();

        addTitle(doc, "Project Cost Report");
        Table t = pdfTable(new float[]{2f, 1.2f, 1.2f, 1.5f, 1.5f, 1.5f, 1.5f});
        pdfHeader(t, "Project Name", "Code", "Status",
                "Budget (₹)", "Actual (₹)", "Variance (₹)", "Variance %");
        rows.forEach(r -> {
            pdfCell(t, r.getProjectName());
            pdfCell(t, r.getProjectCode());
            pdfCell(t, nullStr(r.getStatus()));
            pdfCell(t, r.getBudgetAmount() != null ? r.getBudgetAmount().toString() : "N/A");
            pdfCell(t, r.getActualCost() != null ? r.getActualCost().toString() : "N/A");
            pdfCell(t, r.getVariance() != null ? r.getVariance().toString() : "N/A");
            pdfCell(t, r.getVariancePercent() != null
                    ? String.format("%.2f%%", r.getVariancePercent()) : "N/A");
        });
        doc.add(t);
    }

    // ── PDF helpers ──────────────────────────────────────────────────────────

    private void addTitle(Document doc, String title) {
        doc.add(new Paragraph(title)
                .setFontSize(16).setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(12));
        doc.add(new Paragraph("Generated on: " + LocalDate.now().format(FMT))
                .setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setMarginBottom(6));
    }

    private Table pdfTable(float[] widths) {
        Table t = new Table(UnitValue.createPercentArray(widths));
        t.setWidth(UnitValue.createPercentValue(100));
        return t;
    }

    // FIX #2: Use fully qualified com.itextpdf.layout.element.Cell to avoid
    // conflict with org.apache.poi.ss.usermodel.Cell imported via wildcard.
    private void pdfHeader(Table t, String... headers) {
        for (String h : headers) {
            com.itextpdf.layout.element.Cell c =
                    new com.itextpdf.layout.element.Cell()
                            .add(new Paragraph(h).setBold().setFontSize(9))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER);
            t.addHeaderCell(c);
        }
    }

    private void pdfCell(Table t, String val) {
        t.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(val != null ? val : "").setFontSize(8)));
    }

    // ======================================================
    // EXCEL
    // ======================================================
    @Override
    public void exportExcel(String type, ProjectReportFilterDTO filter,
                            CustomUserDetails user, HttpServletResponse response) throws Exception {
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"project-report-" + type + "-"
                        + LocalDate.now() + ".xlsx\"");

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            switch (type.toLowerCase()) {
                case "master"              -> writeMasterExcel(wb, filter, user);
                case "timeline"            -> writeTimelineExcel(wb, filter, user);
                case "resource-allocation" -> writeAllocationExcel(wb, filter);
                case "employee-wise"       -> writeEmployeeWiseExcel(wb, filter, user);
                case "effort"              -> writeEffortExcel(wb, filter, user);
                case "cost"                -> writeCostExcel(wb, filter);
                default -> throw new InvalidRequestException("Unknown report type: " + type);
            }

            wb.write(response.getOutputStream());
        }
    }

    // ── Excel writers ────────────────────────────────────────────────────────

    private void writeMasterExcel(XSSFWorkbook wb,
                                  ProjectReportFilterDTO filter, CustomUserDetails user) {
        List<ProjectMasterReportDTO> rows = reportService
                .getProjectMasterReport(filter, ALL, user).getData().getContent();

        Sheet s = wb.createSheet("Project Master");
        CellStyle hs = headerStyle(wb);
        String[] headers = {"Project Name", "Code", "Client", "Start", "End",
                "Status", "Total Assignees", "Employees", "Trainees", "Interns",
                "Delayed", "Overdue (days)"};
        createExcelHeader(s, hs, headers);
        int r = 1;
        for (ProjectMasterReportDTO d : rows) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(nullStr(d.getProjectName()));
            row.createCell(1).setCellValue(nullStr(d.getProjectCode()));
            row.createCell(2).setCellValue(nullStr(d.getClientName()));
            row.createCell(3).setCellValue(fmt(d.getStartDate()));
            row.createCell(4).setCellValue(fmt(d.getEndDate()));
            row.createCell(5).setCellValue(nullStr(d.getStatus()));
            row.createCell(6).setCellValue(d.getTotalAssignees());
            row.createCell(7).setCellValue(d.getEmployeeCount());
            row.createCell(8).setCellValue(d.getTraineeCount());
            row.createCell(9).setCellValue(d.getInternCount());
            row.createCell(10).setCellValue(d.isDelayed() ? "YES" : "NO");
            row.createCell(11).setCellValue(d.getDaysOverdue());
        }
        autoSize(s, headers.length);
    }

    private void writeTimelineExcel(XSSFWorkbook wb,
                                    ProjectReportFilterDTO filter, CustomUserDetails user) {
        List<ProjectTimelineReportDTO> rows = reportService
                .getProjectTimelineReport(filter, ALL, user).getData().getContent();

        Sheet s = wb.createSheet("Timeline");
        CellStyle hs = headerStyle(wb);
        String[] headers = {"Project Name", "Code", "Client", "Start", "End", "Status",
                "Duration (days)", "Elapsed (days)", "Progress %", "Delayed",
                "Overdue (days)", "Remaining (days)"};
        createExcelHeader(s, hs, headers);
        int r = 1;
        for (ProjectTimelineReportDTO d : rows) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(nullStr(d.getProjectName()));
            row.createCell(1).setCellValue(nullStr(d.getProjectCode()));
            row.createCell(2).setCellValue(nullStr(d.getClientName()));
            row.createCell(3).setCellValue(fmt(d.getStartDate()));
            row.createCell(4).setCellValue(fmt(d.getEndDate()));
            row.createCell(5).setCellValue(nullStr(d.getStatus()));
            row.createCell(6).setCellValue(d.getDurationDays());
            row.createCell(7).setCellValue(d.getElapsedDays());
            row.createCell(8).setCellValue(d.getProgressPercent());
            row.createCell(9).setCellValue(d.isDelayed() ? "YES" : "NO");
            row.createCell(10).setCellValue(d.getDaysOverdue());
            row.createCell(11).setCellValue(d.getDaysRemaining());
        }
        autoSize(s, headers.length);
    }

    private void writeAllocationExcel(XSSFWorkbook wb, ProjectReportFilterDTO filter) {
        List<ResourceAllocationReportDTO> projects = reportService
                .getResourceAllocationReport(filter, ALL).getData().getContent();

        Sheet s = wb.createSheet("Resource Allocation");
        CellStyle hs = headerStyle(wb);
        CellStyle projStyle = projectRowStyle(wb);
        String[] headers = {"Project Name", "Project Code", "Status",
                "Assignee Name", "Assignee Code", "Type", "Role", "Department", "Designation"};
        createExcelHeader(s, hs, headers);

        int r = 1;
        for (ResourceAllocationReportDTO proj : projects) {
            // Project summary row (merged)
            // FIX #2: explicitly typed as POI Cell to avoid iText Cell conflict
            Row projRow = s.createRow(r);
            org.apache.poi.ss.usermodel.Cell pc = projRow.createCell(0);
            pc.setCellValue(proj.getProjectName() + " [" + proj.getProjectCode() + "]"
                    + "  |  Status: " + proj.getProjectStatus()
                    + "  |  Total: " + proj.getTotalAssignees());
            pc.setCellStyle(projStyle);
            s.addMergedRegion(new CellRangeAddress(r, r, 0, headers.length - 1));
            r++;

            for (ProjectAssigneeDTO a : proj.getAssignees()) {
                Row aRow = s.createRow(r++);
                aRow.createCell(0).setCellValue(proj.getProjectName());
                aRow.createCell(1).setCellValue(proj.getProjectCode());
                aRow.createCell(2).setCellValue(nullStr(proj.getProjectStatus()));
                aRow.createCell(3).setCellValue(nullStr(a.getAssigneeName()));
                aRow.createCell(4).setCellValue(nullStr(a.getAssigneeCode()));
                aRow.createCell(5).setCellValue(nullStr(a.getAssigneeType()));
                aRow.createCell(6).setCellValue(nullStr(a.getRoleInProject()));
                aRow.createCell(7).setCellValue(nullStr(a.getDepartment()));
                aRow.createCell(8).setCellValue(nullStr(a.getDesignation()));
            }
        }
        autoSize(s, headers.length);
    }

    private void writeEmployeeWiseExcel(XSSFWorkbook wb,
                                        ProjectReportFilterDTO filter, CustomUserDetails user) {
        List<EmployeeProjectReportDTO> rows = reportService
                .getEmployeeWiseProjectReport(filter, ALL, user).getData().getContent();

        Sheet s = wb.createSheet("Employee-wise Projects");
        CellStyle hs = headerStyle(wb);
        String[] headers = {"Project Name", "Code", "Client", "Start", "End",
                "Status", "Role in Project", "Type", "Delayed", "Overdue (days)"};
        createExcelHeader(s, hs, headers);
        int r = 1;
        for (EmployeeProjectReportDTO d : rows) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(nullStr(d.getProjectName()));
            row.createCell(1).setCellValue(nullStr(d.getProjectCode()));
            row.createCell(2).setCellValue(nullStr(d.getClientName()));
            row.createCell(3).setCellValue(fmt(d.getStartDate()));
            row.createCell(4).setCellValue(fmt(d.getEndDate()));
            row.createCell(5).setCellValue(nullStr(d.getProjectStatus()));
            row.createCell(6).setCellValue(nullStr(d.getRoleInProject()));
            row.createCell(7).setCellValue(nullStr(d.getAssigneeType()));
            row.createCell(8).setCellValue(d.isDelayed() ? "YES" : "NO");
            row.createCell(9).setCellValue(d.getDaysOverdue());
        }
        autoSize(s, headers.length);
    }

    private void writeEffortExcel(XSSFWorkbook wb,
                                  ProjectReportFilterDTO filter, CustomUserDetails user) {
        List<ProjectEffortReportDTO> rows = reportService
                .getProjectEffortReport(filter, ALL, user).getData().getContent();

        Sheet s = wb.createSheet("Effort");
        CellStyle hs = headerStyle(wb);
        String[] headers = {"Project Name", "Code", "Status", "Start", "End",
                "Total Assignees", "Total Hours", "Note"};
        createExcelHeader(s, hs, headers);
        int r = 1;
        for (ProjectEffortReportDTO d : rows) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(nullStr(d.getProjectName()));
            row.createCell(1).setCellValue(nullStr(d.getProjectCode()));
            row.createCell(2).setCellValue(nullStr(d.getProjectStatus()));
            row.createCell(3).setCellValue(fmt(d.getStartDate()));
            row.createCell(4).setCellValue(fmt(d.getEndDate()));
            row.createCell(5).setCellValue(d.getTotalAssignees());
            row.createCell(6).setCellValue(d.getTotalHours() != null ? d.getTotalHours() : 0.0);
            row.createCell(7).setCellValue(nullStr(d.getTimesheetNote()));
        }
        autoSize(s, headers.length);
    }

    private void writeCostExcel(XSSFWorkbook wb, ProjectReportFilterDTO filter) {
        List<ProjectCostReportDTO> rows = reportService
                .getProjectCostReport(filter, ALL).getData().getContent();

        Sheet s = wb.createSheet("Cost");
        CellStyle hs = headerStyle(wb);
        String[] headers = {"Project Name", "Code", "Status", "Start", "End", "Client",
                "Budget (₹)", "Actual Cost (₹)", "Variance (₹)", "Variance %", "Note"};
        createExcelHeader(s, hs, headers);
        int r = 1;
        for (ProjectCostReportDTO d : rows) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(nullStr(d.getProjectName()));
            row.createCell(1).setCellValue(nullStr(d.getProjectCode()));
            row.createCell(2).setCellValue(nullStr(d.getStatus()));
            row.createCell(3).setCellValue(fmt(d.getStartDate()));
            row.createCell(4).setCellValue(fmt(d.getEndDate()));
            row.createCell(5).setCellValue(nullStr(d.getClientName()));
            row.createCell(6).setCellValue(d.getBudgetAmount() != null ? d.getBudgetAmount() : 0.0);
            row.createCell(7).setCellValue(d.getActualCost() != null ? d.getActualCost() : 0.0);
            row.createCell(8).setCellValue(d.getVariance() != null ? d.getVariance() : 0.0);
            row.createCell(9).setCellValue(d.getVariancePercent() != null
                    ? String.format("%.2f%%", d.getVariancePercent()) : "N/A");
            row.createCell(10).setCellValue(nullStr(d.getCostNote()));
        }
        autoSize(s, headers.length);
    }

    // ── Excel helpers ────────────────────────────────────────────────────────

    // FIX #2: Use fully qualified POI Cell to avoid ambiguity with iText Cell
    private void createExcelHeader(Sheet s, CellStyle style, String[] headers) {
        Row header = s.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(style);
        }
    }

    private CellStyle headerStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle projectRowStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void autoSize(Sheet s, int cols) {
        for (int i = 0; i < cols; i++) s.autoSizeColumn(i);
    }

    private String fmt(LocalDate d) {
        return d != null ? d.format(FMT) : "";
    }

    private String nullStr(Object o) {
        return o != null ? o.toString() : "";
    }
}