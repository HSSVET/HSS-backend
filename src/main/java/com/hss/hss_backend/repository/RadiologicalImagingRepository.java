package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.RadiologicalImaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RadiologicalImagingRepository extends JpaRepository<RadiologicalImaging, Long> {
    
    // Used by AnimalMedicalRecordsController for aggregated views
    List<RadiologicalImaging> findByAnimalAnimalId(Long animalId);
    
    List<RadiologicalImaging> findByAnimal_AnimalIdOrderByDateDesc(Long animalId);
    
    List<RadiologicalImaging> findByType(String type);
    
    List<RadiologicalImaging> findByAnimal_AnimalIdAndType(Long animalId, String type);
    
    List<RadiologicalImaging> findByAnimal_AnimalIdAndDateBetween(Long animalId, LocalDate startDate, LocalDate endDate);
    
    long countByAnimal_AnimalId(Long animalId);
}
