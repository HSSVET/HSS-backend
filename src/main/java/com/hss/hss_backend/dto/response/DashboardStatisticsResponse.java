package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {

    private Long totalAnimals;
    private Long totalOwners;
    private Long totalAppointments;
    private Long pendingAppointments;
    private Long completedAppointments;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private Long totalInvoices;
    private Long pendingInvoices;
    private Long overdueInvoices;
    private Long lowStockItems;
    private Long outOfStockItems;
    private Long upcomingVaccinations;
    private Long overdueVaccinations;
    private Map<String, Long> appointmentsByStatus;
    private Map<String, Long> animalsBySpecies;
    private Map<String, BigDecimal> revenueByMonth;
}

