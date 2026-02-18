package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.RadiologicalImaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RadiologicalImagingRepository extends JpaRepository<RadiologicalImaging, Long> {

  List<RadiologicalImaging> findByAnimalAnimalId(Long animalId);

  List<RadiologicalImaging> findByAnimalAnimalIdOrderByDateDesc(Long animalId);
}
