package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
  List<Surgery> findByAnimal_AnimalId(Long animalId);

  List<Surgery> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

  List<Surgery> findByStatus(String status);
}
