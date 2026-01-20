package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.AnimalWeightHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnimalWeightHistoryRepository extends JpaRepository<AnimalWeightHistory, Long> {
  List<AnimalWeightHistory> findByAnimal_AnimalIdOrderByMeasuredAtDesc(Long animalId);

  List<AnimalWeightHistory> findByAnimal_AnimalIdAndMeasuredAtBetweenOrderByMeasuredAtAsc(Long animalId,
      LocalDate startDate, LocalDate endDate);
}
