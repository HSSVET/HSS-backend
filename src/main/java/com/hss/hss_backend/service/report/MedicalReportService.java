package com.hss.hss_backend.service.report;

import com.hss.hss_backend.repository.AnimalRepository;
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
public class MedicalReportService {

    private final AnimalRepository animalRepository;
    private final AppointmentRepository appointmentRepository;

    public Map<String, Object> generateStatisticsReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating medical statistics report from {} to {}", startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("reportType", "MEDICAL_STATISTICS");
        
        // Hayvan istatistikleri
        Long totalAnimals = animalRepository.count();
        report.put("totalAnimals", totalAnimals);
        
        // Randevu istatistikleri
        Long totalAppointments = appointmentRepository.countAppointmentsInDateRange(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        report.put("totalAppointments", totalAppointments);
        
        // Tür bazlı dağılım
        var speciesCount = animalRepository.getAnimalCountBySpecies();
        report.put("speciesDistribution", speciesCount);
        
        return report;
    }
}

