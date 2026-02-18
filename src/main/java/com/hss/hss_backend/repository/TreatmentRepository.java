package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    
    List<Treatment> findByAnimal_AnimalIdOrderByStartDateDesc(Long animalId);
    
    List<Treatment> findByClinic_ClinicIdOrderByStartDateDesc(Long clinicId);
    
    List<Treatment> findByStatus(Treatment.TreatmentStatus status);
    
    List<Treatment> findByAnimal_AnimalIdAndStatus(Long animalId, Treatment.TreatmentStatus status);
    
    List<Treatment> findByTreatmentType(Treatment.TreatmentType treatmentType);
    
    long countByAnimal_AnimalId(Long animalId);
}
