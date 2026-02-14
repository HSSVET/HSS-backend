package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.ReportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportScheduleRepository extends JpaRepository<ReportSchedule, Long> {

    List<ReportSchedule> findByIsActive(Boolean isActive);

    List<ReportSchedule> findByReportType(ReportSchedule.ReportType reportType);

    List<ReportSchedule> findByFrequency(ReportSchedule.Frequency frequency);

    @Query("SELECT rs FROM ReportSchedule rs WHERE rs.isActive = true")
    List<ReportSchedule> findActiveSchedules();

    @Query("SELECT rs FROM ReportSchedule rs WHERE rs.isActive = true AND rs.nextRun <= :now")
    List<ReportSchedule> findSchedulesToRun(@Param("now") LocalDateTime now);

    @Query("SELECT rs FROM ReportSchedule rs WHERE rs.reportType = :reportType AND rs.isActive = true")
    List<ReportSchedule> findActiveSchedulesByType(@Param("reportType") ReportSchedule.ReportType reportType);
}

