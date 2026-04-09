// ===== Implementation =====
package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.AttendanceReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.service.AttendanceExportService;
import com.gm.hrms.service.AttendanceReportService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceExportServiceImpl implements AttendanceExportService {

    private final AttendanceReportService reportService;

    // ===== MASTER FETCH (unpaged — all records for export) =====
    private static final Pageable ALL_PAGES = PageRequest.of(0, Integer.MAX_VALUE);

    // ================================================================
    // PDF EXPORT
    // ================================================================
    @Override
    public void exportPdf(String reportType, AttendanceReportFilterDTO filter,
                          HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"attendance_" + reportType + ".pdf\"");

        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 40, 30);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);

        doc.add(new Paragraph("Attendance Report — " + reportType.toUpperCase(), titleFont));
        doc.add(Chunk.NEWLINE);

        switch (reportType.toLowerCase()) {
            case "daily"          -> writeDailyPdf(doc, filter, headerFont, cellFont);
            case "late-coming"    -> writeLatePdf(doc, filter, headerFont, cellFont);
            case "overtime"       -> writeOvertimePdf(doc, filter, headerFont, cellFont);
            case "absent"         -> writeAbsentPdf(doc, filter, headerFont, cellFont);
            case "monthly"        -> writeMonthlyPdf(doc, filter, headerFont, cellFont);
            case "regularization" -> writeRegularizationPdf(doc, filter, headerFont, cellFont);
            default               -> doc.add(new Paragraph("Unknown report type: " + reportType));
        }

        doc.close();
    }

    // ================================================================
    // EXCEL EXPORT
    // ================================================================
    @Override
    public void exportExcel(String reportType, AttendanceReportFilterDTO filter,
                            HttpServletResponse response) throws Exception {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"attendance_" + reportType + ".xlsx\"");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(reportType);

            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font hf = wb.createFont();
            hf.setBold(true);
            hf.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hf);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            switch (reportType.toLowerCase()) {
                case "daily"          -> writeDailyExcel(sheet, filter, headerStyle);
                case "late-coming"    -> writeLateExcel(sheet, filter, headerStyle);
                case "overtime"       -> writeOvertimeExcel(sheet, filter, headerStyle);
                case "absent"         -> writeAbsentExcel(sheet, filter, headerStyle);
                case "monthly"        -> writeMonthlyExcel(sheet, filter, headerStyle);
                case "regularization" -> writeRegularizationExcel(sheet, filter, headerStyle);
            }

            // Auto-size columns
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            wb.write(response.getOutputStream());
        }
    }

    // ================================================================
    // PDF writers
    // ================================================================

    private void writeDailyPdf(Document doc, AttendanceReportFilterDTO filter,
                               Font hf, Font cf) throws DocumentException {
        List<DailyAttendanceReportDTO> records = reportService
                .getDailyReport(filter, ALL_PAGES).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Shift",
                "Date","Check-In","Check-Out","Status","Work Min","Late Min","OT Min"};
        PdfPTable table = createPdfTable(headers, hf);

        for (DailyAttendanceReportDTO r : records) {
            addCell(table, cf, r.getEmployeeCode());
            addCell(table, cf, r.getEmployeeName());
            addCell(table, cf, r.getDepartment());
            addCell(table, cf, r.getDesignation());
            addCell(table, cf, r.getShift());
            addCell(table, cf, str(r.getAttendanceDate()));
            addCell(table, cf, str(r.getCheckIn()));
            addCell(table, cf, str(r.getCheckOut()));
            addCell(table, cf, str(r.getStatus()));
            addCell(table, cf, str(r.getWorkMinutes()));
            addCell(table, cf, str(r.getLateMinutes()));
            addCell(table, cf, str(r.getOvertimeMinutes()));
        }
        doc.add(table);
    }

    private void writeLatePdf(Document doc, AttendanceReportFilterDTO filter,
                              Font hf, Font cf) throws DocumentException {
        List<LateComingReportDTO> records = reportService
                .getLateComingReport(filter, ALL_PAGES).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Date","Check-In","Shift Start","Late Min"};
        PdfPTable table = createPdfTable(headers, hf);

        for (LateComingReportDTO r : records) {
            addCell(table, cf, r.getEmployeeCode());
            addCell(table, cf, r.getEmployeeName());
            addCell(table, cf, r.getDepartment());
            addCell(table, cf, r.getDesignation());
            addCell(table, cf, str(r.getAttendanceDate()));
            addCell(table, cf, str(r.getCheckIn()));
            addCell(table, cf, r.getShiftStartTime());
            addCell(table, cf, str(r.getLateMinutes()));
        }
        doc.add(table);
    }

    private void writeOvertimePdf(Document doc, AttendanceReportFilterDTO filter,
                                  Font hf, Font cf) throws DocumentException {
        List<OvertimeReportDTO> records = reportService
                .getOvertimeReport(filter, ALL_PAGES).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Date","Work Min","OT Min"};
        PdfPTable table = createPdfTable(headers, hf);

        for (OvertimeReportDTO r : records) {
            addCell(table, cf, r.getEmployeeCode());
            addCell(table, cf, r.getEmployeeName());
            addCell(table, cf, r.getDepartment());
            addCell(table, cf, r.getDesignation());
            addCell(table, cf, str(r.getAttendanceDate()));
            addCell(table, cf, str(r.getWorkMinutes()));
            addCell(table, cf, str(r.getOvertimeMinutes()));
        }
        doc.add(table);
    }

    private void writeAbsentPdf(Document doc, AttendanceReportFilterDTO filter,
                                Font hf, Font cf) throws DocumentException {
        List<AbsentReportDTO> records = reportService
                .getAbsentReport(filter, ALL_PAGES).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Date"};
        PdfPTable table = createPdfTable(headers, hf);

        for (AbsentReportDTO r : records) {
            addCell(table, cf, r.getEmployeeCode());
            addCell(table, cf, r.getEmployeeName());
            addCell(table, cf, r.getDepartment());
            addCell(table, cf, r.getDesignation());
            addCell(table, cf, str(r.getAbsentDate()));
        }
        doc.add(table);
    }

    private void writeMonthlyPdf(Document doc, AttendanceReportFilterDTO filter,
                                 Font hf, Font cf) throws DocumentException {
        List<MonthlyAttendanceSummaryDTO> records = reportService
                .getMonthlySummary(filter, ALL_PAGES).getData().getContent();

        String[] headers = {"Code","Name","Dept","Desig","Month","Year",
                "Present","Absent","HalfDay","Leave","Holiday","Late","OT","WorkMin"};
        PdfPTable table = createPdfTable(headers, hf);

        for (MonthlyAttendanceSummaryDTO r : records) {
            addCell(table, cf, r.getEmployeeCode());
            addCell(table, cf, r.getEmployeeName());
            addCell(table, cf, r.getDepartment());
            addCell(table, cf, r.getDesignation());
            addCell(table, cf, str(r.getMonth()));
            addCell(table, cf, str(r.getYear()));
            addCell(table, cf, str(r.getTotalPresent()));
            addCell(table, cf, str(r.getTotalAbsent()));
            addCell(table, cf, str(r.getTotalHalfDay()));
            addCell(table, cf, str(r.getTotalLeave()));
            addCell(table, cf, str(r.getTotalHoliday()));
            addCell(table, cf, str(r.getTotalLate()));
            addCell(table, cf, str(r.getTotalOvertime()));
            addCell(table, cf, str(r.getTotalWorkMinutes()));
        }
        doc.add(table);
    }

    private void writeRegularizationPdf(Document doc, AttendanceReportFilterDTO filter,
                                        Font hf, Font cf) throws DocumentException {
        List<RegularizationReportDTO> records = reportService
                .getRegularizationReport(filter, ALL_PAGES).getData().getContent();

        String[] headers = {"Code","Name","Date","Orig In","Orig Out","Req In","Req Out","Reason","Status","Remarks"};
        PdfPTable table = createPdfTable(headers, hf);

        for (RegularizationReportDTO r : records) {
            addCell(table, cf, r.getEmployeeCode());
            addCell(table, cf, r.getEmployeeName());
            addCell(table, cf, str(r.getAttendanceDate()));
            addCell(table, cf, str(r.getOriginalCheckIn()));
            addCell(table, cf, str(r.getOriginalCheckOut()));
            addCell(table, cf, str(r.getRequestedCheckIn()));
            addCell(table, cf, str(r.getRequestedCheckOut()));
            addCell(table, cf, r.getReason());
            addCell(table, cf, str(r.getStatus()));
            addCell(table, cf, r.getRemarks());
        }
        doc.add(table);
    }

    // ================================================================
    // EXCEL writers
    // ================================================================

    private void writeDailyExcel(Sheet sheet, AttendanceReportFilterDTO filter, CellStyle hs) {
        List<DailyAttendanceReportDTO> records = reportService
                .getDailyReport(filter, ALL_PAGES).getData().getContent();
        createExcelHeader(sheet, hs, "Code","Name","Department","Designation","Shift",
                "Date","Check-In","Check-Out","Status","Work Min","Late Min","OT Min");
        int r = 1;
        for (DailyAttendanceReportDTO d : records) {
            Row row = sheet.createRow(r++);
            writeRow(row, d.getEmployeeCode(), d.getEmployeeName(), d.getDepartment(),
                    d.getDesignation(), d.getShift(), str(d.getAttendanceDate()),
                    str(d.getCheckIn()), str(d.getCheckOut()), str(d.getStatus()),
                    str(d.getWorkMinutes()), str(d.getLateMinutes()), str(d.getOvertimeMinutes()));
        }
    }

    private void writeLateExcel(Sheet sheet, AttendanceReportFilterDTO filter, CellStyle hs) {
        List<LateComingReportDTO> records = reportService
                .getLateComingReport(filter, ALL_PAGES).getData().getContent();
        createExcelHeader(sheet, hs, "Code","Name","Department","Designation",
                "Date","Check-In","Shift Start","Late Min");
        int r = 1;
        for (LateComingReportDTO d : records) {
            Row row = sheet.createRow(r++);
            writeRow(row, d.getEmployeeCode(), d.getEmployeeName(), d.getDepartment(),
                    d.getDesignation(), str(d.getAttendanceDate()), str(d.getCheckIn()),
                    d.getShiftStartTime(), str(d.getLateMinutes()));
        }
    }

    private void writeOvertimeExcel(Sheet sheet, AttendanceReportFilterDTO filter, CellStyle hs) {
        List<OvertimeReportDTO> records = reportService
                .getOvertimeReport(filter, ALL_PAGES).getData().getContent();
        createExcelHeader(sheet, hs, "Code","Name","Department","Designation","Date","Work Min","OT Min");
        int r = 1;
        for (OvertimeReportDTO d : records) {
            Row row = sheet.createRow(r++);
            writeRow(row, d.getEmployeeCode(), d.getEmployeeName(), d.getDepartment(),
                    d.getDesignation(), str(d.getAttendanceDate()),
                    str(d.getWorkMinutes()), str(d.getOvertimeMinutes()));
        }
    }

    private void writeAbsentExcel(Sheet sheet, AttendanceReportFilterDTO filter, CellStyle hs) {
        List<AbsentReportDTO> records = reportService
                .getAbsentReport(filter, ALL_PAGES).getData().getContent();
        createExcelHeader(sheet, hs, "Code","Name","Department","Designation","Date");
        int r = 1;
        for (AbsentReportDTO d : records) {
            Row row = sheet.createRow(r++);
            writeRow(row, d.getEmployeeCode(), d.getEmployeeName(),
                    d.getDepartment(), d.getDesignation(), str(d.getAbsentDate()));
        }
    }

    private void writeMonthlyExcel(Sheet sheet, AttendanceReportFilterDTO filter, CellStyle hs) {
        List<MonthlyAttendanceSummaryDTO> records = reportService
                .getMonthlySummary(filter, ALL_PAGES).getData().getContent();
        createExcelHeader(sheet, hs, "Code","Name","Dept","Desig","Month","Year",
                "Present","Absent","HalfDay","Leave","Holiday","Late","OT","WorkMin");
        int r = 1;
        for (MonthlyAttendanceSummaryDTO d : records) {
            Row row = sheet.createRow(r++);
            writeRow(row, d.getEmployeeCode(), d.getEmployeeName(),
                    d.getDepartment(), d.getDesignation(),
                    str(d.getMonth()), str(d.getYear()),
                    str(d.getTotalPresent()), str(d.getTotalAbsent()),
                    str(d.getTotalHalfDay()), str(d.getTotalLeave()),
                    str(d.getTotalHoliday()), str(d.getTotalLate()),
                    str(d.getTotalOvertime()), str(d.getTotalWorkMinutes()));
        }
    }

    private void writeRegularizationExcel(Sheet sheet, AttendanceReportFilterDTO filter, CellStyle hs) {
        List<RegularizationReportDTO> records = reportService
                .getRegularizationReport(filter, ALL_PAGES).getData().getContent();
        createExcelHeader(sheet, hs, "Code","Name","Date","Orig In","Orig Out",
                "Req In","Req Out","Reason","Status","Remarks");
        int r = 1;
        for (RegularizationReportDTO d : records) {
            Row row = sheet.createRow(r++);
            writeRow(row, d.getEmployeeCode(), d.getEmployeeName(), str(d.getAttendanceDate()),
                    str(d.getOriginalCheckIn()), str(d.getOriginalCheckOut()),
                    str(d.getRequestedCheckIn()), str(d.getRequestedCheckOut()),
                    d.getReason(), str(d.getStatus()), d.getRemarks());
        }
    }

    // ================================================================
    // SHARED HELPERS
    // ================================================================

    private PdfPTable createPdfTable(String[] headers, Font hf) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, hf));
            cell.setBackgroundColor(new Color(41, 58, 97));
            cell.setPadding(5);
            table.addCell(cell);
        }
        return table;
    }

    private void addCell(PdfPTable table, Font font, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", font));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void createExcelHeader(Sheet sheet, CellStyle style, String... headers) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeRow(Row row, String... values) {
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i] != null ? values[i] : "");
        }
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}