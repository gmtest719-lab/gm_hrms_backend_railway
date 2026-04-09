package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.service.LeaveReportExportService;
import com.gm.hrms.service.LeaveReportService;
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
public class LeaveReportExportServiceImpl implements LeaveReportExportService {

    private final LeaveReportService reportService;

    private static final Pageable ALL = PageRequest.of(0, Integer.MAX_VALUE);

    // ── PDF
    @Override
    public void exportPdf(String reportType,
                          LeaveReportFilterDTO filter,
                          HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"leave_" + reportType + ".pdf\"");

        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 40, 30);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  9, Color.WHITE);
        Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA,       8, Color.BLACK);

        doc.add(new Paragraph("Leave Report — " + reportType.toUpperCase(), titleFont));
        doc.add(Chunk.NEWLINE);

        switch (reportType.toLowerCase()) {
            case "balance"    -> writeBalancePdf   (doc, filter, headerFont, cellFont);
            case "history"    -> writeHistoryPdf   (doc, filter, headerFont, cellFont);
            case "requests"   -> writeRequestsPdf  (doc, filter, headerFont, cellFont);
            case "type-usage" -> writeTypeUsagePdf (doc, filter, headerFont, cellFont);
            case "trends"     -> writeTrendsPdf    (doc, filter, headerFont, cellFont);
            case "encashment" -> writeEncashmentPdf(doc, filter, headerFont, cellFont);
            case "approvals"  -> writeApprovalsPdf (doc, filter, headerFont, cellFont);
            default           -> doc.add(new Paragraph("Unknown report type: " + reportType));
        }

        doc.close();
    }

    // ── Excel ─────────────────────────────────────────────────────────
    @Override
    public void exportExcel(String reportType,
                            LeaveReportFilterDTO filter,
                            HttpServletResponse response) throws Exception {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"leave_" + reportType + ".xlsx\"");

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet(reportType);

            CellStyle hs = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font hf = wb.createFont();
            hf.setBold(true);
            hf.setColor(IndexedColors.WHITE.getIndex());
            hs.setFont(hf);
            hs.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            hs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hs.setBorderBottom(BorderStyle.THIN);

            switch (reportType.toLowerCase()) {
                case "balance"    -> writeBalanceExcel   (sheet, filter, hs);
                case "history"    -> writeHistoryExcel   (sheet, filter, hs);
                case "requests"   -> writeRequestsExcel  (sheet, filter, hs);
                case "type-usage" -> writeTypeUsageExcel (sheet, filter, hs);
                case "trends"     -> writeTrendsExcel    (sheet, filter, hs);
                case "encashment" -> writeEncashmentExcel(sheet, filter, hs);
                case "approvals"  -> writeApprovalsExcel (sheet, filter, hs);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (int i = 0; i < headerRow.getLastCellNum(); i++) sheet.autoSizeColumn(i);
            }

            wb.write(response.getOutputStream());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // PDF writers
    // ═══════════════════════════════════════════════════════════════

    private void writeBalancePdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveBalanceReportDTO> records = reportService.getBalanceReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Code","Name","Dept","Designation","Leave Type","Code","Total","Used","Remaining","Year");
        for (LeaveBalanceReportDTO r : records) {
            addCells(t, cf,
                    r.getEmployeeCode(), r.getEmployeeName(), safe(r.getDepartment()),
                    safe(r.getDesignation()), safe(r.getLeaveType()), safe(r.getLeaveCode()),
                    str(r.getTotalLeaves()), str(r.getUsedLeaves()), str(r.getRemainingLeaves()),
                    str(r.getYear()));
        }
        doc.add(t);
    }

    private void writeHistoryPdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveHistoryReportDTO> records = reportService.getHistoryReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Code","Name","Dept","Leave Type","From","To","Days","Status","Applied On");
        for (LeaveHistoryReportDTO r : records) {
            addCells(t, cf,
                    r.getEmployeeCode(), r.getEmployeeName(), safe(r.getDepartment()),
                    safe(r.getLeaveType()), str(r.getStartDate()), str(r.getEndDate()),
                    str(r.getTotalDays()), safe(r.getStatus()), str(r.getAppliedOn()));
        }
        doc.add(t);
    }

    private void writeRequestsPdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveRequestReportDTO> records = reportService.getRequestReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Code","Name","Leave Type","From","To","Days","Status","Approval Level","Applied On");
        for (LeaveRequestReportDTO r : records) {
            addCells(t, cf,
                    r.getEmployeeCode(), r.getEmployeeName(), safe(r.getLeaveType()),
                    str(r.getStartDate()), str(r.getEndDate()), str(r.getTotalDays()),
                    safe(r.getStatus()), str(r.getApprovalLevel()), str(r.getAppliedOn()));
        }
        doc.add(t);
    }

    private void writeTypeUsagePdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveTypeUsageReportDTO> records = reportService.getTypeUsageReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Leave Type","Code","Paid","Requests","Days Taken","Approved","Pending","Rejected","Cancelled");
        for (LeaveTypeUsageReportDTO r : records) {
            addCells(t, cf,
                    r.getLeaveTypeName(), r.getLeaveCode(), r.isPaid() ? "Yes" : "No",
                    str(r.getTotalRequests()), str(r.getTotalDaysTaken()),
                    str(r.getApprovedCount()), str(r.getPendingCount()),
                    str(r.getRejectedCount()), str(r.getCancelledCount()));
        }
        doc.add(t);
    }

    private void writeTrendsPdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveTrendsReportDTO> records = reportService.getTrendsReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Year","Month","Requests","Days Taken","Approved","Pending","Rejected");
        for (LeaveTrendsReportDTO r : records) {
            addCells(t, cf,
                    str(r.getYear()), r.getMonthName(), str(r.getTotalRequests()),
                    str(r.getTotalDaysTaken()), str(r.getApprovedCount()),
                    str(r.getPendingCount()), str(r.getRejectedCount()));
        }
        doc.add(t);
    }

    private void writeEncashmentPdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveEncashmentReportDTO> records = reportService.getEncashmentReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Code","Name","Dept","Leave Type","Days Encashed","Before Bal","After Bal","Date","Remarks");
        for (LeaveEncashmentReportDTO r : records) {
            addCells(t, cf,
                    r.getEmployeeCode(), r.getEmployeeName(), safe(r.getDepartment()),
                    safe(r.getLeaveType()), str(r.getDaysEncashed()),
                    str(r.getBeforeBalance()), str(r.getAfterBalance()),
                    str(r.getEncashedOn()), safe(r.getRemarks()));
        }
        doc.add(t);
    }

    private void writeApprovalsPdf(Document doc, LeaveReportFilterDTO f, Font hf, Font cf) throws DocumentException {
        List<LeaveApprovalReportDTO> records = reportService.getApprovalReport(f, ALL).getData().getContent();
        PdfPTable t = pdfTable(hf, "Code","Name","Leave Type","From","To","Days","Status",
                "Approver","Approved At","Rejection Reason");
        for (LeaveApprovalReportDTO r : records) {
            addCells(t, cf,
                    r.getEmployeeCode(), r.getEmployeeName(), safe(r.getLeaveType()),
                    str(r.getStartDate()), str(r.getEndDate()), str(r.getTotalDays()),
                    safe(r.getStatus()), safe(r.getApproverName()),
                    str(r.getApprovedAt()), safe(r.getRejectionReason()));
        }
        doc.add(t);
    }

    // ═══════════════════════════════════════════════════════════════
    // Excel writers
    // ═══════════════════════════════════════════════════════════════

    private void writeBalanceExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveBalanceReportDTO> records = reportService.getBalanceReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Code","Name","Dept","Designation","Leave Type","Code","Total","Used","Remaining","Year");
        int i = 1;
        for (LeaveBalanceReportDTO r : records) excelRow(s, i++,
                r.getEmployeeCode(), r.getEmployeeName(), safe(r.getDepartment()),
                safe(r.getDesignation()), safe(r.getLeaveType()), safe(r.getLeaveCode()),
                str(r.getTotalLeaves()), str(r.getUsedLeaves()), str(r.getRemainingLeaves()), str(r.getYear()));
    }

    private void writeHistoryExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveHistoryReportDTO> records = reportService.getHistoryReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Code","Name","Dept","Leave Type","From","To","Days","Status","Applied On");
        int i = 1;
        for (LeaveHistoryReportDTO r : records) excelRow(s, i++,
                r.getEmployeeCode(), r.getEmployeeName(), safe(r.getDepartment()),
                safe(r.getLeaveType()), str(r.getStartDate()), str(r.getEndDate()),
                str(r.getTotalDays()), safe(r.getStatus()), str(r.getAppliedOn()));
    }

    private void writeRequestsExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveRequestReportDTO> records = reportService.getRequestReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Code","Name","Leave Type","From","To","Days","Status","Approval Level","Applied On");
        int i = 1;
        for (LeaveRequestReportDTO r : records) excelRow(s, i++,
                r.getEmployeeCode(), r.getEmployeeName(), safe(r.getLeaveType()),
                str(r.getStartDate()), str(r.getEndDate()), str(r.getTotalDays()),
                safe(r.getStatus()), str(r.getApprovalLevel()), str(r.getAppliedOn()));
    }

    private void writeTypeUsageExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveTypeUsageReportDTO> records = reportService.getTypeUsageReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Leave Type","Code","Paid","Requests","Days Taken","Approved","Pending","Rejected","Cancelled");
        int i = 1;
        for (LeaveTypeUsageReportDTO r : records) excelRow(s, i++,
                r.getLeaveTypeName(), r.getLeaveCode(), r.isPaid() ? "Yes" : "No",
                str(r.getTotalRequests()), str(r.getTotalDaysTaken()),
                str(r.getApprovedCount()), str(r.getPendingCount()),
                str(r.getRejectedCount()), str(r.getCancelledCount()));
    }

    private void writeTrendsExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveTrendsReportDTO> records = reportService.getTrendsReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Year","Month","Requests","Days Taken","Approved","Pending","Rejected");
        int i = 1;
        for (LeaveTrendsReportDTO r : records) excelRow(s, i++,
                str(r.getYear()), r.getMonthName(), str(r.getTotalRequests()),
                str(r.getTotalDaysTaken()), str(r.getApprovedCount()),
                str(r.getPendingCount()), str(r.getRejectedCount()));
    }

    private void writeEncashmentExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveEncashmentReportDTO> records = reportService.getEncashmentReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Code","Name","Dept","Leave Type","Days Encashed","Before Bal","After Bal","Date","Remarks");
        int i = 1;
        for (LeaveEncashmentReportDTO r : records) excelRow(s, i++,
                r.getEmployeeCode(), r.getEmployeeName(), safe(r.getDepartment()),
                safe(r.getLeaveType()), str(r.getDaysEncashed()),
                str(r.getBeforeBalance()), str(r.getAfterBalance()),
                str(r.getEncashedOn()), safe(r.getRemarks()));
    }

    private void writeApprovalsExcel(Sheet s, LeaveReportFilterDTO f, CellStyle hs) {
        List<LeaveApprovalReportDTO> records = reportService.getApprovalReport(f, ALL).getData().getContent();
        excelHeader(s, hs, "Code","Name","Leave Type","From","To","Days","Status",
                "Approver","Approved At","Rejection Reason");
        int i = 1;
        for (LeaveApprovalReportDTO r : records) excelRow(s, i++,
                r.getEmployeeCode(), r.getEmployeeName(), safe(r.getLeaveType()),
                str(r.getStartDate()), str(r.getEndDate()), str(r.getTotalDays()),
                safe(r.getStatus()), safe(r.getApproverName()),
                str(r.getApprovedAt()), safe(r.getRejectionReason()));
    }

    // ── shared PDF helpers ─────────────────────────────────────────
    private PdfPTable pdfTable(Font hf, String... headers) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, hf));
            cell.setBackgroundColor(new Color(30, 58, 138));
            cell.setPadding(5);
            table.addCell(cell);
        }
        return table;
    }

    private void addCells(PdfPTable t, Font cf, String... values) {
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "", cf));
            cell.setPadding(4);
            t.addCell(cell);
        }
    }

    // ── shared Excel helpers ───────────────────────────────────────
    private void excelHeader(Sheet s, CellStyle style, String... headers) {
        Row row = s.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(style);
        }
    }

    private void excelRow(Sheet s, int rowNum, String... values) {
        Row row = s.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i] != null ? values[i] : "");
        }
    }

    private String safe(String v) { return v != null ? v : ""; }
    private String str(Object o)  { return o == null  ? "" : o.toString(); }
}
