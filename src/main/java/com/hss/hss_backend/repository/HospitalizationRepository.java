package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Hospitalization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalizationRepository extends JpaRepository<Hospitalization, Long> {
  List<Hospitalization> findByAnimal_AnimalId(Long animalId);

  List<Hospitalization> findByStatus(String status);
}
