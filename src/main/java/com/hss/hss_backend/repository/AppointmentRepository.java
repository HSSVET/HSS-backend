package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

       // Basic query methods
       List<Appointment> findByAnimalAnimalId(Long animalId);

       List<Appointment> findByVeterinarianId(Long veterinarianId);

       List<Appointment> findByStatus(Appointment.Status status);

       List<Appointment> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

       List<Appointment> findByAnimalAnimalIdAndDateTimeBetween(Long animalId, LocalDateTime startDateTime,
                     LocalDateTime endDateTime);

       // Date range queries
       List<Appointment> findByDateTimeAfter(LocalDateTime dateTime);

       List<Appointment> findByDateTimeBefore(LocalDateTime dateTime);

       List<Appointment> findByDateTimeBetweenOrderByDateTimeAsc(LocalDateTime startDateTime,
                     LocalDateTime endDateTime);

       // Status and date combinations
       List<Appointment> findByStatusAndDateTimeBetween(Appointment.Status status, LocalDateTime startDateTime,
                     LocalDateTime endDateTime);

       List<Appointment> findByStatusAndDateTimeAfter(Appointment.Status status, LocalDateTime dateTime);

       // Custom queries with @Query
       @Query("SELECT a FROM Appointment a WHERE a.animal.owner.clinic.clinicId = :clinicId")
       Page<Appointment> findByClinicId(@Param("clinicId") Long clinicId, Pageable pageable);

       @Query("SELECT a FROM Appointment a WHERE a.animal.owner.ownerId = :ownerId")
       List<Appointment> findByOwnerId(@Param("ownerId") Long ownerId);

       @Query("SELECT a FROM Appointment a WHERE a.animal.owner.ownerId = :ownerId AND a.dateTime BETWEEN :startDateTime AND :endDateTime")
       List<Appointment> findByOwnerIdAndDateRange(@Param("ownerId") Long ownerId,
                     @Param("startDateTime") LocalDateTime startDateTime,
                     @Param("endDateTime") LocalDateTime endDateTime);

       @Query("SELECT a FROM Appointment a WHERE a.veterinarianId = :veterinarianId AND a.dateTime BETWEEN :startDateTime AND :endDateTime")
       List<Appointment> findByVeterinarianIdAndDateRange(@Param("veterinarianId") Long veterinarianId,
                     @Param("startDateTime") LocalDateTime startDateTime,
                     @Param("endDateTime") LocalDateTime endDateTime);

       @Query("SELECT a FROM Appointment a WHERE a.dateTime >= :fromDateTime AND a.status IN :statuses ORDER BY a.dateTime ASC")
       List<Appointment> findUpcomingAppointments(@Param("fromDateTime") LocalDateTime fromDateTime,
                     @Param("statuses") List<Appointment.Status> statuses);

       @Query("SELECT a FROM Appointment a WHERE a.animal.animalId = :animalId AND a.status = :status ORDER BY a.dateTime DESC")
       List<Appointment> findByAnimalIdAndStatusOrderByDateTimeDesc(@Param("animalId") Long animalId,
                     @Param("status") Appointment.Status status);

       @Query("SELECT a FROM Appointment a WHERE a.subject LIKE %:subject%")
       List<Appointment> findBySubjectContaining(@Param("subject") String subject);

       @Query("SELECT a FROM Appointment a WHERE a.notes LIKE %:notes%")
       List<Appointment> findByNotesContaining(@Param("notes") String notes);

       // Statistics queries
       @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
       Long countByStatus(@Param("status") Appointment.Status status);

       @Query("SELECT COUNT(a) FROM Appointment a WHERE a.veterinarianId = :veterinarianId")
       Long countByVeterinarianId(@Param("veterinarianId") Long veterinarianId);

       @Query("SELECT COUNT(a) FROM Appointment a WHERE a.animal.animalId = :animalId")
       Long countByAnimalId(@Param("animalId") Long animalId);

       @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
       List<Object[]> getAppointmentCountByStatus();

       @Query("SELECT a.veterinarianId, COUNT(a) FROM Appointment a WHERE a.veterinarianId IS NOT NULL GROUP BY a.veterinarianId")
       List<Object[]> getAppointmentCountByVeterinarian();

       // Time-based statistics
       @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dateTime >= :startDate AND a.dateTime < :endDate")
       Long countAppointmentsInDateRange(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT a FROM Appointment a WHERE a.dateTime >= :startDate AND a.dateTime < :endDate ORDER BY a.dateTime ASC")
       List<Appointment> findAppointmentsInDateRange(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // Pagination support
       Page<Appointment> findByAnimalAnimalId(Long animalId, Pageable pageable);

       Page<Appointment> findByVeterinarianId(Long veterinarianId, Pageable pageable);

       Page<Appointment> findByStatus(Appointment.Status status, Pageable pageable);

       Page<Appointment> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime,
                     Pageable pageable);

       // Complex search
       @Query("SELECT a FROM Appointment a WHERE " +
                     "(:animalId IS NULL OR a.animal.animalId = :animalId) AND " +
                     "(:veterinarianId IS NULL OR a.veterinarianId = :veterinarianId) AND " +
                     "(:status IS NULL OR a.status = :status) AND " +
                     "(:startDateTime IS NULL OR a.dateTime >= :startDateTime) AND " +
                     "(:endDateTime IS NULL OR a.dateTime <= :endDateTime)")
       Page<Appointment> findByMultipleCriteria(@Param("animalId") Long animalId,
                     @Param("veterinarianId") Long veterinarianId,
                     @Param("status") Appointment.Status status,
                     @Param("startDateTime") LocalDateTime startDateTime,
                     @Param("endDateTime") LocalDateTime endDateTime,
                     Pageable pageable);
}