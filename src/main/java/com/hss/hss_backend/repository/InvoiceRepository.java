package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

       // Basic query methods
       List<Invoice> findByOwnerOwnerId(Long ownerId);

       @Query("SELECT i FROM Invoice i WHERE i.owner.clinic.clinicId = :clinicId")
       List<Invoice> findByOwnerClinicClinicId(@Param("clinicId") Long clinicId);

       List<Invoice> findByStatus(Invoice.Status status);

       List<Invoice> findByDateBetween(LocalDate startDate, LocalDate endDate);

       List<Invoice> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

       List<Invoice> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

       Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

       // Date range queries
       List<Invoice> findByDateAfter(LocalDate date);

       List<Invoice> findByDateBefore(LocalDate date);

       List<Invoice> findByDueDateBefore(LocalDate date);

       List<Invoice> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

       // Status and date combinations
       List<Invoice> findByStatusAndDateBetween(Invoice.Status status, LocalDate startDate, LocalDate endDate);

       List<Invoice> findByStatusAndDueDateBefore(Invoice.Status status, LocalDate date);

       // Custom queries with @Query
       @Query("SELECT i FROM Invoice i WHERE i.owner.ownerId = :ownerId AND i.status = :status")
       List<Invoice> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Invoice.Status status);

       @Query("SELECT i FROM Invoice i WHERE i.owner.ownerId = :ownerId AND i.date BETWEEN :startDate AND :endDate")
       List<Invoice> findByOwnerIdAndDateRange(@Param("ownerId") Long ownerId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < CURRENT_DATE")
       List<Invoice> findOverdueInvoices(@Param("status") Invoice.Status status);

       @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' AND i.dueDate < CURRENT_DATE")
       List<Invoice> findOverduePendingInvoices();

       @Query("SELECT i FROM Invoice i WHERE i.amount >= :minAmount AND i.amount <= :maxAmount")
       List<Invoice> findByAmountRange(@Param("minAmount") BigDecimal minAmount,
                     @Param("maxAmount") BigDecimal maxAmount);

       @Query("SELECT i FROM Invoice i WHERE i.paymentMethod = :paymentMethod")
       List<Invoice> findByPaymentMethod(@Param("paymentMethod") String paymentMethod);

       @Query("SELECT i FROM Invoice i WHERE i.owner.firstName LIKE %:name% OR i.owner.lastName LIKE %:name%")
       List<Invoice> findByOwnerNameContaining(@Param("name") String name);

       @Query("SELECT i FROM Invoice i WHERE i.owner.email = :email")
       List<Invoice> findByOwnerEmail(@Param("email") String email);

       @Query("SELECT i FROM Invoice i WHERE i.owner.phone = :phone")
       List<Invoice> findByOwnerPhone(@Param("phone") String phone);

       // Statistics queries
       @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
       Long countByStatus(@Param("status") Invoice.Status status);

       @Query("SELECT COUNT(i) FROM Invoice i WHERE i.owner.ownerId = :ownerId")
       Long countByOwnerId(@Param("ownerId") Long ownerId);

       @Query("SELECT i.status, COUNT(i) FROM Invoice i GROUP BY i.status")
       List<Object[]> getInvoiceCountByStatus();

       @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = :status")
       BigDecimal getTotalAmountByStatus(@Param("status") Invoice.Status status);

       @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.owner.ownerId = :ownerId")
       BigDecimal getTotalAmountByOwnerId(@Param("ownerId") Long ownerId);

       @Query("SELECT SUM(i.totalAmount - i.paidAmount) FROM Invoice i WHERE i.owner.ownerId = :ownerId AND i.dueDate < CURRENT_DATE AND i.status != 'PAID'")
       BigDecimal getOverdueAmountByOwnerId(@Param("ownerId") Long ownerId);

       @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.date BETWEEN :startDate AND :endDate")
       BigDecimal getTotalAmountInDateRange(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query("SELECT AVG(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID'")
       BigDecimal getAveragePaidInvoiceAmount();

       // Monthly and yearly statistics
       @Query("SELECT YEAR(i.date), MONTH(i.date), COUNT(i), SUM(i.totalAmount) FROM Invoice i " +
                     "WHERE i.date BETWEEN :startDate AND :endDate GROUP BY YEAR(i.date), MONTH(i.date) " +
                     "ORDER BY YEAR(i.date), MONTH(i.date)")
       List<Object[]> getMonthlyInvoiceStatistics(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query("SELECT YEAR(i.date), COUNT(i), SUM(i.totalAmount) FROM Invoice i " +
                     "WHERE i.date BETWEEN :startDate AND :endDate GROUP BY YEAR(i.date) " +
                     "ORDER BY YEAR(i.date)")
       List<Object[]> getYearlyInvoiceStatistics(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Top customers
       @Query("SELECT i.owner.ownerId, i.owner.firstName, i.owner.lastName, COUNT(i), SUM(i.totalAmount) " +
                     "FROM Invoice i GROUP BY i.owner.ownerId, i.owner.firstName, i.owner.lastName " +
                     "ORDER BY SUM(i.totalAmount) DESC")
       Page<Object[]> getTopCustomersByAmount(Pageable pageable);

       @Query("SELECT i.owner.ownerId, i.owner.firstName, i.owner.lastName, COUNT(i), SUM(i.totalAmount) " +
                     "FROM Invoice i GROUP BY i.owner.ownerId, i.owner.firstName, i.owner.lastName " +
                     "ORDER BY COUNT(i) DESC")
       Page<Object[]> getTopCustomersByInvoiceCount(Pageable pageable);

       // Pagination support
       Page<Invoice> findByOwnerOwnerId(Long ownerId, Pageable pageable);

       Page<Invoice> findByStatus(Invoice.Status status, Pageable pageable);

       Page<Invoice> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

       // Complex search
       @Query("SELECT i FROM Invoice i WHERE " +
                     "(:ownerId IS NULL OR i.owner.ownerId = :ownerId) AND " +
                     "(:status IS NULL OR i.status = :status) AND " +
                     "(:startDate IS NULL OR i.date >= :startDate) AND " +
                     "(:endDate IS NULL OR i.date <= :endDate) AND " +
                     "(:minAmount IS NULL OR i.totalAmount >= :minAmount) AND " +
                     "(:maxAmount IS NULL OR i.totalAmount <= :maxAmount)")
       Page<Invoice> findByMultipleCriteria(@Param("ownerId") Long ownerId,
                     @Param("status") Invoice.Status status,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("minAmount") BigDecimal minAmount,
                     @Param("maxAmount") BigDecimal maxAmount,
                     Pageable pageable);
}