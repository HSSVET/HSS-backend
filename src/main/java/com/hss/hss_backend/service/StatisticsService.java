package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.DashboardStatisticsResponse;
import com.hss.hss_backend.dto.response.FinancialStatisticsResponse;
import com.hss.hss_backend.dto.response.MedicalStatisticsResponse;
import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.entity.Invoice;
import com.hss.hss_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatisticsService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final StockProductRepository stockProductRepository;
    private final VaccinationScheduleRepository vaccinationScheduleRepository;

    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getDashboardStatistics() {
        log.info("Calculating dashboard statistics");

        // Hayvan ve sahip sayıları
        Long totalAnimals = animalRepository.count();
        Long totalOwners = ownerRepository.getTotalOwnerCount();

        // Randevu istatistikleri
        Long totalAppointments = (long) appointmentRepository.findAll().size();
        Long pendingAppointments = appointmentRepository.countByStatus(Appointment.Status.SCHEDULED) +
            appointmentRepository.countByStatus(Appointment.Status.CONFIRMED);
        Long completedAppointments = appointmentRepository.countByStatus(Appointment.Status.COMPLETED);

        // Fatura istatistikleri
        BigDecimal totalRevenue = invoiceRepository.getTotalAmountByStatus(Invoice.Status.PAID);
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        BigDecimal monthlyRevenue = invoiceRepository.getTotalAmountInDateRange(monthStart, today);
        Long totalInvoices = (long) invoiceRepository.findAll().size();
        Long pendingInvoices = invoiceRepository.countByStatus(Invoice.Status.PENDING);
        Long overdueInvoices = (long) invoiceRepository.findOverduePendingInvoices().size();

        // Stok istatistikleri
        var lowStockProducts = stockProductRepository.findLowStockProducts();
        var outOfStockProducts = stockProductRepository.findOutOfStockProducts();

        // Aşı istatistikleri
        var upcomingSchedules = vaccinationScheduleRepository.findSchedulesBetween(
            LocalDate.now(), LocalDate.now().plusDays(30));
        var overdueSchedules = vaccinationScheduleRepository.findOverduePendingSchedules();

        // Randevu durum dağılımı
        Map<String, Long> appointmentsByStatus = new HashMap<>();
        for (Appointment.Status status : Appointment.Status.values()) {
            appointmentsByStatus.put(status.name(), appointmentRepository.countByStatus(status));
        }

        // Hayvan tür dağılımı
        var speciesCount = animalRepository.getAnimalCountBySpecies();
        Map<String, Long> animalsBySpecies = speciesCount.stream()
            .collect(Collectors.toMap(
                arr -> arr[0].toString(),
                arr -> ((Number) arr[1]).longValue()
            ));

        // Aylık gelir (son 6 ay)
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            LocalDate monthStartDate = monthDate.withDayOfMonth(1);
            LocalDate monthEndDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            BigDecimal monthRevenue = invoiceRepository.getTotalAmountInDateRange(monthStartDate, monthEndDate);
            revenueByMonth.put(monthDate.getYear() + "-" + String.format("%02d", monthDate.getMonthValue()), 
                monthRevenue != null ? monthRevenue : BigDecimal.ZERO);
        }

        return DashboardStatisticsResponse.builder()
            .totalAnimals(totalAnimals)
            .totalOwners(totalOwners)
            .totalAppointments(totalAppointments)
            .pendingAppointments(pendingAppointments)
            .completedAppointments(completedAppointments)
            .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
            .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
            .totalInvoices(totalInvoices)
            .pendingInvoices(pendingInvoices)
            .overdueInvoices(overdueInvoices)
            .lowStockItems((long) lowStockProducts.size())
            .outOfStockItems((long) outOfStockProducts.size())
            .upcomingVaccinations((long) upcomingSchedules.size())
            .overdueVaccinations((long) overdueSchedules.size())
            .appointmentsByStatus(appointmentsByStatus)
            .animalsBySpecies(animalsBySpecies)
            .revenueByMonth(revenueByMonth)
            .build();
    }

    @Transactional(readOnly = true)
    public FinancialStatisticsResponse getFinancialStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating financial statistics from {} to {}", startDate, endDate);

        BigDecimal totalRevenue = invoiceRepository.getTotalAmountInDateRange(startDate, endDate);
        BigDecimal paidRevenue = invoiceRepository.getTotalAmountByStatus(Invoice.Status.PAID);
        BigDecimal pendingRevenue = invoiceRepository.getTotalAmountByStatus(Invoice.Status.PENDING);
        BigDecimal overdueRevenue = invoiceRepository.getTotalAmountByStatus(Invoice.Status.OVERDUE);

        var invoices = invoiceRepository.findByDateBetween(startDate, endDate);
        Long totalInvoices = (long) invoices.size();
        Long paidInvoices = invoiceRepository.countByStatus(Invoice.Status.PAID);
        Long pendingInvoices = invoiceRepository.countByStatus(Invoice.Status.PENDING);
        Long overdueInvoices = (long) invoiceRepository.findOverduePendingInvoices().size();

        BigDecimal averageInvoiceAmount = totalInvoices > 0 && totalRevenue != null ?
            totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal totalTaxAmount = invoices.stream()
            .map(Invoice::getTaxAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aylık gelir
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDate monthStart = current.withDayOfMonth(1);
            LocalDate monthEnd = current.withDayOfMonth(current.lengthOfMonth());
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }
            BigDecimal monthRevenue = invoiceRepository.getTotalAmountInDateRange(monthStart, monthEnd);
            revenueByMonth.put(monthStart.getYear() + "-" + String.format("%02d", monthStart.getMonthValue()),
                monthRevenue != null ? monthRevenue : BigDecimal.ZERO);
            current = monthEnd.plusDays(1);
        }

        // Fatura durum dağılımı
        Map<String, Long> invoicesByStatus = new HashMap<>();
        for (Invoice.Status status : Invoice.Status.values()) {
            invoicesByStatus.put(status.name(), invoiceRepository.countByStatus(status));
        }

        // En iyi müşteriler (top 10)
        var topCustomers = invoiceRepository.getTopCustomersByAmount(
            org.springframework.data.domain.PageRequest.of(0, 10));
        List<Map<String, Object>> topCustomersList = topCustomers.getContent().stream()
            .map(arr -> {
                Map<String, Object> customer = new HashMap<>();
                customer.put("ownerId", arr[0]);
                customer.put("firstName", arr[1]);
                customer.put("lastName", arr[2]);
                customer.put("invoiceCount", arr[3]);
                customer.put("totalAmount", arr[4]);
                return customer;
            })
            .collect(Collectors.toList());

        return FinancialStatisticsResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
            .paidRevenue(paidRevenue != null ? paidRevenue : BigDecimal.ZERO)
            .pendingRevenue(pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO)
            .overdueRevenue(overdueRevenue != null ? overdueRevenue : BigDecimal.ZERO)
            .totalInvoices(totalInvoices)
            .paidInvoices(paidInvoices)
            .pendingInvoices(pendingInvoices)
            .overdueInvoices(overdueInvoices)
            .averageInvoiceAmount(averageInvoiceAmount)
            .totalTaxAmount(totalTaxAmount)
            .revenueByMonth(revenueByMonth)
            .invoicesByStatus(invoicesByStatus)
            .topCustomers(topCustomersList)
            .build();
    }

    @Transactional(readOnly = true)
    public MedicalStatisticsResponse getMedicalStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating medical statistics from {} to {}", startDate, endDate);

        Long totalAnimals = animalRepository.count();
        Long totalAppointments = appointmentRepository.countAppointmentsInDateRange(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        Long completedAppointments = appointmentRepository.countByStatus(Appointment.Status.COMPLETED);
        Long cancelledAppointments = appointmentRepository.countByStatus(Appointment.Status.CANCELLED);

        // Aşı istatistikleri
        var upcomingSchedules = vaccinationScheduleRepository.findSchedulesBetween(
            LocalDate.now(), LocalDate.now().plusDays(30));
        var overdueSchedules = vaccinationScheduleRepository.findOverduePendingSchedules();
        var allSchedules = vaccinationScheduleRepository.findAll();
        Long totalVaccinations = (long) allSchedules.size();

        // Hayvan tür dağılımı
        var speciesCount = animalRepository.getAnimalCountBySpecies();
        Map<String, Long> animalsBySpecies = speciesCount.stream()
            .collect(Collectors.toMap(
                arr -> arr[0].toString(),
                arr -> ((Number) arr[1]).longValue()
            ));

        // Randevu tipi dağılımı (şimdilik boş, appointment entity'de type yok)
        Map<String, Long> appointmentsByType = new HashMap<>();

        return MedicalStatisticsResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalAnimals(totalAnimals)
            .totalAppointments(totalAppointments)
            .completedAppointments(completedAppointments)
            .cancelledAppointments(cancelledAppointments)
            .totalVaccinations(totalVaccinations)
            .upcomingVaccinations((long) upcomingSchedules.size())
            .overdueVaccinations((long) overdueSchedules.size())
            .animalsBySpecies(animalsBySpecies)
            .animalsByBreed(new HashMap<>())
            .appointmentsByType(appointmentsByType)
            .mostCommonTreatments(new ArrayList<>())
            .vaccinationsByType(new HashMap<>())
            .build();
    }

    public void calculateAndCacheStatistics() {
        log.info("Calculating and caching statistics");
        // İstatistikleri hesapla ve cache'le
        // Şimdilik sadece log, cache implementasyonu eklenebilir
        try {
            getDashboardStatistics();
            log.info("Statistics calculated successfully");
        } catch (Exception e) {
            log.error("Error calculating statistics", e);
        }
    }
}

