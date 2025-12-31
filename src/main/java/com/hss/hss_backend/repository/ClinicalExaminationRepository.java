package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.ClinicalExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalExaminationRepository extends JpaRepository<ClinicalExamination, Long> {

  List<ClinicalExamination> findByAnimalAnimalId(Long animalId);

  List<ClinicalExamination> findByAnimalAnimalIdOrderByDateDesc(Long animalId);
}
