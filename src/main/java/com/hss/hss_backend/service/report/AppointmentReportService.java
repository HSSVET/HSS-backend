package com.hss.hss_backend.service.report;

import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentReportService {

    private final AppointmentRepository appointmentRepository;

    public Map<String, Object> generateDailySummary(LocalDate date) {
        log.info("Generating daily appointment summary for date: {}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("reportType", "DAILY_APPOINTMENT_SUMMARY");
        
        var appointments = appointmentRepository.findAppointmentsInDateRange(
            date.atStartOfDay(), date.atTime(23, 59, 59));
        
        long scheduled = appointments.stream()
            .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
            .count();
        long completed = appointments.stream()
            .filter(a -> a.getStatus() == Appointment.Status.COMPLETED)
            .count();
        long cancelled = appointments.stream()
            .filter(a -> a.getStatus() == Appointment.Status.CANCELLED)
            .count();
        
        report.put("totalAppointments", appointments.size());
        report.put("scheduled", scheduled);
        report.put("completed", completed);
        report.put("cancelled", cancelled);
        
        return report;
    }
}

