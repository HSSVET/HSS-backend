package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.BehaviorNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BehaviorNoteRepository extends JpaRepository<BehaviorNote, Long> {
    
    List<BehaviorNote> findByAnimal_AnimalIdOrderByObservedDateDesc(Long animalId);
    
    List<BehaviorNote> findByClinic_ClinicIdOrderByObservedDateDesc(Long clinicId);
    
    List<BehaviorNote> findByCategory(BehaviorNote.BehaviorCategory category);
    
    List<BehaviorNote> findBySeverity(BehaviorNote.Severity severity);
    
    List<BehaviorNote> findByAnimal_AnimalIdAndSeverity(Long animalId, BehaviorNote.Severity severity);
    
    long countByAnimal_AnimalId(Long animalId);
}
