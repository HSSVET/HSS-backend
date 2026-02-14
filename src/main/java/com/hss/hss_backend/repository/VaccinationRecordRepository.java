package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {

    List<VaccinationRecord> findByAnimalAnimalId(Long animalId);

    List<VaccinationRecord> findByVaccineVaccineId(Long vaccineId);

    List<VaccinationRecord> findByVaccineNameContainingIgnoreCase(String vaccineName);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.animal.animalId = :animalId AND vr.date BETWEEN :startDate AND :endDate")
    List<VaccinationRecord> findByAnimalIdAndDateBetween(@Param("animalId") Long animalId, 
                                                        @Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.nextDueDate <= :date AND vr.nextDueDate IS NOT NULL")
    List<VaccinationRecord> findDueVaccinations(@Param("date") LocalDate date);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.animal.animalId = :animalId AND vr.nextDueDate <= :date AND vr.nextDueDate IS NOT NULL")
    List<VaccinationRecord> findDueVaccinationsForAnimal(@Param("animalId") Long animalId, @Param("date") LocalDate date);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.nextDueDate BETWEEN :startDate AND :endDate AND vr.nextDueDate IS NOT NULL")
    List<VaccinationRecord> findVaccinationsDueBetween(@Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.batchNumber = :batchNumber")
    List<VaccinationRecord> findByBatchNumber(@Param("batchNumber") String batchNumber);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.veterinarianName = :veterinarianName")
    List<VaccinationRecord> findByVeterinarianName(@Param("veterinarianName") String veterinarianName);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.animal.animalId = :animalId AND vr.vaccine.vaccineId = :vaccineId")
    List<VaccinationRecord> findByAnimalAnimalIdAndVaccineVaccineId(@Param("animalId") Long animalId, 
                                                                     @Param("vaccineId") Long vaccineId);
}
