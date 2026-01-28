package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

  // Find today's queue for a specific clinic
  List<QueueEntry> findByClinicClinicIdAndQueueDateOrderByQueueNumberAsc(Long clinicId, LocalDate queueDate);

  // Find active queue entries (waiting or in progress)
  List<QueueEntry> findByClinicClinicIdAndQueueDateAndStatusInOrderByPriorityDescQueueNumberAsc(
      Long clinicId, LocalDate queueDate, List<QueueEntry.QueueStatus> statuses);

  // Find next queue number for today
  @Query("SELECT COALESCE(MAX(q.queueNumber), 0) + 1 FROM QueueEntry q " +
      "WHERE q.clinic.clinicId = :clinicId AND q.queueDate = :queueDate")
  Integer findNextQueueNumber(@Param("clinicId") Long clinicId, @Param("queueDate") LocalDate queueDate);

  // Find by appointment
  Optional<QueueEntry> findByAppointmentAppointmentId(Long appointmentId);

  // Find by animal and date
  Optional<QueueEntry> findByAnimalAnimalIdAndQueueDate(Long animalId, LocalDate queueDate);

  // Find entries assigned to veterinarian
  List<QueueEntry> findByAssignedVeterinarianIdAndQueueDateAndStatusOrderByQueueNumberAsc(
      Long veterinarianId, LocalDate queueDate, QueueEntry.QueueStatus status);

  // Count patients in queue
  @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.clinic.clinicId = :clinicId " +
      "AND q.queueDate = :queueDate AND q.status IN (:statuses)")
  Long countByClinicAndDateAndStatus(@Param("clinicId") Long clinicId,
      @Param("queueDate") LocalDate queueDate,
      @Param("statuses") List<QueueEntry.QueueStatus> statuses);
}
