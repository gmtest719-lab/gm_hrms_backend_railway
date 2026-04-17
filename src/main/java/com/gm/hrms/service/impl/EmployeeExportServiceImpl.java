package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.service.EmployeeExportService;
import com.gm.hrms.service.EmployeeReportService;
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
public class EmployeeExportServiceImpl implements EmployeeExportService {

    private final EmployeeReportService reportService;

    private static final Pageable ALL = PageRequest.of(0, Integer.MAX_VALUE);

    // ══════════════════════════════════════════════════════════
    // PDF
    // ══════════════════════════════════════════════════════════
    @Override
    public void exportPdf(String reportType,
                          EmployeeReportFilterDTO filter,
                          RoleType viewerRole,
                          Long viewerPersonalId,
                          HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"employee_" + reportType + ".pdf\"");

        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 40, 30);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,  Color.WHITE);
        Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA,      8,  Color.BLACK);

        doc.add(new Paragraph("Employee Report — " + reportType.toUpperCase(), titleFont));
        doc.add(Chunk.NEWLINE);

        switch (reportType.toLowerCase()) {
            case "master"       -> writeMasterPdf(doc, filter, viewerRole, viewerPersonalId, headerFont, cellFont);
            case "directory"    -> writeDirectoryPdf(doc, filter, viewerRole, headerFont, cellFont);
            case "joining"      -> writeJoiningPdf(doc, filter, headerFont, cellFont);
            case "exit"         -> writeExitPdf(doc, filter, headerFont, cellFont);
            case "status"       -> writeStatusPdf(doc, filter, headerFont, cellFont);
            case "diversity"    -> writeDiversityPdf(doc, filter, headerFont, cellFont);
            default             -> doc.add(new Paragraph("Unknown report type: " + reportType));
        }

        doc.close();
    }

    // ══════════════════════════════════════════════════════════
    // EXCEL
    // ══════════════════════════════════════════════════════════
    @Override
    public void exportExcel(String reportType,
                            EmployeeReportFilterDTO filter,
                            RoleType viewerRole,
                            Long viewerPersonalId,
                            HttpServletResponse response) throws Exception {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"employee_" + reportType + ".xlsx\"");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(reportType);

            CellStyle hs = buildHeaderStyle(wb);

            switch (reportType.toLowerCase()) {
                case "master"    -> writeMasterExcel(sheet, filter, viewerRole, viewerPersonalId, hs);
                case "directory" -> writeDirectoryExcel(sheet, filter, viewerRole, hs);
                case "joining"   -> writeJoiningExcel(sheet, filter, hs);
                case "exit"      -> writeExitExcel(sheet, filter, hs);
                case "status"    -> writeStatusExcel(sheet, filter, hs);
                case "diversity" -> writeDiversityExcel(sheet, filter, hs);
            }

            autoSize(sheet);
            wb.write(response.getOutputStream());
        }
    }

    // ══════════════════════════════════════════════════════════
    // PDF writers
    // ══════════════════════════════════════════════════════════

    private void writeMasterPdf(Document doc, EmployeeReportFilterDTO filter,
                                RoleType role, Long selfId,
                                Font hf, Font cf) throws DocumentException {

        List<EmployeeMasterReportDTO> records = reportService
                .getMasterReport(filter, ALL, role, selfId).getData().getContent();

        boolean isAdminHr = RoleType.ADMIN == role || RoleType.HR == role;

        String[] headers = isAdminHr
                ? new String[]{"Code","Name","Gender","DOB","Dept","Designation","Branch",
                "Joining","Email","Phone","CTC","Status"}
                : new String[]{"Code","Name","Dept","Designation","Email","Status"};

        PdfPTable table = createTable(headers, hf);

        for (EmployeeMasterReportDTO r : records) {
            if (isAdminHr) {
                addCells(table, cf,
                        r.getEmployeeCode(), r.getFullName(),
                        str(r.getGender()), str(r.getDateOfBirth()),
                        r.getDepartment(), r.getDesignation(), r.getBranch(),
                        str(r.getDateOfJoining()), r.getOfficeEmail(),
                        r.getPersonalPhone(), str(r.getCtc()),
                        str(r.getActive()));
            } else {
                addCells(table, cf,
                        r.getEmployeeCode(), r.getFullName(),
                        r.getDepartment(), r.getDesignation(),
                        r.getOfficeEmail(), str(r.getActive()));
            }
        }
        doc.add(table);
    }

    private void writeDirectoryPdf(Document doc, EmployeeReportFilterDTO filter,
                                   RoleType role, Font hf, Font cf) throws DocumentException {

        List<EmployeeDirectoryDTO> records = reportService
                .getDirectoryReport(filter, ALL, role).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Branch",
                "Email","Phone","Status"};
        PdfPTable table = createTable(headers, hf);

        for (EmployeeDirectoryDTO r : records) {
            addCells(table, cf,
                    r.getEmployeeCode(), r.getFullName(),
                    r.getDepartment(), r.getDesignation(), r.getBranch(),
                    r.getOfficeEmail(), r.getPersonalPhone(), str(r.getActive()));
        }
        doc.add(table);
    }

    private void writeJoiningPdf(Document doc, EmployeeReportFilterDTO filter,
                                 Font hf, Font cf) throws DocumentException {

        List<EmployeeJoiningReportDTO> records = reportService
                .getJoiningReport(filter, ALL).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Branch",
                "Joining Date","Employment Type","Role","Active"};
        PdfPTable table = createTable(headers, hf);

        for (EmployeeJoiningReportDTO r : records) {
            addCells(table, cf,
                    r.getEmployeeCode(), r.getFullName(),
                    r.getDepartment(), r.getDesignation(), r.getBranch(),
                    str(r.getDateOfJoining()), str(r.getEmploymentType()),
                    r.getRole(), str(r.getActive()));
        }
        doc.add(table);
    }

    private void writeExitPdf(Document doc, EmployeeReportFilterDTO filter,
                              Font hf, Font cf) throws DocumentException {

        List<EmployeeExitReportDTO> records = reportService
                .getExitReport(filter, ALL).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation","Joining Date",
                "Exit Date","Exit Reason","Remarks"};
        PdfPTable table = createTable(headers, hf);

        for (EmployeeExitReportDTO r : records) {
            addCells(table, cf,
                    r.getEmployeeCode(), r.getFullName(),
                    r.getDepartment(), r.getDesignation(),
                    str(r.getDateOfJoining()), str(r.getExitDate()),
                    r.getExitReason(), r.getExitRemarks());
        }
        doc.add(table);
    }

    private void writeStatusPdf(Document doc, EmployeeReportFilterDTO filter,
                                Font hf, Font cf) throws DocumentException {

        List<EmployeeStatusReportDTO> records = reportService
                .getStatusReport(filter, ALL).getData().getContent();

        String[] headers = {"Code","Name","Department","Designation",
                "Employment Type","Active","Record Status","Joining Date"};
        PdfPTable table = createTable(headers, hf);

        for (EmployeeStatusReportDTO r : records) {
            addCells(table, cf,
                    r.getEmployeeCode(), r.getFullName(),
                    r.getDepartment(), r.getDesignation(),
                    str(r.getEmploymentType()), str(r.getActive()),
                    r.getRecordStatus(), str(r.getDateOfJoining()));
        }
        doc.add(table);
    }

    private void writeDiversityPdf(Document doc, EmployeeReportFilterDTO filter,
                                   Font hf, Font cf) throws DocumentException {

        DiversityReportDTO d = reportService.getDiversityReport(filter);

        String[] headers = {"Metric", "Value"};
        PdfPTable table = createTable(headers, hf);

        addCells(table, cf, "Total Employees",    str(d.getTotalEmployees()));
        addCells(table, cf, "Male",               d.getMaleCount()   + " (" + d.getMalePercent()   + "%)");
        addCells(table, cf, "Female",             d.getFemaleCount() + " (" + d.getFemalePercent() + "%)");
        addCells(table, cf, "Other",              d.getOtherGenderCount() + " (" + d.getOtherPercent() + "%)");
        addCells(table, cf, "Employees",          str(d.getEmployeeCount()));
        addCells(table, cf, "Trainees",           str(d.getTraineeCount()));
        addCells(table, cf, "Interns",            str(d.getInternCount()));

        doc.add(table);
        doc.add(Chunk.NEWLINE);

        // Department breakdown
        if (d.getByDepartment() != null && !d.getByDepartment().isEmpty()) {
            String[] deptHeaders = {"Department", "Count"};
            PdfPTable deptTable = createTable(deptHeaders, hf);
            d.getByDepartment().forEach((dept, count) ->
                    addCells(deptTable, cf, dept, str(count)));
            doc.add(deptTable);
        }
    }

    // ══════════════════════════════════════════════════════════
    // Excel writers
    // ══════════════════════════════════════════════════════════

    private void writeMasterExcel(Sheet sheet, EmployeeReportFilterDTO filter,
                                  RoleType role, Long selfId, CellStyle hs) {

        List<EmployeeMasterReportDTO> records = reportService
                .getMasterReport(filter, ALL, role, selfId).getData().getContent();

        boolean isAdminHr = RoleType.ADMIN == role || RoleType.HR == role;

        if (isAdminHr) {
            createHeader(sheet, hs, "Code","Name","Gender","DOB","Dept","Designation","Branch",
                    "Joining","Email","Phone","CTC","Status");
            int r = 1;
            for (EmployeeMasterReportDTO d : records) {
                writeRow(sheet.createRow(r++),
                        d.getEmployeeCode(), d.getFullName(),
                        str(d.getGender()), str(d.getDateOfBirth()),
                        d.getDepartment(), d.getDesignation(), d.getBranch(),
                        str(d.getDateOfJoining()), d.getOfficeEmail(),
                        d.getPersonalPhone(), str(d.getCtc()), str(d.getActive()));
            }
        } else {
            createHeader(sheet, hs, "Code","Name","Dept","Designation","Email","Status");
            int r = 1;
            for (EmployeeMasterReportDTO d : records) {
                writeRow(sheet.createRow(r++),
                        d.getEmployeeCode(), d.getFullName(),
                        d.getDepartment(), d.getDesignation(),
                        d.getOfficeEmail(), str(d.getActive()));
            }
        }
    }

    private void writeDirectoryExcel(Sheet sheet, EmployeeReportFilterDTO filter,
                                     RoleType role, CellStyle hs) {

        List<EmployeeDirectoryDTO> records = reportService
                .getDirectoryReport(filter, ALL, role).getData().getContent();

        createHeader(sheet, hs, "Code","Name","Department","Designation",
                "Branch","Email","Phone","Status");
        int r = 1;
        for (EmployeeDirectoryDTO d : records) {
            writeRow(sheet.createRow(r++),
                    d.getEmployeeCode(), d.getFullName(),
                    d.getDepartment(), d.getDesignation(), d.getBranch(),
                    d.getOfficeEmail(), d.getPersonalPhone(), str(d.getActive()));
        }
    }

    private void writeJoiningExcel(Sheet sheet, EmployeeReportFilterDTO filter, CellStyle hs) {

        List<EmployeeJoiningReportDTO> records = reportService
                .getJoiningReport(filter, ALL).getData().getContent();

        createHeader(sheet, hs, "Code","Name","Department","Designation","Branch",
                "Joining Date","Employment Type","Role","Active");
        int r = 1;
        for (EmployeeJoiningReportDTO d : records) {
            writeRow(sheet.createRow(r++),
                    d.getEmployeeCode(), d.getFullName(),
                    d.getDepartment(), d.getDesignation(), d.getBranch(),
                    str(d.getDateOfJoining()), str(d.getEmploymentType()),
                    d.getRole(), str(d.getActive()));
        }
    }

    private void writeExitExcel(Sheet sheet, EmployeeReportFilterDTO filter, CellStyle hs) {

        List<EmployeeExitReportDTO> records = reportService
                .getExitReport(filter, ALL).getData().getContent();

        createHeader(sheet, hs, "Code","Name","Department","Designation",
                "Joining Date","Exit Date","Exit Reason","Remarks");
        int r = 1;
        for (EmployeeExitReportDTO d : records) {
            writeRow(sheet.createRow(r++),
                    d.getEmployeeCode(), d.getFullName(),
                    d.getDepartment(), d.getDesignation(),
                    str(d.getDateOfJoining()), str(d.getExitDate()),
                    d.getExitReason(), d.getExitRemarks());
        }
    }

    private void writeStatusExcel(Sheet sheet, EmployeeReportFilterDTO filter, CellStyle hs) {

        List<EmployeeStatusReportDTO> records = reportService
                .getStatusReport(filter, ALL).getData().getContent();

        createHeader(sheet, hs, "Code","Name","Department","Designation",
                "Employment Type","Active","Record Status","Joining Date");
        int r = 1;
        for (EmployeeStatusReportDTO d : records) {
            writeRow(sheet.createRow(r++),
                    d.getEmployeeCode(), d.getFullName(),
                    d.getDepartment(), d.getDesignation(),
                    str(d.getEmploymentType()), str(d.getActive()),
                    d.getRecordStatus(), str(d.getDateOfJoining()));
        }
    }

    private void writeDiversityExcel(Sheet sheet, EmployeeReportFilterDTO filter, CellStyle hs) {

        DiversityReportDTO d = reportService.getDiversityReport(filter);

        createHeader(sheet, hs, "Metric", "Value");
        int r = 1;
        writeRow(sheet.createRow(r++), "Total Employees",    str(d.getTotalEmployees()));
        writeRow(sheet.createRow(r++), "Male",               d.getMaleCount()   + " (" + d.getMalePercent()   + "%)");
        writeRow(sheet.createRow(r++), "Female",             d.getFemaleCount() + " (" + d.getFemalePercent() + "%)");
        writeRow(sheet.createRow(r++), "Other",              d.getOtherGenderCount() + " (" + d.getOtherPercent() + "%)");
        writeRow(sheet.createRow(r++), "Employees",          str(d.getEmployeeCount()));
        writeRow(sheet.createRow(r++), "Trainees",           str(d.getTraineeCount()));
        writeRow(sheet.createRow(r++), "Interns",            str(d.getInternCount()));

        if (d.getByDepartment() != null) {
            r++; // blank separator row
            writeRow(sheet.createRow(r++), "--- By Department ---", "");
            for (var entry : d.getByDepartment().entrySet()) {
                writeRow(sheet.createRow(r++), entry.getKey(), str(entry.getValue()));
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // SHARED HELPERS
    // ══════════════════════════════════════════════════════════

    private PdfPTable createTable(String[] headers, Font hf) throws DocumentException {
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

    private void addCells(PdfPTable table, Font font, String... values) {
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "", font));
            cell.setPadding(4);
            table.addCell(cell);
        }
    }

    private CellStyle buildHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font hf = wb.createFont();
        hf.setBold(true);
        hf.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(hf);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private void createHeader(Sheet sheet, CellStyle style, String... headers) {
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

    private void autoSize(Sheet sheet) {
        Row header = sheet.getRow(0);
        if (header != null) {
            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}