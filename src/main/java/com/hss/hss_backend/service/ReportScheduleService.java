package com.hss.hss_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hss.hss_backend.dto.request.ReportScheduleCreateRequest;
import com.hss.hss_backend.dto.request.ReportScheduleUpdateRequest;
import com.hss.hss_backend.dto.response.ReportDataResponse;
import com.hss.hss_backend.dto.response.ReportScheduleResponse;
import com.hss.hss_backend.entity.ReportSchedule;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.ReportScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportScheduleService {

    private final ReportScheduleRepository scheduleRepository;
    private final ReportGeneratorService reportGeneratorService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public ReportScheduleResponse createSchedule(ReportScheduleCreateRequest request) {
        log.info("Creating report schedule: {}", request.getName());

        String cronExpression = request.getCronExpression();
        if (cronExpression == null || cronExpression.isEmpty()) {
            cronExpression = generateCronExpression(request.getFrequency());
        }

        LocalDateTime nextRun = calculateNextRun(request.getFrequency(), cronExpression);

        ReportSchedule schedule = ReportSchedule.builder()
                .name(request.getName())
                .description(request.getDescription())
                .frequency(request.getFrequency())
                .cronExpression(cronExpression)
                .nextRun(nextRun)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .reportType(request.getReportType())
                .parameters(request.getParameters())
                .emailRecipients(request.getEmailRecipients())
                .build();

        ReportSchedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Report schedule created successfully with ID: {}", savedSchedule.getReportId());
        return toResponse(savedSchedule);
    }

    @Transactional(readOnly = true)
    public ReportScheduleResponse getScheduleById(Long id) {
        log.info("Fetching report schedule with ID: {}", id);
        ReportSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", id));
        return toResponse(schedule);
    }

    @Transactional(readOnly = true)
    public List<ReportScheduleResponse> getAllSchedules() {
        log.info("Fetching all report schedules");
        List<ReportSchedule> schedules = scheduleRepository.findAll();
        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportScheduleResponse> getActiveSchedules() {
        log.info("Fetching active report schedules");
        List<ReportSchedule> schedules = scheduleRepository.findActiveSchedules();
        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ReportScheduleResponse updateSchedule(Long id, ReportScheduleUpdateRequest request) {
        log.info("Updating report schedule with ID: {}", id);
        ReportSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", id));

        if (request.getName() != null) {
            schedule.setName(request.getName());
        }
        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }
        if (request.getFrequency() != null) {
            schedule.setFrequency(request.getFrequency());
        }
        if (request.getCronExpression() != null) {
            schedule.setCronExpression(request.getCronExpression());
        }
        if (request.getReportType() != null) {
            schedule.setReportType(request.getReportType());
        }
        if (request.getParameters() != null) {
            schedule.setParameters(request.getParameters());
        }
        if (request.getEmailRecipients() != null) {
            schedule.setEmailRecipients(request.getEmailRecipients());
        }
        if (request.getIsActive() != null) {
            schedule.setIsActive(request.getIsActive());
        }

        // Next run'ı yeniden hesapla
        if (request.getFrequency() != null || request.getCronExpression() != null) {
            String cron = schedule.getCronExpression() != null ? 
                schedule.getCronExpression() : generateCronExpression(schedule.getFrequency());
            schedule.setNextRun(calculateNextRun(schedule.getFrequency(), cron));
        }

        ReportSchedule updatedSchedule = scheduleRepository.save(schedule);
        log.info("Report schedule updated successfully");
        return toResponse(updatedSchedule);
    }

    public void deleteSchedule(Long id) {
        log.info("Deleting report schedule with ID: {}", id);
        ReportSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", id));
        scheduleRepository.delete(schedule);
        log.info("Report schedule deleted successfully");
    }

    public ReportDataResponse executeSchedule(Long scheduleId) {
        log.info("Executing report schedule ID: {}", scheduleId);
        ReportSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("ReportSchedule", scheduleId));

        // Parametreleri parse et
        Map<String, Object> parameters = new HashMap<>();
        if (schedule.getParameters() != null && !schedule.getParameters().isEmpty()) {
            try {
                parameters = objectMapper.readValue(schedule.getParameters(),
                    new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.warn("Error parsing report parameters: {}", e.getMessage());
            }
        }

        // Rapor oluştur
        Map<String, Object> reportData = reportGeneratorService.generateReport(
            schedule.getReportType().name(), parameters);

        // Raporu email ile gönder
        if (schedule.getEmailRecipients() != null && !schedule.getEmailRecipients().isEmpty()) {
            sendReportByEmail(schedule, reportData);
        }

        // Schedule'ı güncelle
        schedule.setLastRun(LocalDateTime.now());
        schedule.setNextRun(calculateNextRun(schedule.getFrequency(), schedule.getCronExpression()));
        scheduleRepository.save(schedule);

        log.info("Report schedule executed successfully");
        return ReportDataResponse.builder()
                .reportId(String.valueOf(scheduleId))
                .reportName(schedule.getName())
                .reportType(schedule.getReportType().name())
                .generatedAt(LocalDateTime.now())
                .data(reportData)
                .format("JSON")
                .build();
    }

    public void processScheduledReports() {
        log.info("Processing scheduled reports");
        LocalDateTime now = LocalDateTime.now();
        List<ReportSchedule> schedulesToRun = scheduleRepository.findSchedulesToRun(now);

        log.info("Found {} reports to generate", schedulesToRun.size());

        for (ReportSchedule schedule : schedulesToRun) {
            try {
                executeSchedule(schedule.getReportId());
            } catch (Exception e) {
                log.error("Error executing report schedule {}: {}", schedule.getReportId(), e.getMessage(), e);
            }
        }
    }

    private void sendReportByEmail(ReportSchedule schedule, Map<String, Object> reportData) {
        try {
            String subject = String.format("Rapor: %s", schedule.getName());
            String body = formatReportAsEmail(reportData);

            for (String email : schedule.getEmailRecipients()) {
                emailService.sendEmail(email, subject, body);
            }
            log.info("Report sent to {} recipients", schedule.getEmailRecipients().size());
        } catch (Exception e) {
            log.error("Error sending report by email: {}", e.getMessage(), e);
        }
    }

    private String formatReportAsEmail(Map<String, Object> reportData) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rapor Detayları:\n\n");
        
        for (Map.Entry<String, Object> entry : reportData.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        return sb.toString();
    }

    private String generateCronExpression(ReportSchedule.Frequency frequency) {
        switch (frequency) {
            case DAILY:
                return "0 8 * * *"; // Her gün 08:00
            case WEEKLY:
                return "0 9 * * 1"; // Her Pazartesi 09:00
            case MONTHLY:
                return "0 10 1 * *"; // Her ayın 1'i 10:00
            case QUARTERLY:
                return "0 9 1 1,4,7,10 *"; // Her çeyreğin ilk günü 09:00
            case YEARLY:
                return "0 9 1 1 *"; // Her yılın ilk günü 09:00
            default:
                return "0 8 * * *";
        }
    }

    private LocalDateTime calculateNextRun(ReportSchedule.Frequency frequency, String cronExpression) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (frequency) {
            case DAILY:
                return now.plusDays(1).withHour(8).withMinute(0).withSecond(0);
            case WEEKLY:
                return now.plusWeeks(1).withHour(9).withMinute(0).withSecond(0);
            case MONTHLY:
                return now.plusMonths(1).withDayOfMonth(1).withHour(10).withMinute(0).withSecond(0);
            case QUARTERLY:
                return now.plusMonths(3).withDayOfMonth(1).withHour(9).withMinute(0).withSecond(0);
            case YEARLY:
                return now.plusYears(1).withDayOfYear(1).withHour(9).withMinute(0).withSecond(0);
            default:
                return now.plusDays(1);
        }
    }

    private ReportScheduleResponse toResponse(ReportSchedule schedule) {
        return ReportScheduleResponse.builder()
                .reportId(schedule.getReportId())
                .name(schedule.getName())
                .description(schedule.getDescription())
                .frequency(schedule.getFrequency())
                .cronExpression(schedule.getCronExpression())
                .lastRun(schedule.getLastRun())
                .nextRun(schedule.getNextRun())
                .isActive(schedule.getIsActive())
                .reportType(schedule.getReportType())
                .parameters(schedule.getParameters())
                .emailRecipients(schedule.getEmailRecipients())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}

