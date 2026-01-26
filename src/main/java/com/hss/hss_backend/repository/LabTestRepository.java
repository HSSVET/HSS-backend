package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    List<LabTest> findByAnimalAnimalId(Long animalId);

    List<LabTest> findByStatus(LabTest.Status status);

    List<LabTest> findByTestNameContainingIgnoreCase(String testName);

    @Query("SELECT lt FROM LabTest lt WHERE lt.animal.animalId = :animalId AND lt.date BETWEEN :startDate AND :endDate")
    List<LabTest> findByAnimalIdAndDateBetween(@Param("animalId") Long animalId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT lt FROM LabTest lt WHERE lt.status = :status AND lt.date < :date")
    List<LabTest> findOverdueTests(@Param("status") LabTest.Status status, @Param("date") LocalDate date);

    @Query("SELECT lt FROM LabTest lt JOIN FETCH lt.animal")
    org.springframework.data.domain.Page<LabTest> findAllWithDetails(org.springframework.data.domain.Pageable pageable);

    @Query(value = "SELECT lt FROM LabTest lt JOIN FETCH lt.animal", countQuery = "SELECT COUNT(lt) FROM LabTest lt")
    org.springframework.data.domain.Page<LabTest> findAll(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT lt FROM LabTest lt WHERE lt.animal.animalId = :animalId AND lt.status = :status")
    List<LabTest> findByAnimalIdAndStatus(@Param("animalId") Long animalId, @Param("status") LabTest.Status status);

    @Query("SELECT COUNT(lt) FROM LabTest lt WHERE lt.status = :status")
    long countByStatus(@Param("status") LabTest.Status status);

    @Query("SELECT COUNT(lt) FROM LabTest lt WHERE lt.date = :date")
    long countByDate(@Param("date") java.time.LocalDate date);

    @Query("SELECT lt FROM LabTest lt JOIN FETCH lt.animal WHERE lt.status = :status")
    org.springframework.data.domain.Page<LabTest> findByStatus(@Param("status") LabTest.Status status,
            org.springframework.data.domain.Pageable pageable);
}
