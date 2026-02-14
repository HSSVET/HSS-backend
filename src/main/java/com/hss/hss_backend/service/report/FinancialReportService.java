package com.hss.hss_backend.service.report;

import com.hss.hss_backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialReportService {

    private final InvoiceRepository invoiceRepository;

    public Map<String, Object> generateDailyReport(LocalDate date) {
        log.info("Generating daily financial report for date: {}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("reportType", "DAILY_FINANCIAL");
        
        BigDecimal totalRevenue = invoiceRepository.getTotalAmountInDateRange(date, date);
        Long totalInvoices = invoiceRepository.countByStatus(com.hss.hss_backend.entity.Invoice.Status.PENDING) +
            invoiceRepository.countByStatus(com.hss.hss_backend.entity.Invoice.Status.PAID);
        
        report.put("totalRevenue", totalRevenue);
        report.put("totalInvoices", totalInvoices);
        report.put("averageInvoiceAmount", totalInvoices > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return report;
    }

    public Map<String, Object> generateWeeklyReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating weekly financial report from {} to {}", startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("reportType", "WEEKLY_FINANCIAL");
        
        BigDecimal totalRevenue = invoiceRepository.getTotalAmountInDateRange(startDate, endDate);
        var invoices = invoiceRepository.findByDateBetween(startDate, endDate);
        Long totalInvoices = (long) invoices.size();
        
        report.put("totalRevenue", totalRevenue);
        report.put("totalInvoices", totalInvoices);
        report.put("averageInvoiceAmount", totalInvoices > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return report;
    }

    public Map<String, Object> generateMonthlyReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating monthly financial report from {} to {}", startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("reportType", "MONTHLY_FINANCIAL");
        
        BigDecimal totalRevenue = invoiceRepository.getTotalAmountInDateRange(startDate, endDate);
        BigDecimal paidRevenue = invoiceRepository.getTotalAmountByStatus(
            com.hss.hss_backend.entity.Invoice.Status.PAID);
        
        report.put("totalRevenue", totalRevenue);
        report.put("paidRevenue", paidRevenue);
        report.put("pendingRevenue", totalRevenue.subtract(paidRevenue));
        
        return report;
    }
}

