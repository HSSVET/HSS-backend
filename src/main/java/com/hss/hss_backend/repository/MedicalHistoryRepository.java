package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    // Used by detailed medical history views (new API)
    List<MedicalHistory> findByAnimalAnimalId(Long animalId);
    
    List<MedicalHistory> findByAnimal_AnimalIdOrderByDateDesc(Long animalId);
    
    List<MedicalHistory> findByAnimal_AnimalIdAndDateBetween(Long animalId, LocalDate startDate, LocalDate endDate);
    
    List<MedicalHistory> findByDiagnosisContainingIgnoreCase(String diagnosis);
    
    long countByAnimal_AnimalId(Long animalId);
}
