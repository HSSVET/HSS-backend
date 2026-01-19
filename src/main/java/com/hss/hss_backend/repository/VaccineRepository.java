package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    
    Optional<Vaccine> findByVaccineName(String vaccineName);
    
    List<Vaccine> findByVaccineNameContainingIgnoreCase(String vaccineName);
    
    @Query("SELECT v FROM Vaccine v WHERE v.boosterRequired = true")
    List<Vaccine> findBoosterRequiredVaccines();
}
