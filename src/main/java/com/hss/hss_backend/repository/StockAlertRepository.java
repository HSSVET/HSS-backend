package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    List<StockAlert> findByStockProductProductId(Long productId);

    List<StockAlert> findByAlertType(StockAlert.AlertType alertType);

    List<StockAlert> findByIsResolved(Boolean isResolved);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.isResolved = false")
    List<StockAlert> findActiveAlerts();

    @Query("SELECT sa FROM StockAlert sa WHERE sa.stockProduct.productId = :productId AND sa.isResolved = false")
    List<StockAlert> findActiveAlertsByProductId(@Param("productId") Long productId);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.alertType = :alertType AND sa.isResolved = false")
    List<StockAlert> findActiveAlertsByType(@Param("alertType") StockAlert.AlertType alertType);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.stockProduct.productId = :productId AND sa.alertType = :alertType AND sa.isResolved = false")
    List<StockAlert> findActiveAlertsByProductAndType(@Param("productId") Long productId, 
                                                      @Param("alertType") StockAlert.AlertType alertType);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.expirationDate <= :date AND sa.isResolved = false")
    List<StockAlert> findExpiringAlerts(@Param("date") LocalDate date);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.expirationDate BETWEEN :startDate AND :endDate AND sa.isResolved = false")
    List<StockAlert> findExpiringAlertsBetween(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
}

