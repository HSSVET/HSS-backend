package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatisticsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal paidRevenue;
    private BigDecimal pendingRevenue;
    private BigDecimal overdueRevenue;
    private Long totalInvoices;
    private Long paidInvoices;
    private Long pendingInvoices;
    private Long overdueInvoices;
    private BigDecimal averageInvoiceAmount;
    private BigDecimal totalTaxAmount;
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, Long> invoicesByStatus;
    private List<Map<String, Object>> topCustomers;
}

