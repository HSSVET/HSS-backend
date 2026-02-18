package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

  List<Prescription> findByAnimalAnimalId(Long animalId);

  List<Prescription> findByAnimalAnimalIdOrderByDateDesc(Long animalId);

  List<Prescription> findByAnimalAnimalIdAndStatus(Long animalId, Prescription.Status status);
}
