package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.VaccinationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationScheduleRepository extends JpaRepository<VaccinationSchedule, Long> {

    List<VaccinationSchedule> findByAnimalAnimalId(Long animalId);

    List<VaccinationSchedule> findByVaccineVaccineId(Long vaccineId);

    List<VaccinationSchedule> findByStatus(VaccinationSchedule.Status status);

    List<VaccinationSchedule> findByPriority(VaccinationSchedule.Priority priority);

    @Query("SELECT vs FROM VaccinationSchedule vs WHERE vs.animal.animalId = :animalId AND vs.status = 'PENDING'")
    List<VaccinationSchedule> findPendingSchedulesByAnimalId(@Param("animalId") Long animalId);

    @Query("SELECT vs FROM VaccinationSchedule vs WHERE vs.scheduledDate <= :date AND vs.status = 'PENDING'")
    List<VaccinationSchedule> findOverdueSchedules(@Param("date") LocalDate date);

    @Query("SELECT vs FROM VaccinationSchedule vs WHERE vs.scheduledDate BETWEEN :startDate AND :endDate AND vs.status = 'PENDING'")
    List<VaccinationSchedule> findSchedulesBetween(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT vs FROM VaccinationSchedule vs WHERE vs.animal.animalId = :animalId AND vs.scheduledDate BETWEEN :startDate AND :endDate")
    List<VaccinationSchedule> findSchedulesByAnimalAndDateRange(@Param("animalId") Long animalId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT vs FROM VaccinationSchedule vs WHERE vs.isOverdue = true AND vs.status = 'PENDING'")
    List<VaccinationSchedule> findOverduePendingSchedules();

    @Query("SELECT vs FROM VaccinationSchedule vs WHERE vs.animal.animalId = :animalId AND vs.vaccine.vaccineId = :vaccineId AND vs.status = 'PENDING'")
    List<VaccinationSchedule> findPendingSchedulesByAnimalAndVaccine(@Param("animalId") Long animalId,
                                                                       @Param("vaccineId") Long vaccineId);
}

