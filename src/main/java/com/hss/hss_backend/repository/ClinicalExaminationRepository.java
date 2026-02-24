package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.ClinicalExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClinicalExaminationRepository extends JpaRepository<ClinicalExamination, Long> {
    
    // Used by AnimalMedicalRecordsController for aggregated views
    List<ClinicalExamination> findByAnimalAnimalId(Long animalId);
    
    List<ClinicalExamination> findByAnimal_AnimalIdOrderByDateDesc(Long animalId);
    
    List<ClinicalExamination> findByAnimal_AnimalIdAndDateBetween(Long animalId, LocalDate startDate, LocalDate endDate);
    
    List<ClinicalExamination> findByVeterinarianNameContainingIgnoreCase(String veterinarianName);
    
    long countByAnimal_AnimalId(Long animalId);
}
