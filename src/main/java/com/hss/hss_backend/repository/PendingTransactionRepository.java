package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PendingTransactionRepository extends JpaRepository<PendingTransaction, Long> {

    // Find by owner
    List<PendingTransaction> findByOwnerOwnerIdOrderByCreatedAtDesc(Long ownerId);

    // Find pending transactions for an owner
    List<PendingTransaction> findByOwnerOwnerIdAndStatus(Long ownerId, PendingTransaction.TransactionStatus status);

    // Find by animal
    List<PendingTransaction> findByAnimalAnimalIdOrderByCreatedAtDesc(Long animalId);

    // Find pending transactions for an animal
    List<PendingTransaction> findByAnimalAnimalIdAndStatus(Long animalId, PendingTransaction.TransactionStatus status);

    // Find by appointment
    Optional<PendingTransaction> findByAppointmentAppointmentId(Long appointmentId);

    // Find by status
    List<PendingTransaction> findByStatus(PendingTransaction.TransactionStatus status);

    // Find by clinic
    List<PendingTransaction> findByClinicClinicIdOrderByCreatedAtDesc(Long clinicId);

    // Find pending transactions by clinic
    List<PendingTransaction> findByClinicClinicIdAndStatus(Long clinicId, PendingTransaction.TransactionStatus status);

    // Find or create for owner
    @Query("SELECT pt FROM PendingTransaction pt WHERE pt.owner.ownerId = :ownerId AND pt.status = 'PENDING' ORDER BY pt.createdAt DESC")
    List<PendingTransaction> findActivePendingByOwner(@Param("ownerId") Long ownerId);

    // Find or create for animal
    @Query("SELECT pt FROM PendingTransaction pt WHERE pt.animal.animalId = :animalId AND pt.status = 'PENDING' ORDER BY pt.createdAt DESC")
    List<PendingTransaction> findActivePendingByAnimal(@Param("animalId") Long animalId);

    // Get total pending amount for owner
    @Query("SELECT COALESCE(SUM(pt.totalAmount - pt.paidAmount - pt.discountAmount), 0) FROM PendingTransaction pt WHERE pt.owner.ownerId = :ownerId AND pt.status IN ('PENDING', 'PARTIAL_PAID')")
    BigDecimal getTotalPendingAmountByOwner(@Param("ownerId") Long ownerId);

    // Get total pending amount for clinic
    @Query("SELECT COALESCE(SUM(pt.totalAmount - pt.paidAmount - pt.discountAmount), 0) FROM PendingTransaction pt WHERE pt.clinic.clinicId = :clinicId AND pt.status IN ('PENDING', 'PARTIAL_PAID')")
    BigDecimal getTotalPendingAmountByClinic(@Param("clinicId") Long clinicId);

    // Count pending transactions
    @Query("SELECT COUNT(pt) FROM PendingTransaction pt WHERE pt.clinic.clinicId = :clinicId AND pt.status = 'PENDING'")
    Long countPendingByClinic(@Param("clinicId") Long clinicId);
}
