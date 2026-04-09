package com.gm.hrms.report.service.impl;

import com.gm.hrms.exception.PayslipReportException;
import com.gm.hrms.report.model.PayslipReportData;
import com.gm.hrms.report.service.PayslipReportService;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
@Slf4j
public class ThymeleafPayslipReportService implements PayslipReportService {


    private final TemplateEngine templateEngine;

    @Override
    public byte[] generatePdf(PayslipReportData data) {
        try {
            String html = renderHtml(data);

            byte[] pdf = renderPdf(html);

            log.debug("Payslip PDF generated successfully ({} bytes).", pdf.length);
            return pdf;

        } catch (Exception ex) {
            log.error("Failed to generate payslip PDF: {}", ex.getMessage(), ex);
            throw new PayslipReportException(
                    "Failed to generate payslip PDF: " + ex.getMessage(), ex);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private String renderHtml(PayslipReportData data) {
        Context ctx = new Context(Locale.ENGLISH);

        // Scalar fields
        ctx.setVariable("companyName",      data.getCompanyName());
        ctx.setVariable("companyAddress",   data.getCompanyAddress());
        ctx.setVariable("logoBase64",       data.getLogoBase64());
        ctx.setVariable("payslipMonthYear", data.getPayslipMonthYear());

        ctx.setVariable("employeeName",     data.getEmployeeName());
        ctx.setVariable("employeeCode",     data.getEmployeeCode());
        ctx.setVariable("department",       data.getDepartment());
        ctx.setVariable("designation",      data.getDesignation());
        ctx.setVariable("payDate",          data.getPayDate());
        ctx.setVariable("dateOfJoining",    data.getDateOfJoining());
        ctx.setVariable("gender",           data.getGender());

        ctx.setVariable("bankName",         data.getBankName());
        ctx.setVariable("accountNumber",    data.getAccountNumber());
        ctx.setVariable("ifscCode",         data.getIfscCode());
        ctx.setVariable("paymentMode",      data.getPaymentMode());
        ctx.setVariable("panNumber",        data.getPanNumber());
        ctx.setVariable("pfNumber",         data.getPfNumber());

        ctx.setVariable("paidDays",         data.getPaidDays());
        ctx.setVariable("lopDays",          data.getLopDays());

        // Format currency values with Indian Rupee symbol
        ctx.setVariable("grossEarnings",    formatRupee(data.getGrossEarnings()));
        ctx.setVariable("totalDeductions",  formatRupee(data.getTotalDeductions()));
        ctx.setVariable("netPayable",       formatRupee(data.getNetPayable()));
        ctx.setVariable("netPayableWords",  data.getNetPayableWords());

        // Component rows — amounts pre-formatted so template stays simple
        ctx.setVariable("rows", data.getRows());

        return templateEngine.process("payslip", ctx);
    }

    private byte[] renderPdf(String html) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.useFont(extractFontToTempFile("/fonts/NotoSans-Regular.ttf"),
                    "Noto Sans", 400, BaseRendererBuilder.FontStyle.NORMAL, true);
            builder.useFont(extractFontToTempFile("/fonts/NotoSans-Bold.ttf"),
                    "Noto Sans", 700, BaseRendererBuilder.FontStyle.NORMAL, true);

            builder.withHtmlContent(html, null);
            builder.toStream(bos);
            builder.run();
            return bos.toByteArray();
        }
    }

    private File extractFontToTempFile(String classpathResource) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Font not found on classpath: " + classpathResource);
            }
            String suffix = classpathResource.substring(classpathResource.lastIndexOf('.'));
            File tmp = File.createTempFile("openhtmltopdf-font-", suffix);
            tmp.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tmp)) {
                in.transferTo(out);
            }
            return tmp;
        }
    }

    public static String formatRupee(Double amount) {
        if (amount == null) return "₹0.00";
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        return "₹" + fmt.format(amount);
    }
}