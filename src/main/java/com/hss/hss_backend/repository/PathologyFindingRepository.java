package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.PathologyFinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathologyFindingRepository extends JpaRepository<PathologyFinding, Long> {

  List<PathologyFinding> findByAnimalAnimalId(Long animalId);

  List<PathologyFinding> findByAnimalAnimalIdOrderByDateDesc(Long animalId);
}
