package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.AnimalCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalConditionRepository extends JpaRepository<AnimalCondition, Long> {
  List<AnimalCondition> findByAnimal_AnimalId(Long animalId);
}
