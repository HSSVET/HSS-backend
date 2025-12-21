package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    // Basic query methods
    List<Animal> findByOwnerOwnerId(Long ownerId);

    // Filter by Clinic
    @Query("SELECT a FROM Animal a WHERE a.owner.clinic.clinicId = :clinicId")
    Page<Animal> findByOwnerClinicClinicId(@Param("clinicId") Long clinicId, Pageable pageable);

    List<Animal> findBySpeciesSpeciesId(Long speciesId);

    List<Animal> findByBreedBreedId(Long breedId);

    Optional<Animal> findByMicrochipNo(String microchipNo);

    boolean existsByMicrochipNo(String microchipNo);

    List<Animal> findByNameContainingIgnoreCase(String name);

    List<Animal> findByOwnerFirstNameContainingIgnoreCaseOrOwnerLastNameContainingIgnoreCase(String firstName,
            String lastName);

    @Query("SELECT a FROM Animal a WHERE a.owner.firstName LIKE %:ownerName% OR a.owner.lastName LIKE %:ownerName%")
    List<Animal> findByOwnerNameContaining(@Param("ownerName") String ownerName);

    // Date range queries
    List<Animal> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);

    List<Animal> findByCreatedAtBetween(java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime);

    // Gender and status queries
    List<Animal> findByGender(Animal.Gender gender);

    List<Animal> findByWeightBetween(Double minWeight, Double maxWeight);

    // Pagination support
    Page<Animal> findByOwnerOwnerId(Long ownerId, Pageable pageable);

    Page<Animal> findBySpeciesSpeciesId(Long speciesId, Pageable pageable);

    // Custom queries with @Query
    @Query("SELECT a FROM Animal a WHERE a.owner.ownerId = :ownerId AND a.name LIKE %:name%")
    List<Animal> findByOwnerIdAndNameContaining(@Param("ownerId") Long ownerId, @Param("name") String name);

    @Query("SELECT a FROM Animal a WHERE a.species.name = :speciesName")
    List<Animal> findBySpeciesName(@Param("speciesName") String speciesName);

    @Query("SELECT a FROM Animal a WHERE a.breed.name = :breedName AND a.species.name = :speciesName")
    List<Animal> findByBreedNameAndSpeciesName(@Param("breedName") String breedName,
            @Param("speciesName") String speciesName);

    @Query("SELECT a FROM Animal a WHERE a.owner.email = :email")
    List<Animal> findByOwnerEmail(@Param("email") String email);

    @Query("SELECT a FROM Animal a WHERE a.owner.phone = :phone")
    List<Animal> findByOwnerPhone(@Param("phone") String phone);

    // Complex queries
    @Query("SELECT a FROM Animal a WHERE a.birthDate >= :minAge AND a.birthDate <= :maxAge")
    List<Animal> findByAgeRange(@Param("minAge") LocalDate minAge, @Param("maxAge") LocalDate maxAge);

    @Query("SELECT COUNT(a) FROM Animal a WHERE a.owner.ownerId = :ownerId")
    Long countByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT a FROM Animal a WHERE a.allergies IS NOT NULL AND a.allergies != ''")
    List<Animal> findAnimalsWithAllergies();

    @Query("SELECT a FROM Animal a WHERE a.chronicDiseases IS NOT NULL AND a.chronicDiseases != ''")
    List<Animal> findAnimalsWithChronicDiseases();

    // Statistics queries
    @Query("SELECT a.species.name, COUNT(a) FROM Animal a GROUP BY a.species.name")
    List<Object[]> getAnimalCountBySpecies();

    @Query("SELECT a.breed.name, COUNT(a) FROM Animal a WHERE a.species.speciesId = :speciesId GROUP BY a.breed.name")
    List<Object[]> getAnimalCountByBreedForSpecies(@Param("speciesId") Long speciesId);
}