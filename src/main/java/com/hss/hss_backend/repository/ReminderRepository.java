package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByAppointmentAppointmentId(Long appointmentId);

    List<Reminder> findByStatus(Reminder.Status status);

    List<Reminder> findByChannel(Reminder.Channel channel);

    @Query("SELECT r FROM Reminder r WHERE r.sendTime <= :now AND r.status = 'PENDING'")
    List<Reminder> findPendingRemindersToSend(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reminder r WHERE r.sendTime BETWEEN :startTime AND :endTime")
    List<Reminder> findRemindersBetween(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Reminder r WHERE r.appointment.appointmentId = :appointmentId AND r.status = 'PENDING'")
    List<Reminder> findPendingRemindersByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT r FROM Reminder r WHERE r.status = 'FAILED' AND r.retryCount < :maxRetries")
    List<Reminder> findFailedRemindersForRetry(@Param("maxRetries") Integer maxRetries);
}

