package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    // Basic query methods (clinic-scoped)
    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicId = :clinicId")
    Page<Owner> findByClinicClinicId(@Param("clinicId") Long clinicId, Pageable pageable);

    @Query("SELECT o FROM Owner o WHERE o.ownerId = :ownerId AND o.clinic.clinicId = :clinicId")
    Optional<Owner> findByOwnerIdAndClinicClinicId(@Param("ownerId") Long ownerId, @Param("clinicId") Long clinicId);

    Optional<Owner> findByEmail(String email);

    Optional<Owner> findByPhone(String phone);

    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicId = :clinicId AND o.email = :email")
    Optional<Owner> findByClinicClinicIdAndEmail(@Param("clinicId") Long clinicId, @Param("email") String email);

    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicId = :clinicId AND o.phone = :phone")
    Optional<Owner> findByClinicClinicIdAndPhone(@Param("clinicId") Long clinicId, @Param("phone") String phone);

    List<Owner> findByFirstNameContainingIgnoreCase(String firstName);

    List<Owner> findByLastNameContainingIgnoreCase(String lastName);

    List<Owner> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    // Date range queries
    List<Owner> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Address queries
    List<Owner> findByAddressContainingIgnoreCase(String address);

    // Custom queries with @Query (clinic-scoped)
    @Query("SELECT o FROM Owner o WHERE o.firstName LIKE %:name% OR o.lastName LIKE %:name%")
    List<Owner> findByNameContaining(@Param("name") String name);

    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicId = :clinicId AND (o.firstName LIKE %:name% OR o.lastName LIKE %:name%)")
    List<Owner> findByClinicClinicIdAndNameContaining(@Param("clinicId") Long clinicId, @Param("name") String name);

    @Query("SELECT o FROM Owner o WHERE o.email LIKE %:email%")
    List<Owner> findByEmailContaining(@Param("email") String email);

    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicId = :clinicId AND o.email LIKE %:email%")
    List<Owner> findByClinicClinicIdAndEmailContaining(@Param("clinicId") Long clinicId, @Param("email") String email);

    @Query("SELECT o FROM Owner o WHERE o.phone LIKE %:phone%")
    List<Owner> findByPhoneContaining(@Param("phone") String phone);

    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicId = :clinicId AND o.phone LIKE %:phone%")
    List<Owner> findByClinicClinicIdAndPhoneContaining(@Param("clinicId") Long clinicId, @Param("phone") String phone);

    // Owner statistics
    @Query("SELECT COUNT(o) FROM Owner o")
    Long getTotalOwnerCount();

    @Query("SELECT o FROM Owner o WHERE SIZE(o.animals) > :minAnimalCount")
    List<Owner> findOwnersWithMoreThanXAnimals(@Param("minAnimalCount") int minAnimalCount);

    @Query("SELECT o FROM Owner o WHERE SIZE(o.animals) = 0")
    List<Owner> findOwnersWithoutAnimals();

    // Recent owners
    @Query("SELECT o FROM Owner o WHERE o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<Owner> findRecentOwners(@Param("since") LocalDateTime since);

    // Owner with most animals
    @Query("SELECT o FROM Owner o ORDER BY SIZE(o.animals) DESC")
    Page<Owner> findOwnersOrderByAnimalCount(Pageable pageable);

    // Search by multiple criteria
    @Query("SELECT o FROM Owner o WHERE " +
            "(:firstName IS NULL OR o.firstName LIKE %:firstName%) AND " +
            "(:lastName IS NULL OR o.lastName LIKE %:lastName%) AND " +
            "(:email IS NULL OR o.email LIKE %:email%) AND " +
            "(:phone IS NULL OR o.phone LIKE %:phone%)")
    List<Owner> findByMultipleCriteria(@Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phone") String phone);

    // Pagination support
    Page<Owner> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    Page<Owner> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<Owner> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}